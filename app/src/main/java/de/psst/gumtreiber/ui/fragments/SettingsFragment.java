package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.ui.MainActivity;
import de.psst.gumtreiber.ui.fragments.settings.SettingsManipulatorEmail;
import de.psst.gumtreiber.ui.fragments.settings.SettingsManipulatorPwd;

public class SettingsFragment extends PreferenceFragmentCompat {

    private MainActivity activity;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = (MainActivity) getActivity();
        return super.onCreateView(inflater, container, savedInstanceState);
    }

    @Override
    public void onDestroyView() {
        activity.resetActionBarTitle();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setActionBarTitle(getString(R.string.title_settings));

        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
        initSummaries(user);
    }

    private void initSummaries(FirebaseUser user) {

        if(user == null) findPreference("email").setSummary("INVALID");
        else findPreference("email").setSummary(user.getEmail());

        if(user == null) findPreference("nickname").setSummary("INVALID");
        else findPreference("nickname").setSummary(user.getDisplayName());

        if(user == null) findPreference("course").setSummary("INVALID");
        else findPreference("course").setSummary("TODO"); //TODO


        findPreference("version").setSummary(getString(R.string.app_version));
    }

    @Override
    public boolean onPreferenceTreeClick(Preference pref) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) return false;

        switch(pref.getKey()) {
            //Account
            case "email":
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsManipulatorEmail()).addToBackStack(null).commit();
                break;
            case "password":
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsManipulatorPwd()).addToBackStack(null).commit();
                break;
            case "nickname":

                break;
            case "course":

                break;

            //Filter
            //...

            //Info
            case "version":

                break;
            default:
                break;
        }

        return super.onPreferenceTreeClick(pref);
    }
}
