package de.psst.gumtreiber.map;

import android.widget.ImageView;

import uk.co.senab.photoview.PhotoViewAttacher;

// TODO maybe delete and just use PhotoViewAttacher
public class MapControl extends PhotoViewAttacher {

    public MapControl(ImageView imageView) {
        super(imageView);
    }

    public MapControl(ImageView imageView, boolean zoomable) {
        super(imageView, zoomable);
    }
}
