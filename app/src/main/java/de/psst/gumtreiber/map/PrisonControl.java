package de.psst.gumtreiber.map;

import android.widget.TextView;

import java.util.ArrayList;

import de.psst.gumtreiber.data.AbstractUser;
import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.Vector2;

import static de.psst.gumtreiber.map.MapControl.MAX_LAT;
import static de.psst.gumtreiber.map.MapControl.MAX_LONG;
import static de.psst.gumtreiber.map.MapControl.MIN_LAT;
import static de.psst.gumtreiber.map.MapControl.MIN_LONG;

/**
 * {@link PrisonControl} manages all users not within the boundaries of the map
 */
public class PrisonControl {
    // TODO adjust both constants according to the self drawn map and testing
    private static final double PRISON_LATITUDE = 51.022255;
    private static final double PRISON_LONGITUDE = 7.560842;
    private static int PRISON_COUNT = 7; //#users to show in the prison

    private final TextView view;
    private MapControl mapControl;
    private ArrayList<AbstractUser> inmates = new ArrayList<>();
    private ArrayList<AbstractUser> freeFolk = new ArrayList<>();
    private ArrayList<String> shownInmates = new ArrayList<>();

    public PrisonControl(TextView view) {
        this.view = view;
    }

    public void setMapControl(MapControl mapControl) {
        this.mapControl = mapControl;
    }

    /**
     * Separate all Users from the given ArrayList which aren't
     * in the geographically area of the map and show them in the {@link #view}
     *
     * @param users ArrayList to filter
     * @return userList without inmates
     */
    public ArrayList<AbstractUser> updateInmates(ArrayList<AbstractUser> users) {
        filterList(users);
        updateView();

        return freeFolk;
    }

    /**
     * Update the location {@link #PRISON_LONGITUDE} and {@link #PRISON_LATITUDE}
     * of the prison based of the current zoom of the map
     */
    public void updateLocation() {
        // adjust to map coordinates
        Vector2 mapPos = mapControl.gpsToMap(new Coordinate(PRISON_LATITUDE, PRISON_LONGITUDE));

        // consider possible translation
        mapPos = mapControl.getMap().adjustToTransformation(mapPos);

        // set the new location
        view.setX(mapPos.x);
        view.setY(mapPos.y);
    }

    /**
     * Separate all Users from the given ArrayList
     * which aren't in the geographically area of the map
     * and save them in their own {@link #inmates} list.
     *
     * @param users ArrayList to filter
     */
    private void filterList(ArrayList<AbstractUser> users) {
        inmates.clear();
        for (int i = users.size() - 1; i >= 0; i--) {
            AbstractUser u = users.get(i);

            if (notOnMap(u.getLatitude(), u.getLongitude())) {
                inmates.add(u);

                if (u.getMarker() != null) {
                    u.getMarker().setVisibility(false);
                    u.getMarker().setAlreadyDrawn(false);
                }

                users.remove(u);
            }
        }

        freeFolk = users;
    }

    /**
     * @return true, if the given user is not in the area of the {@link MapView}
     */
    public static boolean notOnMap(double latitude, double longitude) {
        return latitude > MAX_LAT || latitude < MIN_LAT
                || longitude > MAX_LONG || longitude < MIN_LONG;
    }

    /**
     * Fill the {@link #view} with a maximum of {@link #PRISON_COUNT} users from {@link #inmates}
     */
    private void updateView() {
        shownInmates.clear();

        // add at most #PRISON_COUNT people to the shownInmates list
        if (inmates.size() < PRISON_COUNT)
            for (AbstractUser u : inmates) shownInmates.add(u.getName());
        else {
            // only show the first nine users TODO maybe pic unique random users
            for (int i = 0; i < PRISON_COUNT - 1; i++) {
                shownInmates.add(inmates.get(i).getName());
            }
        }

        StringBuilder sb = new StringBuilder();

        // build a String to show inmates in TextView
        for (String name : shownInmates) {
            sb.append(name);
            sb.append(" ");
        }

        view.setText(sb);
    }
}
