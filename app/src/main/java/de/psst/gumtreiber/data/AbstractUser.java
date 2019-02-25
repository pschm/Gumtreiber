package de.psst.gumtreiber.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.psst.gumtreiber.map.MovableMarker;

public abstract class AbstractUser {
    private String uid;
    private String name;

    private double latitude;
    private double longitude;
    private double altitude;

    private Course course;

    //After this date the location data becomes invalid
    private Calendar expirationDate;

    //marker to show the position on the map
    private MovableMarker marker;

    public AbstractUser(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }


    public String getUid() {
        return uid;
    }

    public void setUid(String uid) {
        this.uid = uid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public double getLatitude() {
        return latitude;
    }

    public void setLatitude(double latitude) {
        this.latitude = latitude;
    }

    public double getLongitude() {
        return longitude;
    }

    public void setLongitude(double longitude) {
        this.longitude = longitude;
    }

    public double getAltitude() {
        return altitude;
    }

    public void setAltitude(double altitude) {
        this.altitude = altitude;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    public Calendar getExpirationDate() {
        return expirationDate;
    }

    public void setExpirationDate(Calendar expirationDate) {
        this.expirationDate = expirationDate;
    }

    public MovableMarker getMarker() {
        return marker;
    }

    public void setMarker(MovableMarker marker) {
        this.marker = marker;
    }
}
