package de.psst.gumtreiber.ui;

import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;
import com.google.firebase.auth.UserProfileChangeRequest;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Firebase;

public class LoginActivity extends AppCompatActivity {


    private FirebaseAuth auth;
    private EditText txtName, txtEmail, txtPwd;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        auth = FirebaseAuth.getInstance();

        txtName = findViewById(R.id.txtName);
        txtEmail = findViewById(R.id.txtEmail);
        txtPwd = findViewById(R.id.txtPassword);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);


        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(txtName.getText().toString(), txtEmail.getText().toString(), txtPwd.getText().toString());
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                signIn(txtEmail.getText().toString(), txtPwd.getText().toString());
            }
        });
        signOut(); //For test purposes
    }

    @Override
    public void onStart() {
        super.onStart();

        // Check if user is signed in (non-null) and update UI accordingly
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);

    }


    /////////////////////////////////////////////////////////////////
    //                    Authentication                           //
    /////////////////////////////////////////////////////////////////
    //TODO Manage User (e.g. displayname, passwd reset, etc.): https://firebase.google.com/docs/auth/android/manage-users

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            Intent intent = new Intent(this, MainActivity.class);
            startActivity(intent);

        } else {
            btnLogin.setClickable(true); //re-enable buttons if login failed
            btnRegister.setClickable(true);
        }
    }


    private void signOut() {
        auth.signOut();
        updateUI(null);
    }

    /**
     * Checks if the required input fields for signing in or registering are filled.
     *
     * @return true if everything is filled in.
     */
    private boolean validateForm(boolean checkNameField) {
        boolean valid = true;

        String email = txtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Pflichtfeld!");
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        String password = txtPwd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPwd.setError("Pflichtfeld!");
            valid = false;
        } else {
            txtPwd.setError(null);
        }


        String displayName = txtName.getText().toString();
        if (checkNameField && TextUtils.isEmpty(displayName)) {
            txtName.setError("Pflichtfeld!");
            valid = false;
        } else {
            txtName.setError(null);
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
    private void createAccount(final String name, String email, String password) {
        Log.d("Auth", "createAccount:" + email);
        if (!validateForm(true)) {
            return;
        }
        btnLogin.setClickable(false); //Disable buttons to avoid double clicking
        btnRegister.setClickable(false);

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth", "createUserWithEmail:success");
                            final FirebaseUser user = auth.getCurrentUser();

                            //After successfully created the user, update its display name
                            updateDisplayName(user, name).addOnCompleteListener(new OnCompleteListener<Void>() {
                                @Override
                                public void onComplete(@NonNull Task<Void> task) {
                                    if (task.isSuccessful()) {
                                        //When name update request is done, create the user node in the database
                                        Firebase.createUser(user.getUid(), user.getDisplayName());
                                        updateUI(user);
                                    } else {
                                        task.getException().printStackTrace();
                                    }
                                }
                            });


                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Auth", "createUserWithEmail:failure", task.getException());

                            String msg = getFirebaseAuthErrorMsg((FirebaseAuthException)task.getException());
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }

    /**
     * Sign in a user by passing the new user's email address and password.
     * @param email Users email.
     * @param password Users password.
     */
    private void signIn(String email, String password) {
        Log.d("Auth", "signIn:" + email);
        if (!validateForm(false)) {
            return;
        }
        btnLogin.setClickable(false); //Disable buttons to avoid double clicking
        btnRegister.setClickable(false);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth", "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Auth", "signInWithEmail:failure", task.getException());

                            String msg = getFirebaseAuthErrorMsg((FirebaseAuthException)task.getException());
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }


    private Task<Void> updateDisplayName(FirebaseUser user, String displayName) {
        UserProfileChangeRequest profileUpdate = new UserProfileChangeRequest.Builder()
                .setDisplayName(displayName)
                .build();

        return user.updateProfile(profileUpdate);
    }


    private String getFirebaseAuthErrorMsg(FirebaseAuthException ae) {
        if(ae == null) return getString(R.string.auth_unknown_error);

        //Codes: https://stackoverflow.com/questions/37859582/how-to-catch-a-firebase-auth-specific-exceptions
        switch(ae.getErrorCode()) {
            case "ERROR_EMAIL_ALREADY_IN_USE": return getString(R.string.auth_email_taken);
            case "ERROR_INVALID_EMAIL": return getString(R.string.auth_invalid_email);
            case "ERROR_WRONG_PASSWORD": return getString(R.string.auth_wrong_pwd);
            case "ERROR_USER_NOT_FOUND": return getString(R.string.auth_user_not_found);
            case "ERROR_WEAK_PASSWORD": return getString(R.string.auth_weak_pwd);

            default:
                String msg = ae.getErrorCode() + ": '" + ae.getMessage() + "'";
                Log.e("getAuthErrMsg", "Unhandled case: " + msg);
                return getString(R.string.auth_unhandled_error);
        }
    }
}
