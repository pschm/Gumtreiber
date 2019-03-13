package de.psst.gumtreiber.map;

import android.app.Activity;
import android.location.Location;
import android.util.Log;
import android.widget.ImageView;

import java.util.ArrayList;

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

    private final static Coordinate MAIN_BUILDING_GPS = new Coordinate(51.022029, 7.561740);
    public final static Vector2 MAIN_BUILDING_MAP = new Vector2(320.97003f, 1762.0068f);

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
            Log.d(CLASS + USER, "user on the map!" + pos);
        }

//        Log.d("MapControl", "DrawMat: " + map.getZoomControl().getDrawMatrix());
//        map.getZoomControl().setScale(MapView.INITIAL_ZOOM, pos.x, pos.y, true);
//        Log.d("MapControl", "DrawMat: " + map.getZoomControl().getDrawMatrix());
//        map.getZoomControl().setScale(MapView.INITIAL_ZOOM);
//        map.getZoomControl().setScale(MapView.INITIAL_ZOOM, MapView.DEBUG_X, MapView.DEBUG_Y, false);

//        Matrix m = viewAttacher.getDisplayMatrix();
//        Log.d("MapControl - pschm", "ini Mat. "  +m);
//        m.setScale(MapView.INITIAL_ZOOM, MapView.INITIAL_ZOOM);
//        Vector2 vec = map.adjustToTransformation(new Vector2(MapView.DEBUG_X, MapView.DEBUG_Y), m);
//        m.setScale(MapView.INITIAL_ZOOM, MapView.INITIAL_ZOOM, MapView.DEBUG_X, MapView.DEBUG_Y);

//        viewAttacher.setScale(MapView.INITIAL_ZOOM, MapView.DEBUG_X, MapView.DEBUG_Y, false);

//        RectF r = viewAttacher.getDisplayRect();
//        Log.d("MapControl", "DisplayRect: "+r); // left, top, right, bottom
//        float currentX = rect.

//        Log.d("MapControl - pschm", "Zoom Mat. " + m);

//        viewAttacher.setDisplayMatrix(m);

//        Log.d("MapControl - pschm", "Zoom Mat. " + viewAttacher.getDisplayMatrix());
//        Log.d("MapControl - pschm", "Zoom Mat. " + viewAttacher.getDisplayRect());
//        Log.d("MapControl - pschm", "---------------------------------------");


        // neue Theorie -> erst linke ecke Zoomen und anschließend manuell translatieren
        // zoom nach center
//        int width = map.getWidth();
//        int height = map.getHeight();
//
//        Vector2 center = new Vector2(width / 2f, height / 2f);
//
//        // zur mitte zentrieren
//        viewAttacher.setScale(INITIAL_ZOOM, center.x, center.y, false);
//
//        // zum punkt bewegen
//        Vector2 transform = Vector2.sub(DEBUG_POINT, center);
//
//        Matrix m = viewAttacher.getDisplayMatrix();
//        m.preTranslate(transform.x, transform.y);
//        viewAttacher.setDisplayMatrix(m);

//        Log.d(CLASS+USER, ((View) map.getParent()).getWidth() + "");
//        Rect r = map.getDrawable().getBounds();
//        int w = ((BitmapDrawable)map.getDrawable()).getBitmap().getWidth();
//
//        int intW = map.getDrawable().getIntrinsicWidth();
//        Log.d(CLASS+USER, r.width() + " drawable");
//        Log.d(CLASS+USER, w + " bitmap");
//        Log.d(CLASS+USER, intW + " drawable Intrisinic");
//        Log.d(CLASS+USER, map.getMeasuredWidth() + " drawable Intrisinic");
//
//
//        int height = ((View) map.getParent()).getHeight();
//        int width = ((View) map.getParent()).getWidth();
//
//        int mHeight = ((View) map.getParent()).getMeasuredHeight();
//        int mWidth = ((View) map.getParent()).getMeasuredWidth();
//
//        Log.d(CLASS+USER, "w/h: "+width+"/"+height + " - mW/H: "+mWidth+"/"+mHeight);
//


        mapView.setScaleType(ImageView.ScaleType.CENTER_INSIDE);
//        viewAttacher.setScale(INITIAL_ZOOM, DEBUG_X, DEBUG_Y, false);
        mapView.setScale(INITIAL_ZOOM, pos.x, pos.y, false);
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
        mapPos.x = (float) (pos.getLongitude() * (mapView.getWidth() / DELTA_LONG));
        mapPos.y = (float) (pos.getLatitude() * (mapView.getHeight() / DELTA_LAT));

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
