package de.psst.gumtreiber.location;

import android.Manifest;
import android.app.Activity;
import android.app.Application;
import android.content.IntentSender;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResolvableApiException;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.location.LocationSettingsRequest;
import com.google.android.gms.location.LocationSettingsResponse;
import com.google.android.gms.location.SettingsClient;
import com.google.android.gms.tasks.OnFailureListener;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.android.gms.tasks.Task;

import java.util.ArrayList;
import java.util.List;

/**
 * A class which handles getting geographical location data using Google Play services.
 * It will automatically request the permission for accessing the location from the user if needed.
 *
 * You need to add {@code android:name="android.permission.ACCESS_FINE_LOCATION"} under the {@code uses-permission}
 * in the AndroidManifest so it works properly.
 */
public class LocationHandler implements Application.ActivityLifecycleCallbacks, ActivityCompat.OnRequestPermissionsResultCallback, GoogleApiClient.ConnectionCallbacks, GoogleApiClient.OnConnectionFailedListener {

    private AppCompatActivity activity;

    public static final long FASTEST_INTERVAL = 5000;
    public static final long INTERVAL = 10000;

    private static final int PERM_REQ_LASTLOC = 1;
    private static final int PERM_REQ_CURRLOC = 2;
    private static final int PERM_REQ_PLAYSERVICES = 3;

    private GoogleApiClient googleApiClient;
    private FusedLocationProviderClient fusedLocationClient;
    private LocationCallback locationCallback;
    private LocationRequest locationRequest;

    private List<OnLocationChangedListener> listeners;

    private Location currentLocation;
    private boolean updatesEnabled;


    /**
     * Creates an instance of this class. It will try to gather the device last know location
     * if updates are enabled and will start checking for new data repeatedly.
     * @param activity The Activity on which the LocationHandler will register its activity lifecycle callbacks
     * @param updatesEnabled Set true to gather the last know lactation and start checking for new location data
     */
    public LocationHandler(AppCompatActivity activity, boolean updatesEnabled) {
        this.activity = activity;
        this.updatesEnabled = updatesEnabled;
        activity.getApplication().registerActivityLifecycleCallbacks(this);



        googleApiClient = new GoogleApiClient.Builder(activity)
            .addConnectionCallbacks(this)
            .addOnConnectionFailedListener(this)
            .addApi(LocationServices.API)
            .build();


        fusedLocationClient = LocationServices.getFusedLocationProviderClient(activity);
        //if(updatesEnabled) updateLastLocation();

        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                if (locationResult == null) {
                    return;
                }
                for (Location location : locationResult.getLocations()) {
                    if(location == null) continue;
                    if(getCurrentLocation() == null) setCurrentLocation(location);

                    //Only Update the Location if its newer
                    if(location.getElapsedRealtimeNanos() <= getCurrentLocation().getElapsedRealtimeNanos()) {
                        setCurrentLocation(location);
                    }
                }
            }
        };
    }

    /**
     * Enables checking new location data
     */
    public void enableUpdates() {
        updatesEnabled = true;
        startLocationUpdates();
    }

    /**
     * Disables checking new location data
     */
    public void disableUpdates() {
        updatesEnabled = false;
        stopLocationUpdates();
    }

    /**
     * @return True if the handler is repeatedly checking for new location data
     */
    public boolean updatesEnabled() {
        return updatesEnabled;
    }

    /**
     * @return The last received location data
     */
    public Location getCurrentLocation() {
        return currentLocation;
    }


    private void setCurrentLocation(Location location) {
        currentLocation = location;
        if(listeners != null) {
            for (OnLocationChangedListener l: listeners) {
                l.onLocationChanged(location);
            }
        }
    }

    /**
     * Add a listener who will listen on receiving new location data
     * @param listener The listener to be added
     */
    public void addOnLocationChangedListener(OnLocationChangedListener listener) {
        if(listeners == null) listeners = new ArrayList<>();
        listeners.add(listener);
    }

    /**
     * Remove a listener who is listen to new location data
     * @param listener The listener to be removed
     */
    public void removeOnLocationChangedListener(OnLocationChangedListener listener) {
        if(listener == null) return;
        listeners.remove(listener);
    }


    private void startLocationUpdates() {
        Log.d("LocHandler", "startLocationUpdates");
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestGPSPermission(PERM_REQ_CURRLOC);
            return;
        }

        fusedLocationClient.requestLocationUpdates(locationRequest, locationCallback, null /* Looper */);
    }

    private void stopLocationUpdates() {
        if(fusedLocationClient != null && locationCallback != null) {
            fusedLocationClient.removeLocationUpdates(locationCallback);
        }
        setCurrentLocation(null);
    }


    @Override
    public void onActivityCreated(Activity activity, Bundle savedInstanceState) {
        //if(!activity.equals(this.activity)) return;
    }

    @Override
    public void onActivityStarted(Activity activity) {
        if(!activity.equals(this.activity)) return;

        //Connect the GoogleApiClient instance to Google Play Services
        googleApiClient.connect();
    }

    @Override
    public void onActivityResumed(Activity activity) {
        if(!activity.equals(this.activity)) return;

        if(updatesEnabled) {
            startLocationUpdates();
        }
    }

    @Override
    public void onActivityPaused(Activity activity) {
        if(!activity.equals(this.activity)) return;

        stopLocationUpdates();
    }

    @Override
    public void onActivityStopped(Activity activity) {
        if(!activity.equals(this.activity)) return;

        //GoogleApiClient is disconnected in order to free resources
        googleApiClient.disconnect();
    }

    @Override
    public void onActivitySaveInstanceState(Activity activity, Bundle outState) {
        //if(!activity.equals(this.activity)) return;
    }

    @Override
    public void onActivityDestroyed(Activity activity) {
        if(!activity.equals(this.activity)) return;

        activity.getApplication().unregisterActivityLifecycleCallbacks(this);
    }



    //Provides callbacks that are called when the client is connected or disconnected from the service.
    //Most applications implement onConnected(Bundle) to start making requests.
    //https://developer.android.com/training/location/change-location-settings#java
    @Override
    public void onConnected(@Nullable Bundle bundle) {
        Log.d("PlayServices", "onConnected");
        locationRequest = createLocationRequest();

        //Get the current location settings of a user's device
        //Add one or more location requests
        LocationSettingsRequest.Builder builder = new LocationSettingsRequest.Builder().addLocationRequest(locationRequest);

        //Check whether the current location settings are satisfied
        SettingsClient client = LocationServices.getSettingsClient(activity);
        Task<LocationSettingsResponse> task = client.checkLocationSettings(builder.build());


        task.addOnSuccessListener(activity, new OnSuccessListener<LocationSettingsResponse>() {
            @Override
            public void onSuccess(LocationSettingsResponse locationSettingsResponse) {
                // All location settings are satisfied. The client can initialize
                // location requests here.
                if(updatesEnabled) startLocationUpdates();
            }
        });

        task.addOnFailureListener(activity, new OnFailureListener() {
            @Override
            public void onFailure(@NonNull Exception e) {
                if (e instanceof ResolvableApiException) {
                    // Location settings are not satisfied, but this can be fixed
                    // by showing the user a dialog.
                    try {
                        // Show the dialog by calling startResolutionForResult(),
                        // and check the result in onActivityResult().

                        Log.d("PlayServices", "ResolvableApiException");
                        ResolvableApiException resolvable = (ResolvableApiException) e;
                        resolvable.startResolutionForResult(activity, PERM_REQ_PLAYSERVICES);
                    } catch (IntentSender.SendIntentException sendEx) {
                        // Ignore the error.
                    }
                    Log.e("PlayServices","Failed on creating task");
                }
            }
        });

    }

    @Override
    public void onConnectionSuspended(int i) {

    }

    @Override
    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
        //TODO?
    }


    //https://developer.android.com/training/location/change-location-settings
    private LocationRequest createLocationRequest() {
        LocationRequest locationRequest = new LocationRequest();
        locationRequest.setInterval(INTERVAL); //Sets the rate in milliseconds at which your app prefers to receive location updates. Can be faster.
        locationRequest.setFastestInterval(FASTEST_INTERVAL); //Sets the fastest rate in milliseconds at which your app can handle location updates.
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY); //Sets the priority of the request, which gives the Google Play services location services a strong hint about which location sources to use.
        return locationRequest;
    }



    private void updateLastLocation() {
        Log.d("LocHandler", "updateLastLocation");
        if (ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            requestGPSPermission(PERM_REQ_LASTLOC);
            return;
        }


        fusedLocationClient.getLastLocation()
            .addOnSuccessListener(activity, new OnSuccessListener<Location>() {
                @Override
                public void onSuccess(Location location) {
                    // Got last known location. In some rare situations this can be null.
                    if (location != null) {
                        // Logic to handle location object
                        setCurrentLocation(location);
                    }
                }
            })
        ;
    }



    //Permission
    @Override
    public void onRequestPermissionsResult(int requestCode, @NonNull String permissions[], @NonNull int[] grantResults) {
        //switch requestCode to "continue" at the right point after perm grand
        switch (requestCode) {
            case PERM_REQ_LASTLOC: {
                // If request is cancelled, the result arrays are empty.
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    // permission was granted, yay! Do the
                    // contacts-related task you need to do.
                    updateLastLocation();
                } else {
                    // permission denied, boo! Disable the
                    // functionality that depends on this permission.
                    setCurrentLocation(null);
                }
            }

            case PERM_REQ_CURRLOC: {
                if(grantResults.length > 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    enableUpdates();
                } else {
                    setCurrentLocation(null);
                }
            }

        }
    }

    private void requestGPSPermission(int requestCode) {
        if(ContextCompat.checkSelfPermission(activity, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            // Permission is not granted
            disableUpdates();
            // Should we show an explanation?
            if(ActivityCompat.shouldShowRequestPermissionRationale(activity, Manifest.permission.ACCESS_FINE_LOCATION)) {
                // Show an explanation to the user *asynchronously* -- don't block
                // this thread waiting for the user's response! After the user
                // sees the explanation, try again to request the permission.
                new LocationPermissionDialogFragment().show(activity.getSupportFragmentManager(), "fragment_gps_permission");

            } else {
                // No explanation needed; request the permission
                ActivityCompat.requestPermissions(activity, new String[]{Manifest.permission.ACCESS_FINE_LOCATION}, requestCode);

                // requestCode is an app-defined int constant. The callback method gets the result of the request.
            }
        }
    }


    /**
     * Listen on this interface to receive the updated location data when location updates are enabled
     */
    public interface OnLocationChangedListener {
        void onLocationChanged(Location location);
    }


}
