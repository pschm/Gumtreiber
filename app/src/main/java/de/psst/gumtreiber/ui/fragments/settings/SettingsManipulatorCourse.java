package de.psst.gumtreiber.ui.fragments.settings;

import android.os.Bundle;
import android.view.MenuItem;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Course;

public class SettingsManipulatorCourse extends SettingsManipulatorFragment {


    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        initLabels();
        initCompleteButton();
    }

    private void initLabels() {

        txtTitle.setText(getString(R.string.change_course));
        txtUserInput1.setVisibility(View.GONE);

        spinner.setVisibility(View.VISIBLE);
        spinner.setAdapter(new ArrayAdapter<>(activity, android.R.layout.simple_spinner_dropdown_item, Course.getAllCourses()));
        spinner.setSelection(model.getCourse().ordinal());

    }

    private void initCompleteButton() {
        //Set listener on "FERTIG" button
        setToolbarBtnListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                Course course = Course.values()[spinner.getSelectedItemPosition()];

                // course not changed
                if (model.getCourse().equals(course)) return true;

                model.setCourse(course);

                Toast.makeText(activity, getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
                    activity.onBackPressed();


                return true;
            }
        });
    }
}
