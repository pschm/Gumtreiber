package de.psst.gumtreiber.map;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

import androidx.annotation.Nullable;
import de.psst.gumtreiber.data.AbstractUser;
import de.psst.gumtreiber.data.Bot;
import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.data.UserFilter;
import de.psst.gumtreiber.data.Vector2;

import static de.psst.gumtreiber.map.MapView.INITIAL_ZOOM;

public class MapControl {
    private static final String CLASS = "MapControl ";
    private static final String USER = "pschm";


    // constants for gps calculation
    final static double MAX_LAT = 51.029673;
    final static double MIN_LAT = 51.019053;
    final static double MAX_LONG = 7.567960;
    final static double MIN_LONG = 7.559551;
    private final static double DELTA_LAT = (MAX_LAT - MIN_LAT) * 1000000;
    private final static double DELTA_LONG = (MAX_LONG - MIN_LONG) * 1000000;

    private final static Coordinate MAIN_BUILDING_GPS = new Coordinate(51.022915, 7.562027);
    private final static Vector2 MAIN_BUILDING_MAP = new Vector2(278.54633f, 1203.9677f);

    private final static int BOX_SIZE = 75;

    private MapView mapView;
    private ArrayList<String> friends = new ArrayList<>();
    private Activity activity;
    private PrisonControl prisonControl;
    private ArrayList<AbstractUser> users = new ArrayList<>();
    private Coordinate currentUserLocation = new Coordinate(0, 0);
    private boolean initialized = false;

    private ArrayList<OnMapInitialized> listeners = new ArrayList<>();

    public MapControl(MapView map, Activity activity, PrisonControl prisonControl) {
        this.mapView = map;
        this.activity = activity;
        this.prisonControl = prisonControl;

        mapView.setMapControl(this);
        mapView.setPrisonControl(prisonControl);
        prisonControl.setMapControl(this);

        MovableMarker.setMapView(mapView);
    }

    /**
     * @return the MapView which is managed by this MapControl
     */
    public MapView getMapView() {
        return mapView;
    }


    private void setUpInitialZoomOnUser() {
        Vector2 pos;
//        Log.d(CLASS + USER, "W/H: "+map.getWidth()+"/"+map.getHeight());
        if (PrisonControl.notOnMap(currentUserLocation.getLatitude(), currentUserLocation.getLongitude())) {
            Log.d(CLASS + USER, "user NOT on the map!" + currentUserLocation);
            pos = MAIN_BUILDING_MAP; // gpsToMap(MAIN_BUILDING_GPS);
        } else {
            pos = gpsToMap(currentUserLocation);
            Log.d(CLASS + USER, "user on the map!" + pos + " GPS " + currentUserLocation);
        }

        mapView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
        mapView.setScale(INITIAL_ZOOM, pos.x, pos.y, false);
//        mapView.setScale(INITIAL_ZOOM, DEBUG_X, DEBUG_Y, false);
    }

    /**
     * Update the user list and show the new filtered users on the mapView
     * @param users new user list
     */
    public void updateUsers(ArrayList<AbstractUser> users) {
        if (!initialized) setUpInitialZoomOnUser();

        // filter users according to the selected filters
        this.users = UserFilter.filterUsers(users);

        // filter users not on the mapView
        this.users = prisonControl.updateInmates(this.users);

        // merge close users in Groups
        this.users = buildUserGroups(this.users, BOX_SIZE); // comment if you want to use dynamicGroups

        // show users on the mapView
        // new ArrayList is needed to avoid ConcurrentModificationExceptions
        mapView.setMarkers(new ArrayList<>(this.users));

        mapView.setTouchable();
        if (!initialized) {
            // inform listener that the map is initialized
            initialized = true;
            for (OnMapInitialized l : listeners) l.onMapInitialized();
        }
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

        // TODO wenn merge, dann marker sagen, dass er setPosition nutzen soll
        // TODO rasterkoordinaten

        if (activity == null) {
            Log.w("MapControl", "Activity is NULL!!");
            return new ArrayList<>();
        }

        // create a grid to detect close users
        AbstractUser[][] map = new AbstractUser[xSize / boxSize][ySize / boxSize];

        // list for all merged users
        ArrayList<User> mergedUserList = new ArrayList<>();

        // sort all users in the grid
        for (int i = 0; i < users.size(); i++) {
            AbstractUser u = users.get(i);

//            // TODO implement to hide expired users
//            if (!u.isVisible()) {
//                if (u.getMarker() != null) u.getMarker().setVisibility(false);
//                users.remove(i);
//                i--;
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

                map[(int) pos.x][(int) pos.y] = u;
            } else if (sector.getUid() != null) {
                // u is the second user in this sector
                // delete both users from list and build a group
                users.remove(sector);
                users.remove(u);

                // hide label if existing
                if (u.getMarker() != null) {
                    u.getMarker().setVisibility(false);
                    u.getMarker().setAlreadyDrawn(false);
                }

                // reduce index according to deleted users
                i -= 2;

                // create new "User-Group"
                User mergedUsers = new User(null, "2");
                mergedUsers.setMarker(sector.getMarker());
                mergedUsers.setLatitude(sector.getLatitude());
                mergedUsers.setLongitude(sector.getLongitude());
                mergedUsers.getMarker().setAlreadyDrawn(false); // merged users should not move

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
            }
        }

        // add all mergedUsers to the list
        users.addAll(mergedUserList);

        // update label names and color
        for (AbstractUser u : users) {
            // set marker label
            u.getMarker().changeLabel(u.getName());

            // make sure every marker is shown
            u.getMarker().setVisibility(true);

            // change color of the marker depending on friend/bot/other
            if (u.getUid() == null)
                u.getMarker().changeLook(MovableMarker.Look.DEFAULT);
            else if (u instanceof Bot)
                u.getMarker().changeLook(MovableMarker.Look.BOT);
            else if (friends.contains(u.getUid()))
                u.getMarker().changeLook(MovableMarker.Look.FRIEND);
            else
                u.getMarker().changeLook(MovableMarker.Look.DEFAULT);
        }

        return users;
    }

    /**
     * Calculates the given coordinate from GPS Position to mapView coordinates
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
        mapPos.x = (float) (pos.getLongitude() * (mapView.getMapViewWidth() / DELTA_LONG));
        mapPos.y = (float) (pos.getLatitude() * (mapView.getMapViewHeight() / DELTA_LAT));

        return mapPos;
    }

    public void updateFriends(ArrayList<String> friendList) {
        friends = friendList;
    }

    /**
     * Update the position of the current active user
     *
     * @param location current location of the user, if null the user is positioned outside the map
     */
    public void updateCurrentUserLocation(@Nullable Location location) {
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
