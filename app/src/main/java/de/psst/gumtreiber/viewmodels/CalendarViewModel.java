package de.psst.gumtreiber.viewmodels;

import android.app.Application;

import java.util.List;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import de.psst.gumtreiber.data.Appointment;

public class CalendarViewModel extends AndroidViewModel {

    private List<Appointment> appointments;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        fetchAppointments();
    }

    private void fetchAppointments() {
        // appointments = Firebase.getAppointments(FirebaseAuth.getInstance().getCurrentUser().getUid(),FirebaseAuth.getInstance().getCurrentUser().getUserToken());
    }


    public void getAppointments() {

    }

}
