package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.UserDataSync;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;
import de.psst.gumtreiber.ui.MainActivity;

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

        MainActivity activity = (MainActivity) getActivity();

        //TODO updatesEnabled aus config laden
//        if (activity == null) Log.d("MapView", "ac is null");

        UserDataSync uds = new UserDataSync(activity, activity.getLocationHandler(), map);
        uds.startUpdating();


        // enable zoom effect
        MapControl mc = new MapControl(map, true);
        map.setMapControl(mc);
        map.setActivity(getActivity());
        mc.setMaximumScale(9f);
        mc.update();
    }

}
