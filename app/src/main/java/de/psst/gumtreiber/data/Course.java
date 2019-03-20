package de.psst.gumtreiber.data;

public enum Course {

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

        String[] allCourses = new String[courses.length-1];
        for (int i = 0; i < courses.length; i++) {
            if (courses[i].equals(PROF)) continue;
            allCourses[i] = courses[i].fullName;
        }

        return allCourses;
    }

    @Override
    public String toString() {
        return fullName;
    }
}
