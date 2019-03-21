package de.psst.gumtreiber.data;



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

    @Override
    public boolean equals(Object obj) {

        try {
            User licenceDetail = (User) obj;
            return this.getUid().equals(licenceDetail.getUid());
        } catch (Exception e) {
            return false;
        }

    }
}

