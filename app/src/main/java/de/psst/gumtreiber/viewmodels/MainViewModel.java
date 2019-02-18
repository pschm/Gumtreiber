package de.psst.gumtreiber.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class MainViewModel extends AndroidViewModel {

    //Shared Preferences Key's
    private static final String PREFERENCES_KEY = "de.psst.gumtreiber";
    private static final String LOCATION_STATE_KEY = "location";

    //Location State of the User
    private Boolean locationState;


    public MainViewModel(@NonNull Application application) {
        super(application);
        fetchLocationStatus();
    }


    private SharedPreferences getPreferences() {
        return getApplication().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }


    private void fetchLocationStatus() {
        locationState = getPreferences().getBoolean(LOCATION_STATE_KEY, true);
    }

    /**
     * Returns the saved LocationState
     *
     * @return LocationState
     */
    public Boolean getLocationState() {
        return locationState;
    }

    /**
     * Setting new location state
     *
     * @param newLocationState the new LocationState
     */
    public void setLocationState(Boolean newLocationState) {
        getPreferences().edit().putBoolean(LOCATION_STATE_KEY, newLocationState).apply();
        fetchLocationStatus();
    }

}
