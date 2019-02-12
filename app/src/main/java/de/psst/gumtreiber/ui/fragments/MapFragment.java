package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;

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

        MapView map = fragmentView.findViewById(R.id.map);

        // enable zoom effect
        MapControl mc = new MapControl(map, true);
        mc.setMapView(map);
        mc.setMaximumScale(9f);
        mc.update();
    }


}
