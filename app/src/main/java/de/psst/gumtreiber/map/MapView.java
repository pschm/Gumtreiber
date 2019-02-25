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
import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.data.Vector2;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MapView extends AppCompatImageView {

    // the transformation matrix is not initialized with the unit matrix
    // to correct this, the error is calculated and considered in future calculation
    private static float defaultMatrixError = 1.5827f;

    // Users that are drawn on the map
    private ArrayList<User> markers;

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
    private boolean firstDraw = true; // TODO maybe move to init function

    // painter used to draw the map TODO could be deleted, if all problems with MovableMarkers are fixed
    private Paint paint = new Paint();


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
    public void setMarkers(ArrayList<User> markers) {
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
     * Adjust a given coordinate to the current zoom of the map
     */
    public void adjustPosToZoom(Vector2 mapPos) {
        adjustPosToZoom(mapPos, zoomControl.getDrawMatrix());
    }

    /**
     * Adjust a given coordinate to the zoom of the given transformation matrix
     */
    public void adjustPosToZoom(Vector2 mapPos, Matrix transformation) {
        // save the coordinate as float array (needed for the matrix mul.)
        float[] point = {mapPos.x*defaultMatrixError, mapPos.y*defaultMatrixError};

        // adjust coordinate to zoom by applying the matrix
        transformation.mapPoints(point);

        // save the values
        mapPos.x = point[0];
        mapPos.y = point[1];
    }

    /**
     * Calculates a pseudo scaling of the image to scale the markers
     */
    private void calcScaling() {
        Vector2 scaledSize = new Vector2(defaultSize.x, defaultSize.y);
        adjustPosToZoom(scaledSize);

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

        // the paint color and size TODO maybe move to init function
        paint.setColor(Color.CYAN);
        paint.setTextSize(35);

        // draw all users on the map
        for (User u : markers) {
            marker = u.getMarker();

            // save user position
            pos.setLocation(u.getLatitude(), u.getLongitude());

            // map the coordinate according to the Gumtreiber area
            mapPos = mapControl.gpsToMap(pos);

//            if (u.name.equals("Hegenkranz")) Log.d("pschm - MapView", u.name + " MapCoords - " + mapPos);

            // consider possible zoom
            adjustPosToZoom(mapPos);

//            if (u.name.equals("Hegenkranz")) Log.d("pschm - MapView", u.name + " ZoomedCoords - " + mapPos);

            if (marker == null) {
                Log.w("MapView", "WARNING: User without markers detected! (" + u.getName() + ")");
                return;
            }

            // make sure the marker is visible TODO maybe move this in MapControl
            marker.setVisibility(true);

            // set the markers directly to the new position if the zoom changed
            // or let the markers move to the new position
            if (firstDraw || !marker.isAlreadyDrawn()) {
                marker.setPosition(mapPos.x, mapPos.y);
                marker.setAlreadyDrawn(true);
                printDebug(u, "First draw");
            } else if (!zoomControl.getDrawMatrix().equals(oldTransformation)) {
                printDebug(u, "new trans. mat -> zoom detected");
                // TODO smooth movable markers while zooming
                if (marker.isMoving()) {
                    printDebug(u, "marker is moving");
                    printDebug(u, "current user position: " + marker.getPosition());

                    // invert old trans. matrix
                    oldTransformation.invert(inverse);

                    // copy current marker position
                    copyVector(oldPosition, marker.getPosition());

                    // calc old map position (excluding zoom)
                    adjustPosToZoom(oldPosition, inverse);
                    printDebug(u, "Old Position (without zoom): " + oldPosition);

                    // calc old position with new zoom
                    adjustPosToZoom(oldPosition);
                    printDebug(u, "Old Position (with new zoom): " + oldPosition);

                    // set the marker to the calc position
                    marker.setPosition(50, 500); // should be oldPosition
                    printDebug(u, "User now positioned at: " + marker.getPosition());

                    // move the marker to the new target position
                    marker.moveTo(mapPos.x, mapPos.y);
                    printDebug(u, "Target position (moveTo): " + mapPos);
                }
                else {
                    // marker is not moving, just adjust to new zoom
                    marker.setPosition(mapPos.x, mapPos.y);
                    printDebug(u, "marker is NOT moving");
                    printDebug(u, "marker is set to new pos: " + marker.getPosition());
                }
            }
            else {
                // smoothly move the markers to the new position
                marker.moveTo(mapPos.x, mapPos.y);
                printDebug(u, "marker moving to new position (no zoom detected)");
            }

            printDebug(u, "----------------------------------------");
            // scale the markers according to the zoom
            calcScaling();
            marker.setScale((float) scale);

            // draw user on the map
            canvas.drawCircle(mapPos.x, mapPos.y, 17.5f, paint);
        }

        // translate prison
        prisonControl.updateLocation();

        // save the current transformation
        copyMatix(oldTransformation, zoomControl.getDrawMatrix());
        firstDraw = false;
    }


    // some Matrix/Vector Utility
    private void copyMatix(Matrix dest, Matrix src) {
        src.getValues(matrixValues);
        dest.setValues(matrixValues);
    }

    private void copyVector(Vector2 dest, Vector2 src) {
        dest.x = src.x;
        dest.y = src.y;
    }

    private Matrix substracMatrix(Matrix a, Matrix b) {
        float[] aA = new float[9];
        float[] bA = new float[9];
        a.getValues(aA);
        b.getValues(bA);
        for (int i = 0; i < 9; i++) {
            aA[i] = aA[i] - bA[i];
        }
        a.setValues(aA);
        return a;
    }

    /**
     * Cleaner debuging of the jumping user on zoom bug
     *
     * @param msg to display in Log.d
     * @param u   currently calc user
     */
    private void printDebug(User u, String msg) {
//        if (u.name.equals("Hegenkranz")) Log.d("pschm - MapView", msg);
    }
}
