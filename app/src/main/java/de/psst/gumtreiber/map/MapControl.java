package de.psst.gumtreiber.map;

import android.util.Log;
import android.widget.ImageView;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MapControl extends PhotoViewAttacher {

    private MapView mapView;

    public MapControl(ImageView imageView) {
        super(imageView);
    }

    public MapControl(ImageView imageView, boolean zoomable) {
        super(imageView, zoomable);
    }

    /**
     * Die gesetzte MapView erhält updates der Transformationsmatrix
     * @param mapView
     */
    public void setMapView(MapView mapView) {
        this.mapView = mapView;
    }

    @Override
    public void update() {
        super.update();

        // setze die Transformationsmatrix der Karte,
        // um Personen bei Zoom richtig richtig anzuzeigen.
        if (mapView != null) mapView.setTransformation(getDrawMatrix());
    }
}
