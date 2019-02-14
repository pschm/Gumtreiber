package de.psst.gumtreiber.viewmodels;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import de.psst.gumtreiber.data.Appointment;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.data.UserDataSync;

public class CalendarViewModel extends AndroidViewModel {

    private ArrayList<Appointment> appointments;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        fetchAppointments();
    }

    private void fetchAppointments() {
        appointments = Firebase.getAppointments(FirebaseAuth.getInstance().getCurrentUser().getUid(), UserDataSync.getUserToken());
    }


    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    //TODO
    public void saveAppointment(Appointment appointment) {
        Firebase.deleteAppointment(FirebaseAuth.getInstance().getCurrentUser().getUid(), appointment);

    }

    //TODO
    public void removeAppointment(Appointment appointment) {
        Firebase.addAppointmentToSchedule(FirebaseAuth.getInstance().getCurrentUser().getUid(), appointment);

    }

}
