package de.psst.gumtreiber.map;

import android.app.Activity;
import android.util.Log;

import java.util.ArrayList;

import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.data.UserFilter;
import de.psst.gumtreiber.data.Vector2;

public class MapControl {
    // constants for gps calculation
    final static double MAX_LAT = 51.027653;
    final static double MIN_LAT = 51.020989;
    final static double MAX_LONG = 7.566508;
    final static double MIN_LONG = 7.560669;
    private final static double DELTA_LAT = (MAX_LAT - MIN_LAT) * 1000000;
    private final static double DELTA_LONG = (MAX_LONG - MIN_LONG) * 1000000;

    // TODO ggf. vom aktuellen zoomfaktor abhängig machen --> könnte performanceprobleme verursachen
    private final static int BOX_SIZE = 200;

    private MapView map;
    private Activity activity;
    private PrisonControl prisonControl;
    private ArrayList<User> users = new ArrayList<>();
    private UserFilter filter = new UserFilter();

    public MapControl(MapView map, Activity activity, PrisonControl prisonControl) {
        this.map = map;
        this.activity = activity;
        this.prisonControl = prisonControl;

        map.setMapControl(this);
        map.setPrisonControl(prisonControl);
        prisonControl.setMapControl(this);
    }

    /**
     * @return the MapView which is managed by this MapControl
     */
    public MapView getMap() {
        return map;
    }

    /**
     * @return the UserFilter used to filter all users before shown
     */
    public UserFilter getFilter() {
        return filter;
    }

    /**
     * Update the user list and show the new filtered users on the map
     * @param users new user list
     */
    public void updateUsers(ArrayList<User> users) {
        // filter users according to the selected filters
        this.users = filter.filterUsers(users);

        // filter users not on the map
        this.users = prisonControl.updateInmates(this.users);

        // merge close users in Groups
        buildUserGroups(BOX_SIZE);

        // show users on the map
        map.setMarkers(this.users);
    }

    /**
     * Merge multiple geographically close users to a group
     * As well as add MovableMarkers to each user/group
     *
     * @param boxSize distance before users are merged
     */
    public void buildUserGroups(int boxSize) {
        // reduce gps coordinates to the area (GM)
        int xSize = (int) DELTA_LONG;
        int ySize = (int) DELTA_LAT;
        double x, y;

        // create a grid to detect close users
        User[][] map = new User[xSize / boxSize][ySize / boxSize];

        // list for all merged users
        ArrayList<User> mergedUserList = new ArrayList<>();

        // sort all users in the grid
        for (int i = 0; i < users.size(); i++) {
            User u = users.get(i);

            // transform user coordinates to the area
            x = (u.longitude - MIN_LONG) * 1000000; // min/max: 0268-6864 -> 0-6596
            y = (u.latitude - MIN_LAT) * 1000000; // min/max: 1335-6252 -> 0-4917
            y = DELTA_LAT - y; // invert y-Axis

            // calc grid position
            x /= boxSize;
            y /= boxSize;

            // load possible users already in this grid sector
            User sector = map[(int) x][(int) y];

            if (sector == null) {
                // u is the first user in this sector
                if (u.getMarker() == null) {
                    if (activity == null) Log.w("MapView", "Activity not given");
                    u.setMarker(new MovableMarker(activity, u.name));
                }
                u.getMarker().changeLabel(u.name); // needed if a user moves out of a group
                map[(int) x][(int) y] = u;
            } else if (sector.uid != null) {
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
                mergedUsers.latitude = u.latitude;
                mergedUsers.longitude = u.longitude;

                // save merge user to the grid
                map[(int) x][(int) y] = mergedUsers;
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
                String label = "" + (Integer.parseInt(sector.name) + 1);
                sector.name = label;
                sector.getMarker().changeLabel(label);
            }
        }

        // add all mergedUsers to the list
        users.addAll(mergedUserList);
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
}
