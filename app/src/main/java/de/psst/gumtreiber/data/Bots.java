package de.psst.gumtreiber.data;

import android.location.Location;

import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;


public class Bots {

    private static final String firebaseURL = "https://gumtreiber-1fb84.firebaseio.com";
    private static final DatabaseReference database = FirebaseDatabase.getInstance().getReference();

    public static void createBot(String botID, String name) {
        database.child("bots").child(botID).child("name").setValue(name);
    }

    public static void addRoutineToBot(String botID, long startTime, long endTime, Location location) {
        addRoutineToBot(botID,startTime,endTime,location.getLatitude(),location.getLongitude(), location.getAltitude());
    }

    public static void addRoutineToBot(String botID, long startTime, long endTime, double latitude, double longitude, double altitude) {

        database.child("bots").child(botID).child("routines").child(""+startTime).child("startTime").setValue(startTime);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("endTime").setValue(endTime);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("latitude").setValue(latitude);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("longitude").setValue(longitude);
        database.child("bots").child(botID).child("routines").child(""+startTime).child("altitude").setValue(altitude);
    }


}
