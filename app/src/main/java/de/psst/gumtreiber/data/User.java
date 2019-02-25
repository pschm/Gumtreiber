package de.psst.gumtreiber.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

public class User extends AbstractUser {

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");
    private boolean usingSchedule;


    public User(String uid, String name) {
        super(uid, name);
    }

    public boolean isExpired() {

        Calendar cal = Calendar.getInstance();
        cal.getTime();

        long now = Long.parseLong(dateFormat.format(cal.getTime()));
        long expiration = Long.parseLong(dateFormat.format(getExpirationDate().getTime()));

        boolean isExpired = expiration < now;

        return isExpired;
    }

    public boolean isUsingSchedule() {
        return usingSchedule;
    }

    public void setUsingSchedule(boolean usingSchedule) {
        this.usingSchedule = usingSchedule;
    }

    /*
    public String toString() {

        String s = dateFormat.format(expirationDate.getTime());

        return uid + ": " + name + " "+ altitude+ " " + longitude + " " + latitude + " "+ s;
    }
    */
}

