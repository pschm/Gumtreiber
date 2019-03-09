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
import de.psst.gumtreiber.data.Course;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.data.UserDataSync;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;
import de.psst.gumtreiber.map.PrisonControl;
import de.psst.gumtreiber.ui.MainActivity;
import uk.co.senab.photoview.PhotoViewAttacher;

public class MapFragment extends Fragment {
    private View fragmentView;
    private MainActivity activity;
    private MapControl mapControl;
    private MapControl.OnMapInitialized onMapInitializedListener;
    private UserDataSync.OnUpdateReceivedListener onUpdateReceivedListener;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        fragmentView = inflater.inflate(R.layout.fragment_map, container, false);
        activity = (MainActivity) Objects.requireNonNull(getActivity());
        activity.getUds().startUpdating();
        return fragmentView;
    }

    @Override
    public void onStart() {
        super.onStart();
        initLoadingScreen();
        initMap();

//        testData(); // TODO delete
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
        Log.d("timing", "initMap()");

        MapView mapView = fragmentView.findViewById(R.id.map);
        mapView.setImageResource(R.mipmap.map);
        TextView prisonView = fragmentView.findViewById(R.id.prison);

        // enable zoom effect
        PhotoViewAttacher viewAttacher = new PhotoViewAttacher(mapView, true);
        mapView.setZoomControl(viewAttacher);

        mapControl = new MapControl(mapView, activity, new PrisonControl(prisonView));
        Log.d("timing", "initController()");


        Log.d("timing", "initDataSync()");
//        LocationHandler lh = activity.getLocationHandler();
//        lh.addOnLocationChangedListener(mapControl::updateCurrentUserLocation);

//        uds.addOnUpdateReceivedListener((userList, friendList) -> {
//            ArrayList<AbstractUser> arrayList;
//
//            if (userList == null || userList.isEmpty()) arrayList = new ArrayList<>();
//            else arrayList = new ArrayList<>(userList);
//
//            mapControl.updateFriends(friendList);
//            mapControl.updateUsers(arrayList);
//        });

        // disable zoom till fully loaded
//        viewAttacher.setZoomable(false);
        viewAttacher.setMaximumScale(9f);
        viewAttacher.setMediumScale(3f);
        viewAttacher.setMinimumScale(2f);

//        ViewTreeObserver vto = mapView.getViewTreeObserver();
//        vto.addOnGlobalLayoutListener(new ViewTreeObserver.OnGlobalLayoutListener() {
//            @Override
//            public void onGlobalLayout() {
//                mapView.getViewTreeObserver().removeOnGlobalLayoutListener(this);
//                viewAttacher.setZoomable(true);
////                mapView.getZoomControl().setScale(MapView.INITIAL_ZOOM, MapControl.MAIN_BUILDING_MAP.x, MapControl.MAIN_BUILDING_MAP.y, false);
//            }
//        });

        initListeners();

        Log.d("timing", "initMap() - complete");
    }


    private void initListeners() {
//        new Thread(() -> {
        // map initialized
        onMapInitializedListener = () -> {
            if (isUiThread()) Log.d("MapFrag.", "UI Thread!!!");
            else Log.d("MapFrag.", "other Thread.");
            hideViews();
        }; //this::hideViews;
        mapControl.addOnMapInitializedListener(onMapInitializedListener);

        // current user location
        LocationHandler lh = activity.getLocationHandler();
        // TODO lh.getCurrentLocation();
        lh.addOnLocationChangedListener(mapControl::updateCurrentUserLocation);

        // TODO call updateFriends / updateUsers if uds has data (getter needed)

        // Firebase updates
        onUpdateReceivedListener = (userList, friendList) -> {
            ArrayList<AbstractUser> arrayList;

            if (userList == null || userList.isEmpty()) arrayList = new ArrayList<>();
            else arrayList = new ArrayList<>(userList);

//            User u;
//            u = new User("123452336", "Hans-Peter");
//            u.setLatitude(51.028);
//            u.setLongitude(7.56174);
//            u.setCourse(Course.INF);
//            arrayList.add(u);

            mapControl.updateFriends(friendList);
            mapControl.updateUsers(arrayList);
        };
        activity.getUds().addOnUpdateReceivedListener(onUpdateReceivedListener);
//        }).start();
    }

    private void initLoadingScreen() {
        ProgressBar loadingCircle = fragmentView.findViewById(R.id.loadingCircle);

        // change progessbar color
        loadingCircle.getIndeterminateDrawable().setColorFilter(Color.BLACK, android.graphics.PorterDuff.Mode.MULTIPLY);
    }

    private void hideViews() {
        if (isUiThread()) {
            View loadingScreen = fragmentView.findViewById(R.id.loadingScreen);
            View loadingCircle = fragmentView.findViewById(R.id.loadingCircle);
            loadingCircle.setVisibility(View.GONE);
            loadingScreen.setVisibility(View.GONE);
            return;
        }
        activity.runOnUiThread(() -> {
            View loadingScreen = fragmentView.findViewById(R.id.loadingScreen);
            View loadingCircle = fragmentView.findViewById(R.id.loadingCircle);
            loadingCircle.setVisibility(View.GONE);
            loadingScreen.setVisibility(View.GONE);
        });
    }

    private boolean isUiThread() {
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.M) {
            return Looper.getMainLooper().isCurrentThread();
        } else {
            return Looper.getMainLooper().equals(Looper.myLooper());
        }
    }

    private void testData() {
        ArrayList<AbstractUser> users = new ArrayList<>();
        User u;
        u = new User("123452336", "Hans-Peter");
        u.setLatitude(51.028);
        u.setLongitude(7.56174);
        u.setCourse(Course.INF);
        users.add(u);
        mapControl.updateUsers(users);
    }

}
