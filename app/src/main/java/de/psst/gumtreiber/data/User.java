package de.psst.gumtreiber.data;

public class User extends AbstractUser {

    private boolean usingSchedule;


    public User(String uid, String name) {
        super(uid, name);
    }

    public boolean isUsingSchedule() {
        return usingSchedule;
    }

    public void setUsingSchedule(boolean usingSchedule) {
        this.usingSchedule = usingSchedule;
    }

    /*
    public String toString() {

        String s = dateFormat.format(expirationDate.getTime());

        return uid + ": " + name + " "+ altitude+ " " + longitude + " " + latitude + " "+ s;
    }
    */
}

