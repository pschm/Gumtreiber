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
    private String uid;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        uid = FirebaseAuth.getInstance().getCurrentUser().getUid();
        fetchAppointments();
    }

    /**
     * Fetches a lilst of appointments from Firebase into the appointments variable
     */
    private void fetchAppointments() {
        appointments = Firebase.getAppointments(uid, UserDataSync.getUserToken());
    }

    /**
     * Saves a new Appointment in the Firebase
     *
     * @param appointment
     */
    public void saveAppointment(Appointment appointment) {
        Firebase.addAppointmentToSchedule(uid, appointment);
        fetchAppointments();
    }

    /**
     * Removes an Appointment from Firebase
     *
     * @param appointment
     */
    public void removeAppointment(Appointment appointment) {
        Firebase.deleteAppointment(uid, appointment);
        fetchAppointments();
    }


    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }


}
