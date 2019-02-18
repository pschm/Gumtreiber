package de.psst.gumtreiber.data;

public enum Course {

    //TODO Alle Studiegänge reintun, ich habe bisher nur die Bachelor Inf Studiengänge reingetan
    AI("INF", "Allgemeie Informatik"),
    MI("INF","Medieninformatik"),
    TI("INF", "Technische Informatik"),
    WI("INF", "Wirtschaftsinformatik"),
    ITM("INF", "IT-Management");


    //Attribute
    private final String type;
    private final String fullName;

    //Konstruktor
    Course(String type, String fullName) {
        this.type = type;
        this.fullName = fullName;
    }


}
