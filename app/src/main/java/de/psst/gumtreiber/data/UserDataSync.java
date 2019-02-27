package de.psst.gumtreiber.data;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.GetTokenResult;

import java.util.ArrayList;
import java.util.HashMap;

import androidx.annotation.NonNull;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.map.MapControl;

/**
 * Sends repeatedly the current users location to the server and
 * fetches available userdata from the server to the client for displaying it on the map.<br>
 * <br>
 * Take in mind that the users location is only send to the server
 * if given {@link LocationHandler#updatesEnabled} returns {@code true}.<br>
 * <br>
 * It will automatically start and stop syncing data when the activity gets stopped or started.
 */
public class UserDataSync implements Runnable, Application.ActivityLifecycleCallbacks {

    private HashMap<String, AbstractUser> userList = new HashMap<>();

    private Activity activity;
    private LocationHandler locationHandler;
    private MapControl mapControl;

    private static String userToken;

    private Thread updateThread;
    private boolean allowRunning = true;


    /**
     * Creates a new instance of UserDataSync.
     * @param activity Activity on which the ActivityLifecycleCallbacks are registered.
     * @param locationHandler LocationHandler from which this sync will receive the location data.
     * @param mapControl MapView on which the server data will transferred to.
     */
    public UserDataSync(Activity activity, LocationHandler locationHandler, MapControl mapControl) {
        this.activity = activity;
        this.locationHandler = locationHandler;
        this.mapControl = mapControl;

        activity.getApplication().registerActivityLifecycleCallbacks(this);
    }

    /**
     * Starts synchronizing the data to the server and vice versa.
     */
    public void startUpdating() {
        allowRunning = true;

        if(updateThread != null && updateThread.isAlive()) {
            return;
        }

        updateThread = new Thread(this);

        //Get/Refresh auth token
        //TODO evtl. https://firebase.google.com/docs/auth/admin/manage-sessions
        if(FirebaseAuth.getInstance().getCurrentUser() != null) {
            FirebaseAuth.getInstance().getCurrentUser().getIdToken(true)
                    .addOnCompleteListener(new OnCompleteListener<GetTokenResult>() {
                        public void onComplete(@NonNull Task<GetTokenResult> task) {
                            if (task.isSuccessful()) {
                                userToken = task.getResult().getToken();

                                updateThread.start(); //Start syncing when token is there

                            } else {
                                task.getException().printStackTrace();
                            }
                        }
                    });
        }

    }

    public static String getUserToken() {
        return userToken;
    }

    @Override
    public void run() {
        Log.d("UserDataSync", "Sync-Thread started!");

        while(allowRunning) {
            FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();

            if(locationHandler.updatesEnabled() && user != null) {
                Firebase.setCurrentLocation(user, locationHandler.getCurrentLocation());
            }

            if(!TextUtils.isEmpty(userToken)) {
                Firebase.UpdateUserList(userToken, userList);
                ArrayList<String> friends = Firebase.getFriendlist(FirebaseAuth.getInstance().getUid(), userToken);
                UserFilter.setFriendList(friends);
                mapControl.updateFriends(friends);
                mapControl.updateUsers(new ArrayList<>(userList.values()));
            } else {
                Log.w("UserDataSync","Cannot get all users, Auth-Token is not available yet!");
            }


            try {
                Thread.sleep(LocationHandler.FASTEST_INTERVAL); //Sync as fast as the location updates can be occur
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
    }

    @Override
    public void onActivityCreated(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityStarted(Activity activity) {
        if(!activity.equals(this.activity)) return;
        Log.d("UserDataSync", "onActivityStarted: started updating!");
        startUpdating();
    }

    @Override
    public void onActivityResumed(Activity activity) {

    }

    @Override
    public void onActivityPaused(Activity activity) {

    }

    @Override
    public void onActivityStopped(Activity activity) {
        if(!activity.equals(this.activity)) return;
        Log.d("UserDataSync", "onActivityStopped: stopped updating!");
        stopUpdating();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle bundle) {

    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if(!activity.equals(this.activity)) return;

        activity.getApplication().unregisterActivityLifecycleCallbacks(this);
    }

    /**
     * Stops synchronizing the data
     */
    public void stopUpdating() {
        allowRunning = false;
        userToken = null;
    }
}
