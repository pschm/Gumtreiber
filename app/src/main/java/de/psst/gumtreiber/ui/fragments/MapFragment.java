package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.UserDataSync;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;
import de.psst.gumtreiber.map.PrisonControl;
import de.psst.gumtreiber.ui.MainActivity;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MapFragment extends Fragment {
    private View fragmentView;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
        initMap();
        return fragmentView;
    }

    /**
     * Initialisert die Map
     */
    private void initMap() {
        MapView mapView = fragmentView.findViewById(R.id.map);
        TextView prisonView = fragmentView.findViewById(R.id.prison);

        MainActivity activity = (MainActivity) getActivity();

        // enable zoom effect
        PhotoViewAttacher viewAttacher = new PhotoViewAttacher(mapView, true);
        mapView.setZoomControl(viewAttacher);

        MapControl mapControl = new MapControl(mapView, activity, new PrisonControl(prisonView));

        mapView.setImageResource(R.mipmap.map);

        //TODO updatesEnabled aus config laden
        UserDataSync uds = new UserDataSync(activity, activity.getLocationHandler(), mapControl);
        uds.startUpdating();

        viewAttacher.setMaximumScale(9f);
        viewAttacher.setMediumScale(3f);
        viewAttacher.setMinimumScale(2f);

//        ViewTreeObserver vto = mapView.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                mapView.getZoomControl().setScale(MapView.INITIAL_ZOOM, MapControl.MAIN_BUILDING_MAP.x, MapControl.MAIN_BUILDING_MAP.y, false);
//            }
//        });


//        mapView.setScale(3f, 500f, 500f, true);
//        mapView.setScale(4f);

//        mapView.setScale(5f, 500, 1000, true);
//        mapView.getZoomControl().update();
//        viewAttacher.setZoomable(false); TODO disable zoom before markers are drawn
//        viewAttacher.setScale(3f);


//        viewAttacher.update();
//        mapView.invalidate();
    }

}
