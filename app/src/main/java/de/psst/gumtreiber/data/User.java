package de.psst.gumtreiber.data;

import androidx.annotation.Nullable;

public class User extends AbstractUser {

    private boolean usingSchedule;

    @Nullable
    private Course course;

    public User(String uid, String name) {
        super(uid, name);
    }

    public boolean isUsingSchedule() {
        return usingSchedule;
    }

    public void setUsingSchedule(boolean usingSchedule) {
        this.usingSchedule = usingSchedule;
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

    /*
    public String toString() {

        String s = dateFormat.format(expirationDate.getTime());

        return uid + ": " + name + " "+ altitude+ " " + longitude + " " + latitude + " "+ s;
    }
    */
}

