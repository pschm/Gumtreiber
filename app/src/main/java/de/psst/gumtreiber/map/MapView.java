package de.psst.gumtreiber.map;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

import androidx.appcompat.widget.AppCompatImageView;
import de.psst.gumtreiber.data.AbstractUser;
import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.Vector2;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MapView extends AppCompatImageView {

    // the transformation matrix is not initialized with the unit matrix
    // to correct this, the error is calculated and considered in future calculation
    private static float defaultMatrixError = 1.5827f;

    // Users that are drawn on the map
    private ArrayList<AbstractUser> markers;

    // PhotoViewAttacher which holds this ImageView and enables zoom
    private PhotoViewAttacher zoomControl;

    // MapControl which coordinates the Lists and gps calculation
    private MapControl mapControl;

    // Needed to update the location of the prison on translation
    private PrisonControl prisonControl;

    // save of the old transformation matrix, used to check if the transformation has changed
    private Matrix oldTransformation = new Matrix();
    private Matrix inverse = new Matrix();

    private double scale = 1.0;
    private boolean firstDraw = true;

    // Declare some variables for the onDraw, so we
    // don't have to keep allocating them on the heap
    private MovableMarker marker;
    private Vector2 defaultSize = new Vector2();
    private Coordinate pos = new Coordinate();
    private float[] defaultMatrix = new float[9];
    private float[] matrixValues = new float[9];
    private Vector2 markerPos = new Vector2();
    private Vector2 mapPos = new Vector2(-50f, -50f);
    private Vector2 markerPoint = new Vector2();
    private Vector2 oldPosition = new Vector2();


    // all constructors needed for Android to build the ImageView correctly
    public MapView(Context context) {
        super(context);
    }

    public MapView(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public MapView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
    }

    /**
     * @param zoomControl zoomControl which holds this MapView
     */
    public void setZoomControl(PhotoViewAttacher zoomControl) {
        this.zoomControl = zoomControl;
    }

    /**
     * @param markers users to be drawn of the map
     */
    public void setMarkers(ArrayList<AbstractUser> markers) {
        this.markers = markers;
        invalidate(); // repaint the map
    }

    /**
     * Set the MapControl which calculates the gps/map coordinates and manages the user lists
     * @param mapControl MapControl which manages this MapView
     */
    public void setMapControl(MapControl mapControl) {
        this.mapControl = mapControl;
    }

    public void setPrisonControl(PrisonControl prisonControl) {
        this.prisonControl = prisonControl;
    }

    /**
     * Adjust a given vector to the current zoom of the map
     * @param v1 vector used for the calculation
     * @return new vector adjusted to the map transformation
     */
    public Vector2 adjustToTransformation(Vector2 v1) {
        return adjustToTransformation(v1, zoomControl.getDrawMatrix());
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
     * Calculates a pseudo scaling of the image to scale the markers
     */
    private void calcScaling() {
        Vector2 scaledSize = new Vector2(defaultSize.x, defaultSize.y);
        scaledSize = adjustToTransformation(scaledSize);

        scale = (defaultSize.x + defaultSize.y) / (scaledSize.x + scaledSize.y);

        if (scale < 0.9) scale = 0.9;
        else if (scale > 1.25) scale = 1.25;
    }

    /**
     * Draw the map itself as well as the position of every user
     * on the map based on {@link #markers}
     */
    @Override
    public void onDraw(Canvas canvas) {
        // draw the map
        super.onDraw(canvas);

        // some safety checks
        if (zoomControl == null) {
            Log.d("MapView", "zoomControl missing");
            return;
        }

        if (zoomControl.getDrawMatrix() == null) {
            Log.d("MapView", "There is no draw Matrix");
            return;
        }

        // skip drawing - there are no users to draw
        if (markers == null) {
            Log.d("MapView", "markers is NUll!");
            return;
        }

        if (markers.isEmpty()) {
            Log.w("MapView", "Nothing to draw - the user list ist empty");
            return;
        }

        if (firstDraw) {
            // init scaling (device dependent)
            defaultSize.x = getWidth();
            defaultSize.y = getHeight();

            Log.d("pschm - MapView", "W:" + defaultSize.x + " H:" + defaultSize.y);

            // init transformation matrix error
            zoomControl.getDrawMatrix().getValues(defaultMatrix);
            defaultMatrixError = 1f / defaultMatrix[0];
        }

        // draw all users on the map
        for (AbstractUser u : markers) {
            marker = u.getMarker();

            // save user position
            pos.setLocation(u.getLatitude(), u.getLongitude());

            // map the coordinate according to the Gumtreiber area
            mapPos = mapControl.gpsToMap(pos);

            if (marker == null) {
                Log.w("MapView", "WARNING: User without markers detected! (" + u.getName() + ")");
                return;
            }

            // make sure the marker is visible
            marker.setVisibility(true);

            // set the markers directly to the new position if the zoom changed
            // or let the markers move to the new position
            if (firstDraw || !marker.isAlreadyDrawn()) {
                marker.setPosition(mapPos.x, mapPos.y);
                marker.setAlreadyDrawn(true);
            } else if (!zoomControl.getDrawMatrix().equals(oldTransformation)) {
                // if the marker is currently moving
                // tell the marker to save his position and use this till he is released
                if (marker.isMoving()) {
                    marker.setUsingOwnPosition(true);
                    marker.moveTo(mapPos.x, mapPos.y);
                    if (u.getName().equals("Manni")) Log.d("Manni", "Manni uses now: OWN position");
                }
                else {
                    // marker is currently not moving, so adjust his position to the new zoom
                    marker.setPosition(mapPos.x, mapPos.y);
                }
            }
            else {
                // no zoom detected -> release marker to move again
                marker.setUsingOwnPosition(false);
                if (u.getName().equals("Manni")) Log.d("Manni", "Manni uses now: GIVEN position");

                // smoothly move the markers to the new position
                marker.moveTo(mapPos.x, mapPos.y);
            }
        }

        // translate prison
        prisonControl.updateLocation();

        // save the current transformation
        copyMatrix(oldTransformation, zoomControl.getDrawMatrix());
        firstDraw = false;
    }

    /**
     * @return the PhotoViewAttacher which holds this MapViews
     */
    public PhotoViewAttacher getZoomControl() {
        return zoomControl;
    }

    // some Matrix Utility
    private void copyMatrix(Matrix dest, Matrix src) {
        src.getValues(matrixValues);
        dest.setValues(matrixValues);
    }
}
