package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

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
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
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

        MapControl mapControl = new MapControl(mapView, activity, new PrisonControl(prisonView));

        //TODO updatesEnabled aus config laden
        UserDataSync uds = new UserDataSync(activity, activity.getLocationHandler(), mapControl);
        uds.startUpdating();

        // enable zoom effect
        PhotoViewAttacher viewAttacher = new PhotoViewAttacher(mapView, true);
        mapView.setZoomControl(viewAttacher);
        viewAttacher.setMaximumScale(9f);
        viewAttacher.update();
    }

}
