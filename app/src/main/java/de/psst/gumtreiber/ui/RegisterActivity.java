package de.psst.gumtreiber.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.FirebaseNetworkException;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import java.util.ArrayList;
import java.util.Arrays;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.appcompat.app.AppCompatActivity;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Course;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.viewmodels.SettingsViewModel;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "RegisterActivity";

    private FirebaseAuth auth;
    private EditText txtName, txtEmail, txtPwd, txtPwdRpt;
    private Button btnCompleteRegister;
    private Spinner spnCourse;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_register);

        auth = FirebaseAuth.getInstance();

        txtName = findViewById(R.id.txtRegNickname);
        txtEmail = findViewById(R.id.txtRegEmail);
        txtPwd = findViewById(R.id.txtRegPassword);
        txtPwdRpt = findViewById(R.id.txtRegPasswordRpt);

        spnCourse = findViewById(R.id.spnRegCourse);

        ArrayList<String> spnCourseList = new ArrayList<>();
        spnCourseList.add(getString(R.string.course));
        spnCourseList.add("");
        spnCourseList.addAll( Arrays.asList(Course.getAllCourses()) );
        spnCourse.setAdapter(new ArrayAdapter<>(this, R.layout.auth_spinner_item, spnCourseList ));

        btnCompleteRegister = findViewById(R.id.btnCompleteRegister);
        btnCompleteRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if(!inputsValid()) return;
                btnCompleteRegister.setClickable(false); //Disable buttons to avoid double clicking

                createAccount(txtName.getText().toString(), txtEmail.getText().toString(), txtPwd.getText().toString(), Course.values()[spnCourse.getSelectedItemPosition()] );
            }
        });

    }

    /**
     * Checks if the required input fields for signing in are filled.
     *
     * @return true if everything is filled in.
     */
    private boolean inputsValid() {
        boolean valid = true;

        String email = txtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError(getString(R.string.required_field));
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        String password = txtPwd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPwd.setError(getString(R.string.required_field));
            valid = false;
        } else {
            txtPwd.setError(null);
        }

        String passwordRpt = txtPwdRpt.getText().toString();
        if (TextUtils.isEmpty(passwordRpt)) {
            txtPwdRpt.setError(getString(R.string.required_field));
            valid = false;
        } else {

            if(!passwordRpt.equals(password)) {
                txtPwdRpt.setError(getString(R.string.no_match));
                valid = false;
            } else {
                txtPwdRpt.setError(null);
            }

        }

        String nickname = txtName.getText().toString();
        nickname = nickname.trim();
        txtName.setText(nickname);
        if (TextUtils.isEmpty(nickname)) {
            txtName.setError(getString(R.string.required_field));
            valid = false;
        } else {

            String validationCode = SettingsViewModel.validateUserName(this, nickname);
            if(!validationCode.equals(SettingsViewModel.USERNAME_VALID_CODE)) {
                txtName.setError(validationCode);
                valid = false;
            } else {
                txtName.setError(null);
            }

        }


        if(spnCourse.getSelectedItemPosition() < 2) {
            Toast.makeText(this, getString(R.string.no_course_selected), Toast.LENGTH_SHORT).show();
            valid = false;
        }

        return valid;
    }


    /**
     * Create a new account by passing the new user's email address and password.
     * <br>
     * If the new account was created, the user is also signed in.
     *
     * @param name     Users display name.
     * @param email    Users email.
     * @param password Users password.
     */
    private void createAccount(final String name, String email, String password, final Course course) {
        Log.d(TAG, "createAccount: " + email);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "createUserWithEmail:success");
                            final FirebaseUser user = auth.getCurrentUser();

                            //After successfully created the user, update its display name
                            updateDisplayName(user, name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //When name update request is done, create the user node in the database
                                        Firebase.createUser(user.getUid(), user.getDisplayName());
                                        Firebase.setCourse(user.getUid(), course);
                                        updateUI(user);
                                    } else {
                                        task.getException().printStackTrace();
                                    }
                                }
                            });


                        } else {
                            Exception taskException = task.getException();

                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", taskException);

                            String msg = getString(R.string.auth_unknown_error);
                            if(taskException instanceof FirebaseAuthException) {
                                msg = LoginActivity.getFirebaseAuthErrorMsg(btnCompleteRegister.getContext(), (FirebaseAuthException) taskException);

                            } else if(taskException instanceof FirebaseNetworkException) {
                                msg = getString(R.string.auth_no_internet);

                            } else {
                                taskException.printStackTrace();
                            }

                            Toast.makeText(RegisterActivity.this, msg, Toast.LENGTH_LONG).show();
                            updateUI(null);
                        }

                    }
                });
    }

    public static Task<Void> updateDisplayName(FirebaseUser user, String displayName) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        return user.updateProfile(profileUpdate);
    }

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else {
            btnCompleteRegister.setClickable(true); //re-enable buttons if login failed
        }
    }

    @Override
    public void onBackPressed() {
        Intent intent = new Intent(this, LoginActivity.class);
        startActivity(intent);
    }
}
