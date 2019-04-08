package de.psst.gumtreiber.map;

import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Typeface;
import android.util.Log;
import android.view.ViewGroup.LayoutParams;
import android.widget.TextView;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.constraintlayout.widget.ConstraintLayout;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
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
    private static final String CLASS = "PrisonControl ";
    private static final String USER = "pschm";

    private static final double PRISON_LATITUDE = 51.024707; // 51.024564; // Auf der Platte
    private static final double PRISON_LONGITUDE = 7.560826; // 7.561225;
    private static int PRISON_COUNT = 3; //#users to show in the prison

    private Vector2 position = new Vector2(0, 0);
    private Paint paint = new Paint();
    private float scale = MapView.INITIAL_ZOOM;

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

    public void initTextLooks() {
        // init text color and type
        Typeface tf = ResourcesCompat.getFont(mapControl.getActivity(), R.font.almendra_sc);
        paint.setTypeface(tf);
        paint.setColor(ContextCompat.getColor(mapControl.getActivity(), R.color.colorAuthFont));
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
        scale = mapControl.getMapView().getScale();

        // adjust to map coordinates
        position = mapControl.gpsToMap(new Coordinate(PRISON_LATITUDE, PRISON_LONGITUDE));

        // consider possible translation
        position = mapControl.getMapView().adjustToTransformation(position);

        // set the new location
        view.setX(position.x - view.getWidth() / 2f);
        view.setY(position.y - view.getHeight() / 2f);

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

    public void paintInmates(Canvas canvas) {
        // set text size according to zoom
        paint.setTextSize(scale * 9f);

        float stepSize = 15f;
        for (int i = 0; i < shownInmates.size(); i++) {
            canvas.drawText(
                    shownInmates.get(i),
                    position.x,
                    position.y + i * stepSize * scale,
                    paint
            );
        }
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

        // prison is empty
        if (inmates.isEmpty()) {
            shownInmates.add(mapControl.getActivity().getString(R.string.prison_empty));

            view.setText(mapControl.getActivity().getString(R.string.prison_empty));
            return;
        }

        // remember if all inmates are shown in prison or not
        boolean allAdded = false;

        // add header text
        shownInmates.add(inmates.size() + " Insassen");

        // #prison members less than the PRISON_COUNT --> add all
        if (inmates.size() < PRISON_COUNT) {
            for (AbstractUser u : inmates) shownInmates.add("- " + u.getName());
            allAdded = true;
        }
        else {

            // check if the own user is in prison
            // in that case, show him for sure
            FirebaseUser fb = FirebaseAuth.getInstance().getCurrentUser();
            String myName = null;

            if (fb != null) {
                String myUid = fb.getUid();

                for (AbstractUser u : inmates) {
                    if (u.getUid().equals(myUid)) {
                        myName = u.getName();
                        shownInmates.add("- " + myName);
                        break;
                    }
                }
            }

            // only show the first #PRISON_COUNT
            boolean skipped = false;
            for (int i = 0; i < PRISON_COUNT; i++) {
                // skip own name, if already added
                if (inmates.get(i).getName().equals(myName)) {
                    skipped = true;
                    continue;
                }
                if (!skipped && myName != null && i == PRISON_COUNT - 1) {
                    // skip last draw
                    break;
                }
                shownInmates.add("- " + inmates.get(i).getName());
            }
        }

        StringBuilder sb = new StringBuilder();

        // build text for the counter
        sb.append("\n");
        sb.append(inmates.size());
        sb.append(" Insassen");
        sb.append("\n");

        // add picked inmates
        for (String name : shownInmates) {
            sb.append("- ");
            sb.append(name);
            sb.append(" \n");
        }

        if (!allAdded) {
            sb.append("...");
            shownInmates.add("...");
        }

//        Log.d(CLASS+USER, "Text: " + sb);
        view.setText(sb);
    }

    public void initSize() {
        if (view.getWidth() <= 0) Log.w(CLASS + USER, "Prison not initialized");
        initialWidth = 100;//view.getWidth();
        initialHeight = 109;//view.getHeight();
        initialTextSize = 4f;// view.getTextSize();
//        Log.d(CLASS + USER, "standard text size: " + initialTextSize);
        adjustSize();
    }
}
