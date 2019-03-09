package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
import android.widget.TextView;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.AbstractUser;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.data.UserDataSync;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;
import de.psst.gumtreiber.map.PrisonControl;
import de.psst.gumtreiber.ui.MainActivity;

public class MapFragment extends Fragment {
    private View fragmentView;
    private MapView mapView;
    private TextView prisonView;
    private MapControl mapControl;
    private MainActivity activity;
    private UserDataSync userDataSync;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
        initMap();
        initListeners();
        return fragmentView;
    }

    /**
     * Initialize the Map and UserDataSync
     */
    private void initMap() {
        // load views & activity
        mapView = fragmentView.findViewById(R.id.map);
        prisonView = fragmentView.findViewById(R.id.prison);
        activity = (MainActivity) getActivity();

        mapControl = new MapControl(mapView, activity, new PrisonControl(prisonView));

        // configure mapView
        mapView.setImageResource(R.mipmap.map);
        mapView.setMaximumScale(9f);
        mapView.setMediumScale(3f);
        mapView.setMinimumScale(2f);
        mapView.setZoomable(false); // disable zoom till fully loaded

        //TODO updatesEnabled aus config laden
        //TODO Neues UDS-Konzept umsetzen
        userDataSync = new UserDataSync(activity, activity.getLocationHandler(), true);
    }

    private void initListeners() {
        // start listening to location updates of the current user
        LocationHandler lh = activity.getLocationHandler();
        mapControl.updateCurrentUserLocation(lh.getCurrentLocation());
        lh.addOnLocationChangedListener(mapControl::updateCurrentUserLocation);

        // start listening to Firebase updates
        userDataSync.addOnUpdateReceivedListener((userList, friendList) -> {
            ArrayList<AbstractUser> arrayList;

            if (userList == null || userList.isEmpty()) arrayList = new ArrayList<>();
            else arrayList = new ArrayList<>(userList);

            mapControl.updateFriends(friendList);
            mapControl.updateUsers(arrayList);
        });

        // enable zoom again when global layout is complete
        ViewTreeObserver vto = mapView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mapView.setZoomable(true);
            }
        });
    }


    private void test() {
        ArrayList<AbstractUser> users = new ArrayList<>();
        User u;

        u = new User("12345667790", "Hans-Peter");
        u.setLatitude(51.023);
        u.setLongitude(7.56174);
        users.add(u);

        u = new User("12345667790", "Hans-Peter");
        u.setLatitude(51.0237231);
        u.setLongitude(7.5621626);
        users.add(u);

        mapControl.updateUsers(users);
    }
}
