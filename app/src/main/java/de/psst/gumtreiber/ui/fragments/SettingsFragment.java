package de.psst.gumtreiber.ui.fragments;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProviders;
import androidx.preference.Preference;
import androidx.preference.PreferenceFragmentCompat;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.UserFilter;
import de.psst.gumtreiber.ui.MainActivity;
import de.psst.gumtreiber.ui.fragments.settings.SettingsManipulatorCourse;
import de.psst.gumtreiber.ui.fragments.settings.SettingsManipulatorEmail;
import de.psst.gumtreiber.ui.fragments.settings.SettingsManipulatorNickname;
import de.psst.gumtreiber.ui.fragments.settings.SettingsManipulatorPwd;
import de.psst.gumtreiber.viewmodels.SettingsViewModel;

public class SettingsFragment extends PreferenceFragmentCompat {

    private MainActivity activity;
    private static final String FRAGMENT_FLAG = "SETTINGS";
    private SettingsViewModel model;

    @Override
    public void onCreatePreferences(Bundle savedInstanceState, String rootKey) {
        setPreferencesFromResource(R.xml.preferences, rootKey);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        activity = Objects.requireNonNull((MainActivity) getActivity());
        model = ViewModelProviders.of(activity).get(SettingsViewModel.class);
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
        initSummaries();
    }

    private void initSummaries() {

        if (model.getEmail() == null) findPreference("email").setSummary(R.string.invalid);
        else findPreference("email").setSummary(model.getEmail());

        if (model.getNickname() == null) findPreference("nickname").setSummary(R.string.invalid);
        else findPreference("nickname").setSummary(model.getNickname());

        if (model.getCourse() == null) findPreference("course").setSummary(R.string.invalid);
        else findPreference("course").setSummary(model.getCourse().toString());


        findPreference("version").setSummary(getString(R.string.app_version));
    }

    @Override
    public boolean onPreferenceTreeClick(Preference pref) {
        FragmentManager fragmentManager = getFragmentManager();
        if (fragmentManager == null) return false;

        switch(pref.getKey()) {
            //Account
            case "email":
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsManipulatorEmail()).addToBackStack(FRAGMENT_FLAG).commit();
                break;
            case "password":
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsManipulatorPwd()).addToBackStack(FRAGMENT_FLAG).commit();
                break;
            case "nickname":
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsManipulatorNickname()).addToBackStack(FRAGMENT_FLAG).commit();
                break;
            case "course":
                fragmentManager.beginTransaction().replace(R.id.content_frame, new SettingsManipulatorCourse()).addToBackStack(FRAGMENT_FLAG).commit();
                break;

            //Filter
            case "check_box_preference_1":
                UserFilter.FRIEND_FILTER = pref.getSharedPreferences().getBoolean("check_box_preference_1", true);
                break;
            case "check_box_preference_2":
                UserFilter.BOT_FILTER = pref.getSharedPreferences().getBoolean("check_box_preference_2", true);
                break;
            case "check_box_preference_3":
                UserFilter.INF_FILTER = pref.getSharedPreferences().getBoolean("check_box_preference_3", true);
                break;
            case "check_box_preference_4":
                UserFilter.ING_FILTER = pref.getSharedPreferences().getBoolean("check_box_preference_4", true);
                break;
            case "check_box_preference_5":
                UserFilter.PROF_FILTER = pref.getSharedPreferences().getBoolean("check_box_preference_5", true);
                break;
            case "check_box_preference_6":
                UserFilter.NONE_FILTER = pref.getSharedPreferences().getBoolean("check_box_preference_6", true);
                break;

            //Info
            case "version":

                break;
            default:
                break;
        }

        return super.onPreferenceTreeClick(pref);
    }
}
