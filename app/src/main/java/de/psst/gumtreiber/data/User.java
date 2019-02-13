package de.psst.gumtreiber.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.psst.gumtreiber.map.MovableMarker;

public class User {

    public String uid;
    public String name;

    public double latitude;
    public double longitude;
    public double altitude;

    //After this date the location data becomes invalid
    public Calendar expirationDate;
    DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    //true, if the user uses a Schedule
    public boolean usingSchedule;

    //marker to show the position on the map
    private MovableMarker marker;


    public User(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public boolean isExpired() {

        Calendar cal = Calendar.getInstance();
        cal.getTime();

        long now = Long.parseLong(dateFormat.format(cal.getTime()));
        long expiration = Long.parseLong(dateFormat.format(expirationDate.getTime()));

        boolean isExpired = expiration < now;

        return isExpired;
    }

    public String toString() {

        String s = dateFormat.format(expirationDate.getTime());

        return uid + ": " + name + " "+ altitude+ " " + longitude + " " + latitude + " "+ s;
    }

    public MovableMarker getMarker() {
        return marker;
    }

    public void setMarker(MovableMarker marker) {
        this.marker = marker;
    }
}

