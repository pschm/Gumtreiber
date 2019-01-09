package de.psst.gumtreiber.map;

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
    private Coordinate pos = new Coordinate();
    private ArrayList<User> userList;
    private ArrayList<User> prison = new ArrayList<>();

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
     * Durch das setzen der Transformationsmatrix werden die Personen
     * auf der Karte auch bei verschiedenen Zoom-Einstellungen richtig angezeigt.
     * @param transformation neue Trasnformationsmatrix
     */
    public void setTransformation(Matrix transformation) {
        this.transformation = transformation;
    }

    /**
     * TODO ## fix it ##
     * Rechnet den gegebenen Punkt von GPS in die
     * passende Koordinate auf der Karte um.
     */
    private Coordinate gpsToMap(Coordinate pos) {
        double x, y, xFaktor, yFaktor;

        //Log.v("MapView", "- Width:  (" +getWidth()+ ")");
        //Log.v("MapView", "- Height: (" +getHeight()+ ")");

        x = pos.latitude * 1000000 - 51020000 - 1335; // min/max: 1335-6252 -> 0-4917
        x = 4917 - x; // x-Achse umkehren von <-- nach -->
        y = pos.longitude* 1000000 -  7560000 -  268; // min/max: 0268-6864 -> 0-6596
        //Log.v("MapView", "- cleared: (" +x+ "|" +y+ ")");

        xFaktor = getWidth()  / 4917.0;
        yFaktor = getHeight() / 6596.0;
        //Log.v("MapView", "- Faktor: (" +xFaktor+ "|" +yFaktor+ ")");

        pos.latitude  = x * xFaktor;
        pos.longitude = y * yFaktor;

        Log.v("MapView", "- Map Coords: (" +pos.latitude+ "|" +pos.longitude+ ")");

        return pos;
    }

    /**
     * Transformieren eines Punktes auf den Zoom
     * @param pos Koordinate im Bild
     * @return Transformierte Koordinate
     */
    private Coordinate adjustPosToZoom(Coordinate pos) {
        // Koordinate
        float[] point = {(float)pos.latitude, (float)pos.longitude};

        // Personenkoordinaten auf den Zoomfaktor umrechnen
        transformation.mapPoints(point);

        pos.latitude  = point[0];
        pos.longitude = point[1];

        return pos;
    }

    /**
     * Ermöglicht die angabe vorhandener Nutzer und ihrer Koordinaten
     * @param userList Liste der einzuzeichnendne Nutzer
     */
    public void setUserList(ArrayList<User> userList) {
        this.userList = userList;
    }


    /**
     * Anzeigen der Karte inkl. Personen
     */
    @Override
    public void onDraw(Canvas canvas) {
        // eigentliche Karte zeichnen
        super.onDraw(canvas);

        // wenn keine Nutzer gegeben sind, das Zeichnen überspringen
        if (userList == null) {
            Log.w("MapView", "Es sind keine Nutzer zum Zeichnen vorhanden!");
            return;
        }

        // Zeichenfarbe und größe setzen
        paint.setColor(Color.CYAN);
        paint.setTextSize(35);

        // gefähngnis leeren
        prison.clear();

        for(User u : userList) {
            // Koordinaten als PoinF speichern
            pos.setLocation(u.latitude, u.longitude);

            Log.v("MapView", "++++++++++++++++++++++++++++++++++++++++++++");
            Log.v("MapView", "- GPS " + pos.latitude + "|" + pos.longitude);

            // prüfen, ob die Personen im Bereich der Karte sind ggf. überspringen
            if (pos.latitude > 51.026252f || pos.latitude < 51.021335
                    || pos.longitude > 7.566864 || pos.longitude < 7.560268) {
                prison.add(u);
                continue;
            }

            // Koordinate auf die Gumtreiberkarte mappen
            pos = gpsToMap(pos);

            pos = adjustPosToZoom(pos);
            Log.v("MapView", "- Scale " + pos.latitude + "|" + pos.longitude);

            // Person auf die Karte zeichnen
            canvas.drawCircle((float)pos.latitude, (float) pos.longitude, 17.5f, paint);
            canvas.drawText(u.name, (float) pos.latitude, (float) pos.longitude + 47.5f, paint);
        }

        // alle Personen im "Gefähngnis zeichnen"
        int c = 0;
        for (User u : prison) {
            pos.latitude  = getWidth()/10;
            pos.longitude = (getHeight() / 10) *25 + c*115;

            pos = adjustPosToZoom(pos);

            if (c > 10) break;
            canvas.drawText(u.name, (float) pos.latitude, (float) pos.longitude, paint);
            c++;
        }
        if (c > 10) canvas.drawText("...", (float) pos.latitude, (float) pos.longitude, paint);
    }

}
