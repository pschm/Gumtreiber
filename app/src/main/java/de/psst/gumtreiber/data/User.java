package de.psst.gumtreiber.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class User {

    public String imei;
    public String name;

    public double latitude;
    public double longitude;
    public double altitude;

    //After this date the location data becomes invalid
    public Calendar expirationDate;

    //true, if the user uses a Schedule
    public boolean usingSchedule;



    public User(String imei, String name) {
        this.imei = imei;
        this.name = name;
    }

    public String toString() {
        DateFormat dateFormat = new SimpleDateFormat("/yyyy/MM/dd HH:mm:ss");
        String s = dateFormat.format(expirationDate.getTime());

        return imei + ": " + name + " "+ altitude+ "" + longitude + " " + latitude + " "+ s;
    }



}

