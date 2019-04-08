package de.psst.gumtreiber.map;

import android.app.Activity;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.util.Log;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.core.content.ContextCompat;
import androidx.core.content.res.ResourcesCompat;
import de.psst.gumtreiber.R;
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
    private static final String CLASS = "PrisonControl ";
    private static final String USER = "pschm";

    private static final double PRISON_LATITUDE = 51.024707; // 51.024564; // Auf der Platte
    private static final double PRISON_LONGITUDE = 7.560826; // 7.561225;
    private static int PRISON_COUNT = 3; //#users to show in the prison

    private Activity activity;
    private MapControl mapControl;

    private Vector2 position = new Vector2(0, 0);
    private Paint paint = new Paint();
    private float scale = MapView.INITIAL_ZOOM;

    private ArrayList<AbstractUser> inmates = new ArrayList<>();
    private ArrayList<AbstractUser> freeFolk = new ArrayList<>();
    private ArrayList<String> displayedText = new ArrayList<>();

    public PrisonControl(Activity activity) {
        this.activity = activity;

        // init text font and color
        paint.setTypeface(ResourcesCompat.getFont(activity, R.font.almendra_sc));
        paint.setColor(ContextCompat.getColor(activity, R.color.colorAuthFont));
    }

    /**
     * Separate all Users from the given ArrayList which aren't
     * in the geographically area of the map and show them one the map
     *
     * @param users ArrayList to filter
     * @return userList without inmates
     */
    public ArrayList<AbstractUser> updateInmates(ArrayList<AbstractUser> users) {
        filterList(users);

        updateDisplayedText();

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
    }

    /**
     * paint {@link #displayedText} on the given canvas
     */
    public void paintInmates(Canvas canvas) {
        // set text size according to current zoom
        paint.setTextSize(scale * 9f);

        float stepSize = 15f;
        for (int i = 0; i < displayedText.size(); i++) {
            canvas.drawText(
                    displayedText.get(i),
                    position.x,
                    position.y + i * stepSize * scale,
                    paint
            );
        }

//        Log.d(CLASS+USER, "Scale: "+scale);
//        Log.d(CLASS+USER, "Text: "+displayedText);
    }

    /**
     * Separate all Users from the given ArrayList
     * which aren't in the geographically area of the map
     * and save them in their own {@link #inmates} list.
     *
     * @param users ArrayList to filter
     */
    private void filterList(@NonNull ArrayList<AbstractUser> users) {
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
     * Fill the {@link #displayedText} with a maximum
     * of {@link #PRISON_COUNT} users from {@link #inmates}
     */
    private void updateDisplayedText() {
        displayedText.clear();

        // prison is empty
        if (inmates.isEmpty()) {
            displayedText.add(activity.getString(R.string.prison_empty));
            Log.d(CLASS + USER, "There are now users in prison");
            return;
        }

        // remember if all inmates are shown in prison or not
        boolean allAdded = false;

        // add header text
        displayedText.add(inmates.size() + " Insassen");

        // #prison members less than the PRISON_COUNT --> add all
        if (inmates.size() < PRISON_COUNT) {
            for (AbstractUser u : inmates) displayedText.add("- " + u.getName());
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
                        displayedText.add("- " + myName);
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
                displayedText.add("- " + inmates.get(i).getName());
            }
        }

        // add ... at the end, if there are more inmates than shown
        if (!allAdded) displayedText.add("...");
    }

    public void setMapControl(MapControl mapControl) {
        this.mapControl = mapControl;
    }
}
