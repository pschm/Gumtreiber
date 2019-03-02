package de.psst.gumtreiber.viewmodels;

import android.app.Application;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import java.util.Objects;

import androidx.annotation.NonNull;
import androidx.lifecycle.AndroidViewModel;
import de.psst.gumtreiber.data.Course;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.data.UserDataSync;

public class SettingsViewModel extends AndroidViewModel {

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



}
