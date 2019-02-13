package de.psst.gumtreiber.viewmodels;

import android.app.Application;

import java.util.ArrayList;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import de.psst.gumtreiber.data.Appointment;
import de.psst.gumtreiber.location.Room;

public class CalendarViewModel extends AndroidViewModel {

    private ArrayList<Appointment> appointments;

    public CalendarViewModel(@NonNull Application application) {
        super(application);
        fetchAppointments();
    }

    private void fetchAppointments() {
        //appointments = Firebase.getAppointments(FirebaseAuth.getInstance().getCurrentUser().getUid(), UserDataSync.getUserToken());
        //TODO Beispieldaten entfernen !!
        appointments = new ArrayList<>();
        appointments.add(new Appointment(20190123133000l, 20190123143001l, Room.R0300));
        appointments.add(new Appointment(20190123143002l, 20190123153003l, Room.R0300));
        appointments.add(new Appointment(20190123153004l, 20190123163005l, Room.R0300));
    }


    public ArrayList<Appointment> getAppointments() {
        return appointments;
    }

    //TODO
    public void saveAppointment() {

    }

    //TODO
    public void removeAppointment() {

    }

}
