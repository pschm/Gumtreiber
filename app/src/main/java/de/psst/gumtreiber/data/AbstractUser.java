package de.psst.gumtreiber.data;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Calendar;

import de.psst.gumtreiber.map.MovableMarker;

public abstract class AbstractUser {
    private String uid;
    private String name;

    //Location Data
    private double latitude;
    private double longitude;
    private double altitude;

    private Course course;

    //After this date the location data becomes invalid
    private Calendar expirationDate;

    //Flag for checking if the user should be drawn on the map
    private boolean isVisible = true;

    //marker to show the position on the map
    private MovableMarker marker;

    private final static DateFormat dateFormat = new SimpleDateFormat("yyyyMMddHHmmss");

    public AbstractUser(String uid, String name) {
        this.uid = uid;
        this.name = name;
    }

    public boolean isExpired() {

        Calendar cal = Calendar.getInstance();
        cal.getTime();

        long now = Long.parseLong(dateFormat.format(cal.getTime()));
        long expiration = Long.parseLong(dateFormat.format(getExpirationDate().getTime()));

        boolean isExpired = expiration < now;

        return isExpired;
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

    public boolean isVisible() {
        return isVisible;
    }

    public void setVisible(boolean visible) {
        isVisible = visible;
    }
}
