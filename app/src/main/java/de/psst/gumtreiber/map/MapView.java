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
     * TODO delete later
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
     * TODO Problem beheben, dass die Koordinaten noch eine leichte Verschiebung zur soll Position auf der karte haben
     * Calculates the given coordinate from GPS Position to map coordinates.
     */
    private void gpsToMap(Coordinate pos) {
        double x, y, xFaktor, yFaktor;

        //Log.v("MapView", "- Width:  (" +getWidth()+ ")");
        //Log.v("MapView", "- Height: (" +getHeight()+ ")");

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
     * @param userList users to be drawn of the map
     */
    public void setUserList(ArrayList<User> userList) {
/*
        // add dummy data // TODO delete (just for testing)
        userList = new ArrayList<User>();
        User u = new User("447806517517260", "Nathalie");
        u.latitude =  50.937981;
        u.longitude =  7.020804;
//        u.setMarker(new MovableMarker(this,"Nathalie"));
        userList.add(u); // cologne

        u = new User("447806517517261", "Wolfgang");
        u.latitude =  51.023229;
        u.longitude =  7.562274;
//        u.setMarker(new MovableMarker(this,"Wolfgang"));

        userList.add(u); // main building

        u = new User("447806517517262", "Christopher");
        u.latitude =  51.024237;
        u.longitude =  7.563138;
//        u.setMarker(new MovableMarker(this,"Christopher"));

        userList.add(u); // main building

        u = new User("447806517517262", "Chris");
        u.latitude =  51.024232;
        u.longitude =  7.563138;
//        u.setMarker(new MovableMarker(this,"Chris"));

        userList.add(u); // main building
        userList.add(u); // main building

        u = new User("447806517517263", "Philipp");
        u.latitude =  50.042789;
        u.longitude =  7.287464;
//        u.setMarker(new MovableMarker(this,"Philipp"));
        userList.add(u); // olpe
*/

        // TODO move buildUserGroups() call?
        this.userList = buildUserGroups(activity, userList, 200);
    }

    /**
     * Draw the map itself as well as the position of every user on the map
     */
    @Override
    public void onDraw(Canvas canvas) {
        // draw the map
        super.onDraw(canvas);

        // skip drawing - there are no users to draw
        if (userList == null || userList.isEmpty()) {
            Log.w("MapView", "Nothing to draw - the user list ist empty ");
            return;
        }

        // the paint color and size
        paint.setColor(Color.CYAN);
        paint.setTextSize(35);

        // clear the prison
        prison.clear();

        // draw all users on the map
        for(User u : userList) {
            // save user position
            pos.setLocation(u.latitude, u.longitude);

//            Log.v("MapView", "++++++++++++++++++++++++++++++++++++++++++++");
//            Log.v("MapView", "- GPS (" + u.name + ") " + pos.latitude + "|" + pos.longitude);

            // check if the user is in the area of the map - skip it if not // TODO ggf, aus onDraw an sinvollere Stelle schieben
            if (pos.latitude > 51.026252 || pos.latitude < 51.021335
                    || pos.longitude > 7.566864 || pos.longitude < 7.560268) {
                prison.add(u);
                continue;
            }

            // map the coordinate according to the Gumtreiber map
            gpsToMap(pos);

            // consider possible zoom
            adjustPosToZoom(pos);
//            Log.v("MapView", "- Scale " + pos.latitude + "|" + pos.longitude);

            // TODO offest zum zeichnen an die größe des Markers anpassen
            // set the marker directly to the new position if the zoom changed
            // or let the marker move to the new position
            if (firstDraw || transformation.equals(oldTransformation)) {
                u.getMarker().setPosition((float) pos.latitude - 17, (float) pos.longitude - 150);
                firstDraw = false;
            }
            else
                u.getMarker().moveTo((float) pos.latitude - 17, (float) pos.longitude - 150);

            // draw user on the map // TODO could be deleted if markers work properly
            canvas.drawCircle((float)pos.latitude, (float) pos.longitude, 17.5f, paint);
            canvas.drawText(u.name, (float) pos.latitude, (float) pos.longitude + 47.5f, paint);

            // save the current transformation
            oldTransformation = transformation;
        }

        // draw all prisoners // TODO könnte man später ggf. auch mit einem eigenen Marker machen
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

            // skip all users not on map TODO should be done separately before this!
            if (u.latitude > 51.026252 || u.latitude < 51.021335
                    || u.longitude > 7.566864 || u.longitude < 7.560268) {
                continue;
            }

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
                u.setMarker(new MovableMarker(activity, u.name));
                map[(int)x][(int)y] = u;
            }
            else if (sector.uid != null) {
                // u is the second user in this sector
                // delete both users from list and build a group
                userList.remove(sector);
                userList.remove(u);

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
}
