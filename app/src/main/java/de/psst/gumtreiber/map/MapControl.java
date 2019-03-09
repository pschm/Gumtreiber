package de.psst.gumtreiber.map;

import android.app.Activity;
import android.location.Location;
import android.util.Log;

import java.util.ArrayList;

import de.psst.gumtreiber.data.AbstractUser;
import de.psst.gumtreiber.data.Bot;
import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.data.UserFilter;
import de.psst.gumtreiber.data.Vector2;

public class MapControl {
    // constants for gps calculation
    final static double MAX_LAT = 51.029673; //51.027653;
    final static double MIN_LAT = 51.019053; //51.020989;
    final static double MAX_LONG = 7.567960; //7.566508;
    final static double MIN_LONG = 7.559551; //7.560669;
    private final static double DELTA_LAT = (MAX_LAT - MIN_LAT) * 1000000;
    private final static double DELTA_LONG = (MAX_LONG - MIN_LONG) * 1000000;
    // TODO Maybe Use Location instead of Coordinate
    private final static Coordinate MAIN_BUILDING_GPS = new Coordinate(51.022029, 7.561740);
    public final static Vector2 MAIN_BUILDING_MAP = new Vector2(320.97003f, 1762.0068f);
    private final static float INITIAL_ZOOM_X_OFFSET = -75;
    private final static float INITIAL_ZOOM_Y_OFFSET = 160;

    public final static int BOX_SIZE = 100;

    private MapView map;
    private ArrayList<String> friends = new ArrayList<>();
    private Activity activity;
    private PrisonControl prisonControl;
    private ArrayList<AbstractUser> users = new ArrayList<>();
    private Coordinate currentUserLocation = new Coordinate(0, 0);
    private boolean initialized = false;

    private ArrayList<OnMapInitialized> listeners = new ArrayList<>();

    public MapControl(MapView map, Activity activity, PrisonControl prisonControl) {
        this.map = map;
        this.activity = activity;
        this.prisonControl = prisonControl;

        map.setMapControl(this);
        map.setPrisonControl(prisonControl);
        prisonControl.setMapControl(this);

        MovableMarker.setMapView(map);
    }

    /**
     * @return the MapView which is managed by this MapControl
     */
    public MapView getMap() {
        return map;
    }


    public void setUpInitialZoomOnUser() {
        Vector2 pos;

        if (PrisonControl.notOnMap(currentUserLocation.getLatitude(), currentUserLocation.getLongitude())) {
//            Log.d("MapControl - pschm", "user NOT on the map!" + currentUserLocation.getLatitude() + "/" + currentUserLocation.getLongitude());
//            pos = gpsToMap(currentUserLocation);
//            Log.d("MapControl - pschm", "user NOT on the map!" + pos);
            pos = MAIN_BUILDING_MAP; // gpsToMap(MAIN_BUILDING_GPS);
        } else {
            pos = gpsToMap(currentUserLocation);
            pos.x += INITIAL_ZOOM_X_OFFSET;
            pos.y += INITIAL_ZOOM_Y_OFFSET;
//            Log.d("MapControl - pschm", "user on the map!" + pos);
        }
        Log.d("MapControl", "DrawMat: " + map.getZoomControl().getDrawMatrix());
        map.getZoomControl().setScale(MapView.INITIAL_ZOOM, pos.x, pos.y, true);
        Log.d("MapControl", "DrawMat: " + map.getZoomControl().getDrawMatrix());
    }

    /**
     * Update the user list and show the new filtered users on the map
     * @param users new user list
     */
    public void updateUsers(ArrayList<AbstractUser> users) {
        Log.d("timing", "MapControl - updateUsers");
        if (!initialized) {
            setUpInitialZoomOnUser();
            Log.d("MapControl", "init zoom");
//            initialized = true;
        }

        // filter users according to the selected filters
        this.users = UserFilter.filterUsers(users);

        // filter users not on the map
        this.users = prisonControl.updateInmates(this.users);

        // merge close users in Groups
        this.users = buildUserGroups(this.users, BOX_SIZE); // comment if you want to use dynamicGroups


//        MovableMarker marker = new MovableMarker(activity, "Hier bin ich!");
//        marker.setVisibility(true);
//        marker.setPosition(250, 250);


        for (AbstractUser u : this.users) {
            Log.d("MapControl", u.getName() + ": " + u.getLatitude() + "/" + u.getLongitude());
        }

        // show users on the map
        // new ArrayList is needed to avoid ConcurrentModificationExceptions
        map.setMarkers(new ArrayList<>(this.users));

        if (!initialized) {
            // inform listener that the map is initialized
            initialized = true;
            for (OnMapInitialized l : listeners)
                l.onMapInitialized();
        }
        Log.d("timing", "MapControl - finished");
    }

    /**
     * Merge multiple geographically close users to a group
     * As well as add MovableMarkers to each user/group
     *
     * @param boxSize distance before users are merged
     */
    public ArrayList<AbstractUser> buildUserGroups(ArrayList<AbstractUser> users, int boxSize) {
        // reduce gps coordinates to the area (GM)
        int xSize = (int) DELTA_LONG;
        int ySize = (int) DELTA_LAT;
        Vector2 pos;

        // create a grid to detect close users
        AbstractUser[][] map = new AbstractUser[xSize / boxSize][ySize / boxSize];

        // list for all merged users
        ArrayList<User> mergedUserList = new ArrayList<>();

        // sort all users in the grid
        for (int i = 0; i < users.size(); i++) {
            AbstractUser u = users.get(i);

//            // TODO implement to hide expired users
//            // TODO check remove functions --> loop backwards through the array?
//            if (!u.isVisible()) {
//                if (u.getMarker() != null) u.getMarker().setVisibility(false);
//                users.remove(i);
//                continue;
//            }

            // transform user coordinates to the area
            pos = gpsToMap(new Coordinate(u.getLatitude(), u.getLongitude()));

            // calc grid position
            pos.x /= boxSize;
            pos.y /= boxSize;

            // load possible users already in this grid sector
            AbstractUser sector = map[(int) pos.x][(int) pos.y];

            if (sector == null) {

                // u is the first user in this sector
                if (u.getMarker() == null || !initialized)
                    u.setMarker(new MovableMarker(activity, u.getName()));
                u.getMarker().changeLabel(u.getName()); // needed if a user moves out of a group
                // change the look of the marker, if the user is a friend or bot
                if (u instanceof Bot) u.getMarker().changeLook(MovableMarker.Look.BOT);
                if (friends.contains(u.getUid())) {
                    u.getMarker().changeLook(MovableMarker.Look.FRIEND);
                }

                map[(int) pos.x][(int) pos.y] = u;
            } else if (sector.getUid() != null) {
                // u is the second user in this sector
                // delete both users from list and build a group
                users.remove(sector);
                users.remove(u);

                // hide label if existing
                if (u.getMarker() != null) u.getMarker().setVisibility(false);

                // reduce index according to deleted users
                i -= 2;

                // create new "User-Group"
                User mergedUsers = new User(null, "2");
                mergedUsers.setMarker(sector.getMarker());
                mergedUsers.getMarker().changeLabel("2");
                mergedUsers.getMarker().changeLook(MovableMarker.Look.DEFAULT);
                mergedUsers.setLatitude(sector.getLatitude());
                mergedUsers.setLongitude(sector.getLongitude());

                // save merge user to the grid
                map[(int) pos.x][(int) pos.y] = mergedUsers;
                mergedUserList.add(mergedUsers);

            } else {
                // u is at least the third user in this sector
                // remove u from the userList
                users.remove(u);

                // hide label if existing
                if (u.getMarker() != null) u.getMarker().setVisibility(false);

                // reduce index according to the deleted user
                i--;

                // increase the user counter
                String label = "" + (Integer.parseInt(sector.getName()) + 1);
                sector.setName(label);
                sector.getMarker().changeLabel(label);
            }
        }

        // add all mergedUsers to the list
        users.addAll(mergedUserList);
        return users;
    }

    /**
     * Calculates the given coordinate from GPS Position to map coordinates
     * and returns it
     */
    public Vector2 gpsToMap(Coordinate pos) {
        // limit gps to the relevant ares
        pos.setLatitude((pos.getLatitude() - MIN_LAT) * 1000000); // value between 0-4917
        pos.setLongitude((pos.getLongitude() - MIN_LONG) * 1000000); // value between 0-6596

        // invert y-Axis (other coordinate system)
        pos.setLatitude(DELTA_LAT - pos.getLatitude());

        // calc x,y values according to screen size
        Vector2 mapPos = new Vector2();
        mapPos.x = (float) (pos.getLongitude() * (map.getWidth() / DELTA_LONG));
        mapPos.y = (float) (pos.getLatitude() * (map.getHeight() / DELTA_LAT));

        return mapPos;
    }

    public void updateFriends(ArrayList<String> friendList) {
        friends = friendList;
    }

    public void updateCurrentUserLocation(Location location) {
        if (location != null)
            currentUserLocation.setLocation(location.getLatitude(), location.getLongitude());
    }

    /**
     * Listen on this interface to receive an update if the map is initialized
     */
    public interface OnMapInitialized {
        void onMapInitialized();
    }

    /**
     * Add a listener who will listen on map init
     *
     * @param listener The listener to be added
     */
    public void addOnMapInitializedListener(OnMapInitialized listener) {
        if (listeners == null) listeners = new ArrayList<>();
        listeners.add(listener);
    }

    /**
     * Remove a listener who is listen on map init
     *
     * @param listener The listener to be removed
     */
    public void removeOnMapInitializedListener(OnMapInitialized listener) {
        if (listener == null) return;
        listeners.remove(listener);
    }
}
