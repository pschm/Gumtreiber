package de.psst.gumtreiber.data;

import android.app.Activity;
import android.app.Application;
import android.os.Bundle;
import android.util.Log;

import java.util.Random;

import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.map.MapView;

/**
 * Sends repeatedly the current users location to the server and
 * fetches available userdata from the server to the client for displaying it on the map.<br>
 * <br>
 * Take in mind that the users location is only send to the server
 * if given {@link LocationHandler#updatesEnabled} returns {@code true}.<br>
 * <br>
 * It will automatically start and stop syncing data when the activity gets stopped or started.
 */
public class UserDataSynchronizer implements Runnable, Application.ActivityLifecycleCallbacks {

    private Activity activity;
    private LocationHandler locationHandler;
    private MapView mapView;

    private long updateIntervalServer = 2500; //in ms

    private Thread updateThread;
    private boolean allowRunning = true;


    /**
     * Creates a new instance of UserDatasynchronizer.
     * @param activity Activity on which the ActivityLifecycleCallbacks are registered.
     * @param locationHandler LocationHandler from which this syncer will receive the location data.
     * @param mapView MapView on which the server data will transferred to.
     */
    public UserDataSynchronizer(Activity activity, LocationHandler locationHandler, MapView mapView) {
        this.activity = activity;
        this.locationHandler = locationHandler;
        this.mapView = mapView;

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
        updateThread.start();
    }

    /**
     * Stops synchronizing the data
     */
    public void stopUpdating() {
        allowRunning = false;
    }


    @Override
    public void run() {
        Log.d("UserDataSync", "Thread started!");

        Random rnd = new Random();
        float rngLong, rngLat;

        while(allowRunning) {

            //if(locationHandler.updatesEnabled()) {
                //Firebase.setCurrentLocation("0815", locationHandler.getCurrentLocation());
            //}

            rngLong = rnd.nextFloat() * 0.003f;
            rngLat = rnd.nextFloat() * 0.002f;
            Firebase.setCurrentLocation("123",7.563691 + rngLat,51.024161 + rngLong,  0);


            mapView.setUserList( Firebase.getAllUsers() );

            try {
                Thread.sleep(updateIntervalServer);
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
        Log.d("UserDataSyncer", "onActivityStarted: started updating!");
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
        Log.d("UserDataSyncer", "onActivityStopped: stopped updating!");
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
}
