package de.psst.gumtreiber.viewmodels;

import android.app.Application;
import android.content.res.Resources;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Course;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.data.UserDataSync;

public class SettingsViewModel extends AndroidViewModel {
    private static final int MAX_USERNAME_LENGTH = 12;
    private static final int MIN_USERNAME_LENGTH = 3;

    private String uid;
    private String token;
    private FirebaseUser currentUser;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        token = UserDataSync.getUserToken();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    //Getter
    public Course getCourse() {
        return Firebase.getUser(uid, token).getCourse();
    }

    //Setter
    public void setCourse(Course course) {
        Firebase.setCourse(uid, course);
    }

    public String getPassword() {
        return currentUser.getEmail();
    }

    public String getEmail() {
        return currentUser.getEmail();
    }

    public String getNickname() {
        return Firebase.getUser(uid, token).getName();
    }

    /**
     * Checks if a username meets the requirements
     *
     * @param name which will be reviewed
     * @return "OK" if the username is valid or a corresponding error msg
     */
    public String validateUserName(@NonNull String name) {
        Resources res = getApplication().getResources();

        if (name.length() > MAX_USERNAME_LENGTH)
            return res.getString(R.string.username_error_length_max);
        if (name.length() < MIN_USERNAME_LENGTH)
            return res.getString(R.string.username_error_length_min);
        if (!name.matches("^[a-züäößA-ZÜÄÖ].*"))
            return res.getString(R.string.username_error_starts_with);
        if (!name.matches("[a-züäößA-ZÜÄÖ_-]*"))
            return res.getString(R.string.username_error_allowed_characters);

        return "OK";
    }

}
