package de.psst.gumtreiber.viewmodels;

import android.app.Application;
import android.content.Context;
import android.text.TextUtils;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Course;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.data.User;
import de.psst.gumtreiber.data.UserDataSync;

public class SettingsViewModel extends AndroidViewModel {
    private static final int MAX_USERNAME_LENGTH = 12;
    private static final int MIN_USERNAME_LENGTH = 3;
    public static final String USERNAME_VALID_CODE = "OK";

    private String uid;
    private FirebaseUser currentUser;

    public SettingsViewModel(@NonNull Application application) {
        super(application);
        uid = Objects.requireNonNull(FirebaseAuth.getInstance().getCurrentUser()).getUid();
        currentUser = FirebaseAuth.getInstance().getCurrentUser();
    }

    //Getter
    public Course getCourse() {
        User user = Firebase.getUser(uid, UserDataSync.getUserToken());
        if(user != null) return user.getCourse();
        else return null;
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
        return currentUser.getDisplayName();
    }

    /**
     * Checks if a username meets the requirements
     *
     * @param name which will be reviewed
     * @return "OK" if the username is valid or a corresponding error msg
     */
    public static String validateUserName(@NonNull Context context, String name) {

        if (TextUtils.isEmpty(name))
            return context.getString(R.string.username_error_length_min);
        if (name.length() > MAX_USERNAME_LENGTH)
            return context.getString(R.string.username_error_length_max);
        if (name.length() < MIN_USERNAME_LENGTH)
            return context.getString(R.string.username_error_length_min);
        if (!name.matches("^[a-züäößA-ZÜÄÖ].*"))
            return context.getString(R.string.username_error_starts_with);
        if (!name.matches("[a-züäößA-ZÜÄÖ_-]*"))
            return context.getString(R.string.username_error_allowed_characters);

        return USERNAME_VALID_CODE;
    }

}
