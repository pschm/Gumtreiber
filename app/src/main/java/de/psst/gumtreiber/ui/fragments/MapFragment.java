package de.psst.gumtreiber.ui.fragments;

import android.graphics.Color;
import android.os.Build;
import android.os.Bundle;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.ViewTreeObserver;
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
import de.psst.gumtreiber.data.UserFilter;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;
import de.psst.gumtreiber.map.PrisonControl;
import de.psst.gumtreiber.ui.MainActivity;

import static de.psst.gumtreiber.map.MapControl.MAIN_BUILDING_MAP;
import static de.psst.gumtreiber.map.MapView.INITIAL_ZOOM;
import static de.psst.gumtreiber.ui.MainActivity.STG_FRAGMENT_TAG;

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
        initFilterMsg();

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
     * initialize filter TextView
     */
    private void initFilterMsg() {
        TextView filterMsg = fragmentView.findViewById(R.id.filterMsg);

        if (UserFilter.filterActive()) filterMsg.setText(getString(R.string.filter_msg_text));
        else filterMsg.setText("");


        filterMsg.setOnClickListener(view ->
                activity.getSupportFragmentManager()
                        .beginTransaction()
                        .replace(R.id.content_frame, new SettingsFragment(), STG_FRAGMENT_TAG)
                        .addToBackStack(null).commit()
        );
    }

    /**
     * Initialize the Map and UserDataSync
     */
    private void initMap() {
        // load views
        mapView = fragmentView.findViewById(R.id.map);
        prisonView = fragmentView.findViewById(R.id.prison);

        // configure mapView
        mapView.setImageResource(R.mipmap.map);
        mapView.setMaximumScale(9f);
        mapView.setMediumScale(3f);
        mapView.setMinimumScale(2f);

        PrisonControl prisonControl = new PrisonControl(prisonView);

        prisonView.setVisibility(View.INVISIBLE); // TODO ... DELETE TextView completely

        // initialize zoom on MainBuilding
        ViewTreeObserver vto = mapView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
                mapView.setScale(INITIAL_ZOOM, MAIN_BUILDING_MAP.x, MAIN_BUILDING_MAP.y, false);

                prisonControl.initSize();
            }
        });

        // init mapControl
        mapControl = new MapControl(mapView, activity, prisonControl);
    }

    /**
     * start listening to location updates of the current user
     * and initialization of the MapView
     */
    private void initListeners() {
        // map initialized
        onMapInitializedListener = this::hideLoadingScreen;
        mapControl.addOnMapInitializedListener(onMapInitializedListener);

        // start listening to Firebase updates
        onUpdateReceivedListener = (userList, friendList) -> {
            ArrayList<AbstractUser> arrayList;

            if (userList == null || userList.isEmpty()) arrayList = new ArrayList<>();
            else arrayList = new ArrayList<>(userList);

            mapControl.updateFriends(friendList);
            mapControl.updateUsers(arrayList);
        };
        activity.getUds().addOnUpdateReceivedListener(onUpdateReceivedListener);

        // wait for the layout to finish before updating markers
        ViewTreeObserver vto = mapView.getViewTreeObserver();
        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
            @Override
            public void onGlobalLayout() {
                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);

                // get possible available data
                ArrayList<AbstractUser> users = activity.getUds().getUsers();
                ArrayList<String> friends = activity.getUds().getFriends();

                // if user data is available update immediately
                if (users != null && friends != null) {
                    mapControl.updateFriends(friends);
                    mapControl.updateUsers(users);
                }
            }
        });
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

    public static boolean isUiThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Looper.getMainLooper().isCurrentThread();
        } else {
            return Looper.getMainLooper().equals(Looper.myLooper());
        }
    }

}
