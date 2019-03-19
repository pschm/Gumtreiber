package de.psst.gumtreiber.data;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

public class Bot extends AbstractUser {

    public Bot(String uid, String name) {
        super(uid, name);
    }

//    @Override
//    public String toString() {
//        @SuppressLint("SimpleDateFormat")
//        DateFormat dateFormat = new SimpleDateFormat("yyyy-MM-dd HH:mm:ss");
//        String s = dateFormat.format(getExpirationDate().getTime());
//
//        return getUid() + ": " + getName() + " "+ getAltitude()+ " " + getLongitude() + " " + getLatitude() + " "+ s;
//    }
}
