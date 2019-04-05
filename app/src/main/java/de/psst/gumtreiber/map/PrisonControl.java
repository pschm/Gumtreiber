package de.psst.gumtreiber.map;

import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.AbstractUser;
import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.Vector2;
import de.psst.gumtreiber.ui.fragments.MapFragment;

import static de.psst.gumtreiber.map.MapControl.MAX_LAT;
import static de.psst.gumtreiber.map.MapControl.MAX_LONG;
import static de.psst.gumtreiber.map.MapControl.MIN_LAT;
import static de.psst.gumtreiber.map.MapControl.MIN_LONG;

/**
 * {@link PrisonControl} manages all users not within the boundaries of the map
 */
public class PrisonControl {
    private static final String CLASS = "MapControl ";
    private static final String USER = "pschm";

    private static final double PRISON_LATITUDE = 51.024564; // Auf der Platte
    private static final double PRISON_LONGITUDE = 7.561225;
    private static int PRISON_COUNT = 3; //#users to show in the prison

    private final TextView view;
    private MapControl mapControl;
    private ArrayList<AbstractUser> inmates = new ArrayList<>();
    private ArrayList<AbstractUser> freeFolk = new ArrayList<>();
    private ArrayList<String> shownInmates = new ArrayList<>();

    private int initialWidth;
    private int initialHeight;
    private float initialTextSize;

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

        if (MapFragment.isUiThread()) updateView();
        else mapControl.getActivity().runOnUiThread(this::updateView);

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
        mapPos = mapControl.getMapView().adjustToTransformation(mapPos);

        // set the new location
        view.setX(mapPos.x - view.getWidth() / 2f);
        view.setY(mapPos.y - view.getHeight() / 2f);

        // adjust size
        if (MapFragment.isUiThread()) adjustSize();
        else mapControl.getActivity().runOnUiThread(this::adjustSize);
    }

    /**
     * adjust the size of the TextView according to the current scale of {@link MapView}
     */
    private void adjustSize() {
        float scale = mapControl.getMapView().getScale();
        if (initialWidth <= 0 || initialHeight <= 0) {
            Log.w(CLASS + USER, "size not initialized...");
            return;
        }
//        Log.d(CLASS + USER, "Adjust to scale: " + scale);

        int width = (int) (initialWidth * scale);
        int height = (int) (initialHeight * scale);
        LayoutParams param = new ConstraintLayout.LayoutParams(
                (int) (initialWidth * scale),
                (int) (initialHeight * scale)
        );
        view.setMaxWidth(width);
        view.setMinWidth(width);
        view.setMaxHeight(height);
        view.setMinHeight(height);

        view.setLayoutParams(param);

//        Log.d(CLASS + USER, "Height: " + view.getHeight());

        // textSize
        view.setTextSize(initialTextSize * scale);
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
            // only show the first #PRISON_COUNT
            for (int i = 0; i < PRISON_COUNT; i++) {
                shownInmates.add(inmates.get(i).getName());
            }
        }

        if (shownInmates.isEmpty()) {
            view.setText(mapControl.getActivity().getString(R.string.prison_empty));
            return;
        }

        StringBuilder sb = new StringBuilder();

        // counter
        sb.append("\n");
        sb.append(inmates.size());
        sb.append(" Insassen");
        sb.append("\n");

        // build a String to show inmates in TextView
        for (String name : shownInmates) {
            sb.append("- ");
            sb.append(name);
            sb.append(" \n");
        }

        sb.append("...");

//        Log.d(CLASS+USER, "Text: " + sb);
        view.setText(sb);
    }

    public void initSize() {
        if (view.getWidth() <= 0) Log.w(CLASS + USER, "Prison not initialized");
        initialWidth = 100;//view.getWidth();
        initialHeight = 109;//view.getHeight();
        initialTextSize = 4f;// view.getTextSize();
        Log.d(CLASS + USER, "standard text size: " + initialTextSize);
        adjustSize();
    }
}
