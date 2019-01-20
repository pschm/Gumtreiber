package de.psst.gumtreiber.map;

import android.app.Activity;
import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;
import android.util.Log;

import java.util.ArrayList;

import de.psst.gumtreiber.data.Coordinate;
import de.psst.gumtreiber.data.User;

public class MapView extends AppCompatImageView {

    private Paint paint = new Paint();
    private Matrix transformation = new Matrix();
    private Matrix oldTransformation = new Matrix();
    private Coordinate pos = new Coordinate();
    private ArrayList<User> userList;
    private ArrayList<User> prison = new ArrayList<>();
    private Activity activity;
    private boolean firstDraw = true;
    private double scale = 1.0;
    private Coordinate defaultSize;

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
    }


    /**
     * TODO Problem beheben, dass die Koordinaten noch eine leichte Verschiebung zur soll Position auf der karte haben
     * Calculates the given coordinate from GPS Position to map coordinates.
     */
    private void gpsToMap(Coordinate pos) {
        double x, y, xFaktor, yFaktor;

//        Log.v("MapView", "- Width:  (" +getWidth()+ ")");  // 1860 normal size
//        Log.v("MapView", "- Height: (" +getHeight()+ ")"); // 2193 normal size

        x = pos.latitude * 1000000 - 51020000 - 1335; // min/max: 1335-6252 -> 0-4917
        x = 4917 - x; // x-Achse umkehren von <-- nach -->
        y = pos.longitude* 1000000 -  7560000 -  268; // min/max: 0268-6864 -> 0-6596

        // TODO fix correction
        y += 2200;
        x += 200;

        //Log.v("MapView", "- cleared: (" +x+ "|" +y+ ")");

        xFaktor = getWidth()  / 4917.0;
        yFaktor = getHeight() / 6596.0;
        //Log.v("MapView", "- Faktor: (" +xFaktor+ "|" +yFaktor+ ")");

        pos.latitude  = x * xFaktor;
        pos.longitude = y * yFaktor;

//        Log.v("MapView", "- Map Coords: (" +pos.latitude+ "|" +pos.longitude+ ")");

//        return pos;
    }

    /**
     * Adjust a given coordinate to the current zoom of the map
     */
    private void adjustPosToZoom(Coordinate pos) {
        // Koordinate
        float[] point = {(float)pos.latitude, (float)pos.longitude};

        // Personenkoordinaten auf den Zoomfaktor umrechnen
        transformation.mapPoints(point);

        pos.latitude  = point[0];
        pos.longitude = point[1];

//        return pos;
    }

    /**
     * Calculates the scaling of the image
     */
    private void calcScaling() {
        Coordinate scaledSize = new Coordinate(defaultSize.latitude, defaultSize.longitude);
        adjustPosToZoom(scaledSize); // defaultSize;

        scale = (defaultSize.latitude + defaultSize.longitude) / (scaledSize.latitude + scaledSize.longitude);

        if (scale < 0.9) scale = 0.9;
        else if (scale > 1.25) scale = 1.25;
        Log.v("MapView", "Scale: " + scale);
    }

    /**
     * Draw the map itself as well as the position of every user
     * on the map based on {@link #userList} and {@link #prison}
     */
    @Override
    public void onDraw(Canvas canvas) {
        // draw the map
        super.onDraw(canvas);

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

//            Log.v("MapView", "++++++++++++++++++++++++++++++++++++++++++++");
//            Log.v("MapView", "- GPS (" + u.name + ") " + pos.latitude + "|" + pos.longitude);

            // map the coordinate according to the Gumtreiber area
            gpsToMap(pos);

            // consider possible zoom
            adjustPosToZoom(pos);
//            Log.v("MapView", "- Scale " + pos.latitude + "|" + pos.longitude);

            if (u.getMarker() == null) {
                Log.w("MapView", "WARNING: User without marker detected!");
                return;
            }

            // set the marker directly to the new position if the zoom changed
            // or let the marker move to the new position
            if (firstDraw || transformation.equals(oldTransformation)) {
                // init scaling (device dependent)
                defaultSize = new Coordinate(getWidth(), getHeight());

                u.getMarker().setPosition((float) (pos.latitude - 17), (float) (pos.longitude - 150));

                firstDraw = false;
            }
            else {
                u.getMarker().moveTo((float) pos.latitude - 17, (float) pos.longitude - 150);
            }

            // scale the marker according to the zoom
            calcScaling();
            u.getMarker().setScale((float) scale);

            // draw user on the map // TODO could be deleted if markers work properly
            canvas.drawCircle((float)pos.latitude, (float) pos.longitude, 17.5f, paint);
            canvas.drawText(u.name, (float) pos.latitude, (float) pos.longitude + 47.5f, paint);

            // save the current transformation
            oldTransformation = transformation;
        }

        // draw all prisoners
        // TODO könnte man später ggf. auch mit einem eigenen Marker machen, jenachdem was besser auf der finalen Karte aussieht
        int c = 0;
        for (User u : prison) {
            pos.latitude  = getWidth() / 10.0;
            pos.longitude = (getHeight() / 10.0) * 12 + c*115;

            adjustPosToZoom(pos);

            if (c > 10) break;
            canvas.drawText(u.name, (float) pos.latitude, (float) pos.longitude, paint);
            c++;
        }
        if (c > 10) canvas.drawText("...", (float) pos.latitude, (float) pos.longitude, paint);
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
        int xSize = 51026252 - 51021335; // 4917
        int ySize =  7566864 -  7560268; // 6596
        double x, y;

        // create a grid to detect close users
        User[][] map = new User[xSize/boxSize][ySize/boxSize];

        // list for all merged users
        ArrayList<User> mergedUserList = new ArrayList<>();

        // sort all users in the grid
        for (int i = 0; i < userList.size(); i++) {
            User u = userList.get(i);

            // transform user coordinates to the area
            x = (u.latitude * 1000000 - 51020000 - 1335); // min/max: 1335-6252 -> 0-4917
            x = 4917 - x; // x-Achse umkehren von <-- nach -->
            y = (u.longitude* 1000000 -  7560000 -  268); // min/max: 0268-6864 -> 0-6596

            // calc grid position
            x /= boxSize;
            y /= boxSize;

            // load possible users already in this grid sector
            User sector = map[(int)x][(int)y];

            if (sector == null) {
                // u is the first user in this sector
                if (u.getMarker() == null)
                    u.setMarker(new MovableMarker(activity, u.name));
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
