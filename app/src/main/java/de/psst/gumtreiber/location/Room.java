package de.psst.gumtreiber.location;

import android.location.Location;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

/**
 * Enum representation of the rooms.
 * Each room has its geographical coordinates assigned.
 */
public enum Room {

    //Hauptgebäude Ost-Trakt x.1xx
    //TODO Schaun, ob wir das so machen wollen, dann Fleißarbeit.

    //Hauptgebäude West-Trakt x.2xx
    //TODO Schaun, ob wir das so machen wollen, dann Fleißarbeit.

    //Block Mensa x.3xx + x.4xx
    R0300(51.0220919, 7.562135, 0, "Mensa"),
    R0301(51.022124, 7.562727, 0, "Mensa Essensausgabe"),
    R0401(51.022025, 7.562357, 0, "FERCHAU Saal"),
    R0402(51.022145, 7.562322, 0, "FERCHAU Saal"),
    R0405(51.022318, 7.562288, 0, "BPW Saal"),
    R1400(51.022318, 7.562288, 1, "OPITZ CONSULTING Saal"),
    R1301(51.022124, 7.562727, 1, "Bib");

    //Ferchau-Gebäude LC6 x.5xx
    //TODO Schaun, ob wir das so machen wollen, dann Fleißarbeit.

    private String name;
    private double latitude, longitude, altitude;

    Room(double latitude, double longitude, double altitude) {
        this(latitude, longitude, altitude, null);
    }

    Room(double latitude, double longitude, double altitude, String name) {
        this.name = name;
        this.latitude = latitude;
        this.longitude = longitude;
        this.altitude = altitude;
    }


    public static String[] getAllRooms() {
        Room[] rooms = Room.values();

        String[] allRooms = new String[rooms.length];
        for(int i = 0; i < allRooms.length; i++) {
            allRooms[i] = "R" + rooms[i].getNumberDot() + " (" + rooms[i].getName() + ")";
        }

        return allRooms;
    }



    /**
     * @return Number of this room without the leading R.
     */
    public String getNumber() {
        return name().substring(1);
    }

    /**
     * @return Number of this room without the leading R but with the dot-convention.
     */
    public String getNumberDot() {
        return getNumber().substring(0,1) + "." + getNumber().substring(1);
    }

    /**
     * @return Number of the building in witch this room in in. (e.g. 3=Mensa, ...)
     */
    public String getBuildingNumber() {
        return getNumber().substring(1,2);
    }

    /**
     * @return Name of this room if it has one, {@code null} otherwise.
     */
    @Nullable
    public String getName() {
        return name;
    }

    /**
     * @return Latitude of this room.
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return Longitude of this room.
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return Altitude of this room. Currently: 0 = EG, 1 = 1.OG, ...
     */
    public double getAltitude() {
        return altitude;
    }

    /**
     * @return Location object with lat, long and altitude of this room.
     */
    public Location getLocation() {
        Location location = new Location("Room");
        location.setLatitude(latitude);
        location.setLongitude(longitude);
        location.setAltitude(altitude);

        return location;
    }

    @Override
    @NonNull
    public String toString() {
        return name;

    }
}
