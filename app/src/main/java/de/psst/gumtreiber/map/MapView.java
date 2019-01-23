package de.psst.gumtreiber.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.PointF;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.User;

public class MapView extends AppCompatImageView {

    // Users that are drawn on the map
    private ArrayList<User> userList;
    private ArrayList<User> prison = new ArrayList<>();

    // needed for initializing of the movable markers
    private Activity activity;

    private MapControl mapControl;

    private Matrix transformation = new Matrix();
    private Matrix oldTransformation = new Matrix();
    private boolean firstDraw = true;
    private double scale = 1.0;

    // These are set so we don't keep allocating them on the heap
    private Paint paint = new Paint();
    private PointF defaultSize = new PointF();
    private PointF mapPos = new PointF(-50f, -50f);
    private Coordinate pos = new Coordinate();
    private Coordinate prisonPos = new Coordinate(51.022255, 7.560842);
    private float[] defaultMatrix = new float[9];

    // constants for gps calculation
    private final static double MAX_LAT = 51.026252;
    private final static double MIN_LAT = 51.021335;
    private final static double MAX_LONG = 7.566864;
    private final static double MIN_LONG = 7.560268;
    private final static double DELTA_LAT = (MAX_LAT - MIN_LAT) * 1000000;
    private final static double DELTA_LONG = (MAX_LONG - MIN_LONG) * 1000000;

    // for zoom correction
    private static float defaultMatrixError = 1.5827f;


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
     * @param mapControl mapControl which holds this MapView
     */
    public void setMapControl(MapControl mapControl) {
        this.mapControl = mapControl;
    }

    /**
     * Set the Activity where the map is placed. The Activity is needed to create
     * the MovableMarker on the same context.
     */
    public void setActivity(Activity activity) {
        this.activity = activity;
    }

    /**
     * The transformation matrix is used to calculate the user position
     * on the map accordingly to the zoom
     * @param transformation the matrix that is used to calculate the current zoom
     */
    public void setTransformation(Matrix transformation) {
        this.transformation = transformation;
    }

    /**
     * @param userList users to be drawn of the map
     */
    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;

        fillPrison();

        // TODO boxSize an den Zoom anpassen? Nachteil, einteilung müsste im onDraw() aufgerufen werden
        this.userList = buildUserGroups(activity, userList, 200);

        invalidate(); // repaint the map
    }

    /**
     * Calculates the given coordinate from GPS Position to map coordinates
     * and saves it in {@link #mapPos}
     */
    private void gpsToMap(Coordinate pos) {
        // limit gps to the relevant ares
        pos.latitude = (pos.latitude - MIN_LAT) * 1000000; // value between 0-4917
        pos.longitude = (pos.longitude - MIN_LONG) * 1000000; // value between 0-6596

        // invert y-Axis (other coordinate system)
        pos.latitude = DELTA_LAT - pos.latitude;

        // calc x,y values according to screen size
        mapPos.x = (float)(pos.longitude * (getWidth()/ DELTA_LONG));
        mapPos.y = (float)(pos.latitude * (getHeight()/ DELTA_LAT));

//        return mapPos;
    }

    /**
     * Adjust a given coordinate to the current zoom of the map
     */
    private void adjustPosToZoom(PointF mapPos) {
        // Coordinate
        float[] point = {mapPos.x*defaultMatrixError, mapPos.y*defaultMatrixError};

        // adjust coordinate to zoom by applying the matrix
        mapControl.getDrawMatrix().mapPoints(point);

        // save the values
        mapPos.x = point[0];
        mapPos.y = point[1];

//        return pos;
    }

    /**
     * Calculates the scaling of the image
     */
    private void calcScaling() {
        PointF scaledSize = new PointF(defaultSize.x, defaultSize.y);
        adjustPosToZoom(scaledSize);

        scale = (defaultSize.x + defaultSize.y) / (scaledSize.x + scaledSize.y);

        if (scale < 0.9) scale = 0.9;
        else if (scale > 1.25) scale = 1.25;
    }

    /**
     * Draw the map itself as well as the position of every user
     * on the map based on {@link #userList} and {@link #prison}
     */
    @Override
    public void onDraw(Canvas canvas) {
        // draw the map
        super.onDraw(canvas);

        if (firstDraw) {
            // init scaling (device dependent)
            defaultSize.x = getWidth();
            defaultSize.y = getHeight();

            // init transformation matrix error
            mapControl.getDrawMatrix().getValues(defaultMatrix);
            defaultMatrixError = 1f / defaultMatrix[0];
        }

        // skip drawing - there are no users to draw
        if (userList == null || userList.isEmpty()) {
            Log.w("MapView", "Nothing to draw - the user list ist empty or null ");
            return;
        }

        // the paint color and size
        paint.setColor(Color.CYAN);
        paint.setTextSize(35);

        // draw all users on the map
        for(User u : userList) {
            // save user position
            pos.setLocation(u.latitude, u.longitude);

            // map the coordinate according to the Gumtreiber area
            gpsToMap(pos);

            // consider possible zoom
            adjustPosToZoom(mapPos);

            if (u.getMarker() == null) {
                Log.w("MapView", "WARNING: User without marker detected!");
                return;
            }

            // set the marker directly to the new position if the zoom changed
            // or let the marker move to the new position
            if (firstDraw || transformation.equals(oldTransformation)) {
                u.getMarker().setPosition(mapPos.x - 17,mapPos.y - 150);
                firstDraw = false;
            }
            else {
                u.getMarker().moveTo(mapPos.x - 17, mapPos.y - 150);
            }

            // scale the marker according to the zoom
            calcScaling();
            u.getMarker().setScale((float) scale);

            // draw user on the map // TODO delete before presentation?
            canvas.drawCircle(mapPos.x, mapPos.y, 17.5f, paint);
            canvas.drawText(u.name, mapPos.x, mapPos.y + 47.5f, paint);

            // save the current transformation
            oldTransformation = transformation;
        }

        // draw all prisoners
        // TODO könnte man später ggf. auch mit einem eigenen Marker machen, jenachdem was besser auf der finalen Karte aussieht
        int c = 0;
        for (User u : prison) {
            pos.setLocation(prisonPos.latitude, prisonPos.longitude);

            gpsToMap(pos);
            adjustPosToZoom(mapPos);

            if (c > 10) break;
            canvas.drawText(u.name, mapPos.x, mapPos.y + c*115, paint);
            c++;
        }
        if (c > 10) canvas.drawText("...", mapPos.x, mapPos.y, paint);
    }

    /**
     * Merge multiple geographically close users to a group
     * As well as add MovableMarkers to each user/group
     * @param boxSize distance before users are merged
     * @param userList Userlist to control
     * @return returns a new list with users and merged users
     */
    public ArrayList<User> buildUserGroups(Activity activity, ArrayList<User> userList, int boxSize) {
        // reduce gps coordinates to the area (GM)
        int xSize = (int) DELTA_LONG;
        int ySize = (int) DELTA_LAT;
        double x, y;

        // create a grid to detect close users
        User[][] map = new User[xSize/boxSize][ySize/boxSize];

        // list for all merged users
        ArrayList<User> mergedUserList = new ArrayList<>();

        // sort all users in the grid
        for (int i = 0; i < userList.size(); i++) {
            User u = userList.get(i);

            // transform user coordinates to the area
            x = (u.longitude - MIN_LONG) * 1000000; // min/max: 0268-6864 -> 0-6596
            y = (u.latitude - MIN_LAT) * 1000000; // min/max: 1335-6252 -> 0-4917
            y = DELTA_LAT - y; // invert y-Axis

            // calc grid position
            x /= boxSize;
            y /= boxSize;

            // load possible users already in this grid sector
            User sector = map[(int)x][(int)y];

            if (sector == null) {
                // u is the first user in this sector
                if (u.getMarker() == null) {
                    u.setMarker(new MovableMarker(activity, u.name));
//                    u.getMarker().setPosition(-100f, -100f);
                }
                map[(int)x][(int)y] = u;
            }
            else if (sector.uid != null) {
                // u is the second user in this sector
                // delete both users from list and build a group
                userList.remove(sector);
                userList.remove(u);

                // hide label if existing // TODO maybe set invisible instead?
                if (u.getMarker() != null) u.getMarker().setPosition(-50f, -50f);

                // reduce index according to deleted users
                i -= 2;

                // create new "User-Group"
                User mergedUsers = new User(null, "2");
                mergedUsers.setMarker(sector.getMarker());
                mergedUsers.getMarker().changeLabel("2");
                mergedUsers.latitude = u.latitude;
                mergedUsers.longitude = u.longitude;

                // save merge user to the grid
                map[(int)x][(int)y] = mergedUsers;
                mergedUserList.add(mergedUsers);

            }
            else {
                // u is at least the third user in this sector
                // remove u from the userList
                userList.remove(u);

                // hide label if existing // TODO see above
                if (u.getMarker() != null) u.getMarker().setPosition(-50f, -50f);

                // reduce index according to the deleted user
                i--;

                // increase the user counter
                String label = "" + (Integer.parseInt(sector.name) + 1);
                sector.name = label;
                sector.getMarker().changeLabel(label);
            }
        }

        // add all mergedUsers to the list an return it
        userList.addAll(mergedUserList);
        return userList;
    }

    /**
     * Separate all Users from {@link #userList} which aren't in the geographically area of the map
     * and save them in their own {@link #prison} list.
     */
    private void fillPrison() {
        prison.clear();
        for (int i = userList.size() - 1; i >= 0; i--) {
            User u = userList.get(i);

            if (u.latitude > 51.026252 || u.latitude < 51.021335
                    || u.longitude > 7.566864 || u.longitude < 7.560268) {
                prison.add(u);
                userList.remove(u);
            }
        }
    }
}
