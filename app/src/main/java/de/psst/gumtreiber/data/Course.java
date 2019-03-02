package de.psst.gumtreiber.data;

public enum Course {

    //TODO Alle Studiegänge reintun, ich habe bisher nur die Bachelor Inf Studiengänge reingetan

    NONE("Keiner", 0),
    INF("Informatik", 1),
    ING("Ingenieur", 2),
    PROF("Professor/Dozent", 3);

    //Attribute
    private final String fullName;
    private final int position;
    //Konstruktor
    Course(String fullName, int position) {
        this.fullName = fullName;
        this.position = position;
    }


    public static String[] getAllCourses() {
        Course[] courses = Course.values();

        String[] allCourses = new String[courses.length];
        for (int i = 0; i < allCourses.length - 1; i++) {
            if (courses[i].equals(PROF)) continue;
            allCourses[i] = courses[i].fullName;
        }

        return allCourses;
    }

    @Override
    public String toString() {
        return fullName;
    }

    public int getPosition() {
        return position;
    }

}
