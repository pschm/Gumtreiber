package de.psst.gumtreiber;

import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.v7.app.AppCompatActivity;
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
import com.google.firebase.auth.FirebaseUser;

import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.data.UserDataSync;
import de.psst.gumtreiber.location.LocationHandler;
import de.psst.gumtreiber.map.MapControl;
import de.psst.gumtreiber.map.MapView;

public class LoginActivity extends AppCompatActivity {

    private FirebaseAuth auth;
    private EditText txtEmail, txtPwd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_authentication);

        auth = FirebaseAuth.getInstance();

        txtEmail = findViewById(R.id.txtEmail);
        txtPwd = findViewById(R.id.txtPassword);


        Button btnRegister = findViewById(R.id.btnRegister);
        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                createAccount(txtEmail.getText().toString(), txtPwd.getText().toString());
            }
        });

        Button btnLogin = findViewById(R.id.btnLogin);
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
        // Check if user is signed in (non-null) and update UI accordingly.
        FirebaseUser currentUser = auth.getCurrentUser();
        updateUI(currentUser);
    }

    //TODO Hübsch machen. Ggf. auth in eigene Activity?
    private void setupOldOnCreateStuff() {
        setContentView(R.layout.activity_login);

        MapView map = findViewById(R.id.map);
        //Firebase.createUser("123","Max");
        //Firebase.setCurrentLocation("123",7.563138,51.024232,  0);
        //map.setUserList(Firebase.getAllUsers());

        // enable zoom effect
        MapControl mc = new MapControl(map, true);
        mc.setMapView(map);
        mc.setMaximumScale(9f);
        mc.update();


        //create LocationHandler
        LocationHandler locationHandler = new LocationHandler(this, true); //TODO updatesEnabled aus config laden

        //create UserDataSync
        UserDataSync uds = new UserDataSync(this, locationHandler, map);
        uds.startUpdating();
    }



    /////////////////////////////////////////////////////////////////
    // Authentication                                              //
    /////////////////////////////////////////////////////////////////
    //TODO Manage User (e.g. displayname, passwd reset, etc.): https://firebase.google.com/docs/auth/android/manage-users

    private void updateUI(FirebaseUser user) {
        if (user != null) {
            setupOldOnCreateStuff();

        } else {

            //setContentView(R.layout.activity_authentication);
        }
    }

    private void signOut() {
        auth.signOut();
        updateUI(null);
    }

    /**
     * Checks if the required input fields for signing in or registering are filled.
     * @return true if everything is filled in.
     */
    private boolean validateForm() {
        boolean valid = true;

        String email = txtEmail.getText().toString();
        if (TextUtils.isEmpty(email)) {
            txtEmail.setError("Required.");
            valid = false;
        } else {
            txtEmail.setError(null);
        }

        String password = txtPwd.getText().toString();
        if (TextUtils.isEmpty(password)) {
            txtPwd.setError("Required.");
            valid = false;
        } else {
            txtPwd.setError(null);
        }

        return valid;
    }

    /**
     * Create a new account by passing the new user's email address and password.
     * <br>
     * If the new account was created, the user is also signed in.
     * @param email Users email.
     * @param password Users password.
     */
    private void createAccount(String email, String password) {
        Log.d("Auth", "createAccount:" + email);
        if (!validateForm()) {
            return;
        }

        auth.createUserWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d("Auth", "createUserWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            Firebase.createUser(user.getUid(), "Todo"); //TODO
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w("Auth", "createUserWithEmail:failure", task.getException());
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
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
        if (!validateForm()) {
            return;
        }

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
                            Toast.makeText(LoginActivity.this, "Authentication failed.", Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }
}
