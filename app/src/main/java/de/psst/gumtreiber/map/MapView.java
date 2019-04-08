package de.psst.gumtreiber.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Matrix;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;

import java.util.ArrayList;

import de.psst.gumtreiber.data.AbstractUser;
import de.psst.gumtreiber.data.Vector2;
import uk.co.senab.photoview.PhotoView;

public class MapView extends PhotoView {
    private static final String CLASS = "MapView ";
    private static final String USER = "pschm";

    // the transformation matrix is not initialized with the unit matrix
    // to correct this, the error is calculated and considered in future calculation
    private static float defaultMatrixError = 1.5827f;

    public static final float INITIAL_ZOOM = 2.5f;

    // Users that are drawn on the map
    private ArrayList<AbstractUser> markers;
//    private ArrayList<AbstractUser> markers2; // needed for dynamicGroups

    // Needed to update the location of the prison on translation
    private PrisonControl prisonControl;

    // save of the old transformation matrix, used to check if the transformation has changed
    private Matrix oldTransformation = new Matrix();

    private boolean firstDraw = true;
    private boolean touchable = false;

    // Declare some variables for the onDraw, so we
    // don't have to keep allocating them on the heap
    private float[] defaultMatrix = new float[9];
    private float[] matrixValues = new float[9];

    // all constructors needed for Android to build the ImageView correctly
    public MapView(Context context) {
        super(context);
        initMatrixListener();
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
        initMatrixListener();
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        initMatrixListener();
    }

    private void initMatrixListener() {
        setOnMatrixChangeListener(rect -> adjustMarker());
    }

    /**
     * Update the position of every user based on {@link #markers}
     */
    public void adjustMarker() {
        if (markers == null || markers.isEmpty()) {
            Log.w(CLASS + USER, "There are no Users to display.");
            return;
        }

        if (firstDraw) {
            // init transformation matrix error
            getDisplayMatrix().getValues(defaultMatrix);
            defaultMatrixError = 1f / defaultMatrix[0];
            defaultMatrixError *= INITIAL_ZOOM; // include if buildUserGroups is used in MapControl
        }

        MovableMarker marker;
        Vector2 mapPos;

        // draw all users on the map
        for (AbstractUser u : markers) { // dynamicGroups change to markers2
            marker = u.getMarker();

            // map the coordinate according to the Gumtreiber area
            mapPos = u.getPosition();

            if (marker == null) {
                Log.w(CLASS + USER, "WARNING: User without markers detected! (" + u.getName() + ")");
                return;
            }

            // make sure the marker is visible
            marker.setVisibility(true);

            // apply marker positioning
            if (firstDraw || !marker.isAlreadyDrawn()) {
                marker.setPosition(mapPos.x, mapPos.y);
                marker.setAlreadyDrawn(true);
            } else if (!getDisplayMatrix().equals(oldTransformation)) {
                // if the marker is currently moving
                // tell the marker to save his position and use this till he is released
                if (marker.isMoving()) {
                    marker.setUsingOwnPosition(true);
                    marker.moveTo(mapPos.x, mapPos.y);
                } else {
                    // marker is currently not moving, so adjust his position to the new zoom
                    marker.setPosition(mapPos.x, mapPos.y);
                }
            } else {
                // no zoom detected -> release marker to move again
                marker.setUsingOwnPosition(false);

                // smoothly move the markers to the new position
                marker.moveTo(mapPos.x, mapPos.y);
            }
        }

        // translate prison
        prisonControl.updateLocation();

        // save the current transformation
        copyMatrix(oldTransformation, getDisplayMatrix());
        firstDraw = false;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);

        // paint the prison on the map
        prisonControl.paintInmates(canvas);
    }

    /**
     * Adjust a given vector to the current zoom of the map
     *
     * @param v1 vector used for the calculation
     * @return new vector adjusted to the map transformation
     */
    public Vector2 adjustToTransformation(Vector2 v1) {
        return adjustToTransformation(v1, getDisplayMatrix());
    }

    /**
     * Adjust a given vector to the zoom of the given transformation matrix
     */
    public Vector2 adjustToTransformation(Vector2 v1, Matrix transformation) {
        // save the coordinate as float array (needed for the matrix mul.)
        float[] point = {v1.x * defaultMatrixError, v1.y * defaultMatrixError};

        // adjust coordinate to transformation by applying the matrix
        transformation.mapPoints(point);

        // return new pos
        return new Vector2(point[0], point[1]);
    }

    /**
     * @return the current width of the map in its view
     */
    public int getMapViewWidth() {
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float imgWidth = getDrawable().getIntrinsicWidth();
        float imgHeight = getDrawable().getIntrinsicHeight();
        float viewRatio = viewWidth / viewHeight;
        float imgRatio = imgWidth / imgHeight;
        float newWidth = viewWidth;

        if (viewRatio > imgRatio) {
            // img height fits but width doesn't
            newWidth = viewHeight * imgRatio;
        }

        return (int) newWidth;
    }

    /**
     * @return the current height of the map in its view
     */
    public int getMapViewHeight() {
        float viewWidth = getWidth();
        float viewHeight = getHeight();
        float imgWidth = getDrawable().getIntrinsicWidth();
        float imgHeight = getDrawable().getIntrinsicHeight();
        float viewRatio = viewWidth / viewHeight;
        float imgRatio = imgWidth / imgHeight;
        float newHeight = viewHeight;

        if (viewRatio < imgRatio) {
            // img height fits but width doesn't
            newHeight = viewWidth * imgRatio;
        }

        return (int) newHeight;
    }

    /**
     * Set or update the markers which will be shown on the map
     * @param markers users to be drawn of the map
     */
    public void setMarkers(ArrayList<AbstractUser> markers) {
        this.markers = markers;
        adjustMarker();
    }

    public void setPrisonControl(PrisonControl prisonControl) {
        this.prisonControl = prisonControl;
    }

    @Override
    public boolean dispatchTouchEvent(MotionEvent event) {
        if (!isTouchable()) return false;
        return super.dispatchTouchEvent(event);
    }

    public boolean isTouchable() {
        return touchable;
    }

    public void setTouchable() {
        setTouchable(true);
    }

    public void setTouchable(boolean touchable) {
        this.touchable = touchable;
    }

    // some Matrix Utility
    private void copyMatrix(Matrix dest, Matrix src) {
        src.getValues(matrixValues);
        dest.setValues(matrixValues);
    }
}
