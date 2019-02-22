package de.psst.gumtreiber.ui.fragments.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.psst.gumtreiber.R;

public class SettingsManipulatorCourse extends SettingsManipulatorFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLabels();
        initCompleteButton();
    }

    private void initLabels() {
        txtTitle.setText(getString(R.string.change_course));
        txtUserInput1.setHint("TODO");
        //TODO Liste mit verfügbaren Studiengängen anzeigen
    }

    private void initCompleteButton() {
        //Set listener on "FERTIG" button
        setToolbarBtnListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                if(true) { //TODO Wenn keine Auswahl getroffen, oder nichts geändert ist:
                    Toast.makeText(activity, "Funktionalität noch nicht verfügbar!", Toast.LENGTH_SHORT).show();
                    return false;
                } else {

                    //TODO Studiengang entsprechend der Auswahl ändern


                    Toast.makeText(activity, getString(R.string.update_successful), Toast.LENGTH_SHORT).show(); //TODO updated das auch bei anderen nutzern "sofort"
                    activity.onBackPressed();
                }

                return true;
            }
        });
    }
}
