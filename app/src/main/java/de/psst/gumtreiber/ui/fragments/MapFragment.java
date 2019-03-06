package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;
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
     * Initialize the Map and UserDataSync
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
        //TODO Neues UDS-Konzept umsetzen
        //UserDataSync uds = new UserDataSync(activity, activity.getLocationHandler(), mapControl);
        //uds.startUpdating();

        // disable zoom till fully loaded
        viewAttacher.setZoomable(false);

        viewAttacher.setMaximumScale(9f);
        viewAttacher.setMediumScale(3f);
        viewAttacher.setMinimumScale(2f);

        ViewTreeObserver vto = mapView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                viewAttacher.setZoomable(true);
//                mapView.getZoomControl().setScale(MapView.INITIAL_ZOOM, MapControl.MAIN_BUILDING_MAP.x, MapControl.MAIN_BUILDING_MAP.y, false);
            }
        });
    }

}
