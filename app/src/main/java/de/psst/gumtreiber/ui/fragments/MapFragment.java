package de.psst.gumtreiber.ui.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ProgressBar;
import android.widget.TextView;

import java.util.ArrayList;
import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.AbstractUser;
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

    private MapControl.OnMapInitialized onMapInitializedListener;
    private UserDataSync.OnUpdateReceivedListener onUpdateReceivedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.fragment_map, container, false);

        // load activity
        activity = (MainActivity) Objects.requireNonNull(getActivity());
        activity.getUds().startUpdating();

        initMap();

        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initLoadingScreen();
        initListeners();
    }

    @Override
    public void onStop() {
        super.onStop();
        mapControl.removeOnMapInitializedListener(onMapInitializedListener);
        activity.getUds().removeOnUpdateReceivedListener(onUpdateReceivedListener);
    }

    /**
     * Initialize the Map and UserDataSync
     */
    private void initMap() {
        // load views
        mapView = fragmentView.findViewById(R.id.map);
        prisonView = fragmentView.findViewById(R.id.prison);

        mapControl = new MapControl(mapView, activity, new PrisonControl(prisonView));

        // configure mapView
        mapView.setImageResource(R.mipmap.map);
        mapView.setMaximumScale(9f);
        mapView.setMediumScale(3f);
        mapView.setMinimumScale(2f);
    }

    /**
     * start listening to location updates of the current user
     * and initialization of the MapView
     */
    private void initListeners() {
        // map initialized
        onMapInitializedListener = () -> {
            if (isUiThread()) Log.d("MapFrag.", "UI Thread!!!");
            else Log.d("MapFrag.", "other Thread.");
            hideLoadingScreen();
        }; //this::hideLoadingScreen;
        mapControl.addOnMapInitializedListener(onMapInitializedListener);

        // current user location
        LocationHandler lh = activity.getLocationHandler();
        // TODO lh.getCurrentLocation();
        lh.addOnLocationChangedListener(mapControl::updateCurrentUserLocation);

        // start listening to Firebase updates
        // TODO call updateFriends / updateUsers if uds has data (getter needed)
        // Firebase updates
        onUpdateReceivedListener = (userList, friendList) -> {
            ArrayList<AbstractUser> arrayList;

            if (userList == null || userList.isEmpty()) arrayList = new ArrayList<>();
            else arrayList = new ArrayList<>(userList);

            mapControl.updateFriends(friendList);
            mapControl.updateUsers(arrayList);
        };
        activity.getUds().addOnUpdateReceivedListener(onUpdateReceivedListener);
    }

    /**
     * Initialize the color of the ProgressBar
     */
    private void initLoadingScreen() {
        ProgressBar loadingCircle = fragmentView.findViewById(R.id.loadingCircle);

        // change progessbar color
        loadingCircle.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    /**
     * Hides both loading screen views (Background & ProgressBar)
     */
    private void hideLoadingScreen() {
        if (isUiThread()) {
            View loadingScreen = fragmentView.findViewById(R.id.loadingScreen);
            View loadingCircle = fragmentView.findViewById(R.id.loadingCircle);
            loadingCircle.setVisibility(View.GONE);
            loadingScreen.setVisibility(View.GONE);
            mapView.setTouchable();
            return;
        }
        activity.runOnUiThread(() -> {
            View loadingScreen = fragmentView.findViewById(R.id.loadingScreen);
            View loadingCircle = fragmentView.findViewById(R.id.loadingCircle);
            loadingCircle.setVisibility(View.GONE);
            loadingScreen.setVisibility(View.GONE);
            mapView.setTouchable();
        });
    }

    private boolean isUiThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Looper.getMainLooper().isCurrentThread();
        } else {
            return Looper.getMainLooper().equals(Looper.myLooper());
        }
    }

}
