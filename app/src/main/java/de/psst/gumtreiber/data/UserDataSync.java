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
import java.util.Collection;
import java.util.HashMap;

import androidx.annotation.NonNull;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.ui.MainActivity;

/**
 * Sends repeatedly the current users location to the server and
 * fetches available userdata from the server to the client.<br>
 * <br>
 * Take in mind that the users location is only send to the server
 * if given {@link LocationHandler#updatesEnabled} returns {@code true}.<br>
 * <br>
 * It will automatically start and stop syncing data when the activity gets stopped or started.
 * <br>
 * To receive updated data, register an {@link OnUpdateReceivedListener} listener.
 */
public class UserDataSync implements Runnable, Application.ActivityLifecycleCallbacks {

    private HashMap<String, AbstractUser> userList = new HashMap<>();
    private ArrayList<OnUpdateReceivedListener> listeners = new ArrayList<>();

    private Activity activity;
    private LocationHandler locationHandler;

    private static String userToken = "";

    private Thread updateThread;
    private boolean allowRunning = true;


    /**
     * Creates a new instance of UserDataSync.
     * @param activity Activity on which the ActivityLifecycleCallbacks are registered.
     * @param locationHandler LocationHandler from which this sync will receive the location data.
     * @param enableUpdating Set to {@code true} to immediately start sending and receiving data.
     */
    public UserDataSync(Activity activity, LocationHandler locationHandler, boolean enableUpdating) {
        this.activity = activity;
        this.locationHandler = locationHandler;

        activity.getApplication().registerActivityLifecycleCallbacks(this);

        if(enableUpdating) startUpdating();
        else stopUpdating();
    }


    /**
     * Add a listener who will listen on receiving data updates
     * @param listener The listener to be added
     */
    public void addOnUpdateReceivedListener(OnUpdateReceivedListener listener) {
        if(listeners == null) listeners = new ArrayList<>();
        listeners.add(listener);
    }

    /**
     * Remove a listener who is listen to  data updates
     * @param listener The listener to be removed
     */
    public void removeOnUpdateReceivedListener(OnUpdateReceivedListener listener) {
        if(listener == null) return;
        listeners.remove(listener);
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

            Log.d("UserDataSync", "Checking Internet connection");
            if(!Firebase.isNetworkAvailable()){
                ((MainActivity) activity).returnToLogin();
            }
            Log.d("UserDataSync", "Internet available");

            if(locationHandler.updatesEnabled() && user != null) {
                Firebase.setCurrentLocation(user, locationHandler.getCurrentLocation());
            }

            if(!TextUtils.isEmpty(userToken) && user != null) {
                Firebase.updateUserList(userToken, userList); //Updating the User-Hash-Map

                ArrayList<String> friends = Firebase.getFriendlist(user.getUid(), userToken);
                UserFilter.setFriendList(friends);


                if(listeners != null) {
                    for(OnUpdateReceivedListener l: listeners) {
                        l.onUpdateReceived(userList.values(), friends);
                    }
                }

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
//        startUpdating();
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
        userToken = "";
    }


    /**
     * Listen on this interface to receive the updated location data when location updates are enabled
     */
    public interface OnUpdateReceivedListener {
        void onUpdateReceived(Collection<AbstractUser> userList, ArrayList<String> friendList);
    }
}
