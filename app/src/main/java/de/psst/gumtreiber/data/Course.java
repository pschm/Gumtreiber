package de.psst.gumtreiber.data;

public enum Course {

    //TODO Alle Studiegänge reintun, ich habe bisher nur die Bachelor Inf Studiengänge reingetan

    NONE("Keiner"),
    INF("Informatik"),
    ING("Ingenieur"),
    PROF("Professor/Dozent");

    //Attribute
    private final String fullName;

    //Konstruktor
    Course(String fullName) {
        this.fullName = fullName;
    }


    public static String[] getAllCourses() {
        Course[] courses = Course.values();

        String[] allCourses = new String[courses.length];
        for (int i = 0; i < allCourses.length; i++) {
            if (courses[i].equals(PROF)) continue;
            allCourses[i] = courses[i].fullName;
        }

        return allCourses;
    }


}
