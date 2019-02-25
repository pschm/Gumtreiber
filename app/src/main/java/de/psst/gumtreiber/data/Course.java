package de.psst.gumtreiber.data;

public enum Course {

    //TODO Alle Studiegänge reintun, ich habe bisher nur die Bachelor Inf Studiengänge reingetan
    /*Empfehlung:
        NONE("Keiner"),
        INF("Informatik"),
        MB("Maschienenbau")
     */

    NONE("Keiner", "Keiner"),
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


    public static String[] getAllCourses() {
        Course[] courses = Course.values();

        String[] allCourses = new String[courses.length];
        for(int i = 0; i < allCourses.length; i++) {
            allCourses[i] = courses[i].fullName;
        }

        return allCourses;
    }


}
