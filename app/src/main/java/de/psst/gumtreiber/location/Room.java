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
    R3100(51.023025, 7.562189, 3,"Seminar 6"),
    R3101(51.023000, 7.562344, 3, "Seminarsaal IV"),
    R3102(51.023117, 7.562346, 3, "euro engineering  Saal"),
    R3103(51.023227, 7.562328, 3, "Seminar 5"),
    R3104(51.023293, 7.562339, 3, "Seminar 4"),
    R3105(51.023323, 7.562305, 3, "UV/AV"),
    R3106(51.023423, 7.562317, 3, "FISIA BABCOCK Saal"),
    R3107(51.023520, 7.562307, 3, "Unitechnik Saal"),
    R3108(51.023597, 7.562282, 3, "Seminar 1"),
    R3109(51.023694, 7.562278, 3, "Seminarraum"),
    R3110(51.023639, 7.562138, 3, "Seminarraum"),
    R3111(51.023579, 7.562137, 3, "Seminarraum"),
    R3112(51.023483, 7.562122, 3, "Seminarraum"),
    R3113(51.023391, 7.562150, 3, "Mathe-PC-Pool"),
    R3114(51.023360, 7.562156, 3, "Mathe-Mitarb."),
    R3115(51.023335, 7.562190, 3, "Beh-WC"),
    R3116(51.023330, 7.562137, 3, "EDV-Raum"),
    R3117(51.023319, 7.562193, 3, "D-WC"),
    //R3118(,, 3,""),
    R3119(51.023281, 7.562166, 3,"H-WC"),

    R2100(51.023015, 7.562175, 2, "Seminar 7"),
    R2101(51.022975, 7.562340, 2, "Übungsraum"),
    R2102(51.023020, 7.562354, 2, "Übungsraum"),
    R2103(51.023039, 7.562359, 2, "Übungsraum"),
    R2104(51.023099, 7.562312, 2, "Übungsraum"),
    R2105(51.023165, 7.562347, 2, "SLZ"),
    R2106(51.023261, 7.562320, 2, "WI-PC-Pool I"),
    R2107(51.023444, 7.562314, 2, "WI-PC-Pool II"),
    R2108(51.023587, 7.562308, 2, "Medienraum"),
    R2109(51.023714, 7.562263, 2, "ADV-Terminalraum II"),
    R2110(51.023636, 7.562121, 2, "ADV-Terminalraum I"),
    R2111(51.023580, 7.562103, 2, "Supervisor"),
    R2112(51.023517, 7.562129, 2, "PC-Pool Mathe Ing."),
    R2113(51.023456, 7.562151, 2, "Seminarraum"),
    R2114(51.023372, 7.562142, 2, "Seminarraum"),
    R2115(51.023326, 7.562188, 2, "UV/AV"),
    R2116(51.023318, 7.562193, 2, "D-WC"),
    //R2117(, 2, ""),
    R2118(51.023288, 7.562157, 2, "H-WC"),
    //TODO restliche Etagen

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
