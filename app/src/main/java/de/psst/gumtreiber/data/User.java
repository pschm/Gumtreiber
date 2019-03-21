package de.psst.gumtreiber.data;

import android.annotation.SuppressLint;

import java.text.DateFormat;
import java.text.SimpleDateFormat;

import androidx.annotation.Nullable;

public class User extends AbstractUser {

    @Nullable
    private Course course;

    public User(String uid, String name) {
        super(uid, name);
    }

    public Course getCourse() {
        return course;
    }

    public void setCourse(Course course) {
        this.course = course;
    }

}

