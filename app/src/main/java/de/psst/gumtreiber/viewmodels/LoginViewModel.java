package de.psst.gumtreiber.viewmodels;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;

public class LoginViewModel extends AndroidViewModel {

    //Shared Preferences Key's
    private static final String PREFERENCES_KEY = "de.psst.gumtreiber";
    private static final String EMAIL_KEY = "UserEmail";
    private static final String PASSWORD_KEY = "UserPassword";
    private static final String SAVE_STATE_KEY = "UserSaveState";


    //Location State of the User
    private String email;
    private String password;
    private boolean saveState;


    public LoginViewModel(@NonNull Application application) {
        super(application);
        fetchEmail();
        fetchPassword();
        fetchSaveState();
    }


    private SharedPreferences getSharedPreferences() {
        return getApplication().getSharedPreferences(PREFERENCES_KEY, Context.MODE_PRIVATE);
    }

    //Email address

    /**
     * Fetches the saved email-address into the email variable
     */
    private void fetchEmail() {
        email = getSharedPreferences().getString(EMAIL_KEY, "");
    }

    /**
     * Returns the saved email-address
     *
     * @return LocationState
     */
    public String getEmail() {
        return email;
    }

    /**
     * Saving a new Email address in the Shared Preference
     *
     * @param email the new email address
     */
    public void setEmail(String email) {
        getSharedPreferences().edit().putString(EMAIL_KEY, email).apply();
        fetchEmail();
    }

    /**
     * Removes the saved Email
     */
    public void removeEmail() {
        setEmail("");
        fetchEmail();
    }

    //Password

    /**
     * Fetches the saved password into the password variable
     */
    private void fetchPassword() {
        password = getSharedPreferences().getString(PASSWORD_KEY, "");
    }

    /**
     * Returns the saved password
     *
     * @return password
     */
    public String getPassword() {
        return password;
    }

    /**
     * Saving a new Password in the Shared Preference
     *
     * @param password the new password
     */
    public void setPassword(String password) {
        getSharedPreferences().edit().putString(PASSWORD_KEY, password).apply();
        fetchPassword();
    }

    /**
     * Removes the saved password
     */
    public void removePassword() {
        setPassword("");
        fetchPassword();
    }

    //SaveState
    private void fetchSaveState() {
        saveState = getSharedPreferences().getBoolean(SAVE_STATE_KEY, false);
    }

    public boolean getSaveState() {
        return saveState;
    }

    public void setSaveState(boolean saveState) {
        getSharedPreferences().edit().putBoolean(SAVE_STATE_KEY, saveState).apply();
        fetchSaveState();
    }
}
