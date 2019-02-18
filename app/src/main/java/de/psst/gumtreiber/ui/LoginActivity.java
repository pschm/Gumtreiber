package de.psst.gumtreiber.ui;

import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.EditText;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.appcompat.app.AppCompatActivity;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.viewmodels.LoginViewModel;

public class LoginActivity extends AppCompatActivity {

    private static final String TAG = "LoginActivity";

    private LoginViewModel model;
    private FirebaseAuth auth;
    private EditText txtEmail, txtPwd;
    private CheckBox checkbox;
    private Button btnLogin, btnRegister;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_auth_login);

        model = new LoginViewModel(getApplication());

        txtEmail = findViewById(R.id.txtEmail);
        txtPwd = findViewById(R.id.txtPassword);

        checkbox = findViewById(R.id.cbxSaveCredentials);

        prepareTextViews();
        auth = FirebaseAuth.getInstance();

        //TODO Auslagern in separaten Screen
        //txtName = findViewById(R.id.txtName);
        btnLogin = findViewById(R.id.btnLogin);
        btnRegister = findViewById(R.id.btnRegister);


        checkbox.setOnCheckedChangeListener(new CheckBox.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton compoundButton, boolean b) {
                if (b) {
                    model.setEmail(txtEmail.getText().toString());
                    model.setPassword(txtPwd.getText().toString());
                    model.setSaveState(b);
                    Toast.makeText(LoginActivity.this, "Password und Email werden gespeichert", Toast.LENGTH_SHORT).show();

                } else {
                    model.removeEmail();
                    model.removePassword();
                    model.setSaveState(b);
                    Toast.makeText(LoginActivity.this, "Passwort und Email werden nicht mehr gespeichert", Toast.LENGTH_SHORT).show();
                }
            }
        });

        btnRegister.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(btnRegister.getContext(), RegisterActivity.class);
                startActivity(intent);
            }
        });

        btnLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                if (!inputsValid()) return;

                btnLogin.setClickable(false); //Disable buttons to avoid double clicking
                btnRegister.setClickable(false);

                signIn(txtEmail.getText().toString(), txtPwd.getText().toString());
            }
        });

        //TODO Schätze das gehört zum Test ?
        signOut();
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
    //TODO Mach das im Settings Fragment !!!

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
     * Checks if the required input fields for signing in are filled.
     *
     * @return true if everything is filled in.
     */
    private boolean inputsValid() {
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
        return valid;
    }


    /**
     * Sign in a user by passing the new user's email address and password.
     * @param email Users email.
     * @param password Users password.
     */
    private void signIn(String email, String password) {
        Log.d(TAG, "signIn: " + email);

        auth.signInWithEmailAndPassword(email, password)
                .addOnCompleteListener(this, new OnCompleteListener<AuthResult>() {
                    @Override
                    public void onComplete(@NonNull Task<AuthResult> task) {
                        if (task.isSuccessful()) {
                            // Sign in success, update UI with the signed-in user's information
                            Log.d(TAG, "signInWithEmail:success");
                            FirebaseUser user = auth.getCurrentUser();
                            updateUI(user);
                        } else {
                            // If sign in fails, display a message to the user.
                            Log.w(TAG, "signInWithEmail:failure", task.getException());

                            String msg = getFirebaseAuthErrorMsg(btnLogin.getContext(), (FirebaseAuthException)task.getException());
                            Toast.makeText(LoginActivity.this, msg, Toast.LENGTH_SHORT).show();
                            updateUI(null);
                        }

                    }
                });
    }



    public static String getFirebaseAuthErrorMsg(Context context, FirebaseAuthException ae) {
        if(ae == null) return context.getString(R.string.auth_unknown_error);

        //Codes: https://stackoverflow.com/questions/37859582/how-to-catch-a-firebase-auth-specific-exceptions
        switch(ae.getErrorCode()) {
            case "ERROR_EMAIL_ALREADY_IN_USE": return context.getString(R.string.auth_email_taken);
            case "ERROR_INVALID_EMAIL": return context.getString(R.string.auth_invalid_email);
            case "ERROR_WRONG_PASSWORD": return context.getString(R.string.auth_wrong_pwd);
            case "ERROR_USER_NOT_FOUND": return context.getString(R.string.auth_user_not_found);
            case "ERROR_WEAK_PASSWORD": return context.getString(R.string.auth_weak_pwd);

            default:
                String msg = ae.getErrorCode() + ": '" + ae.getMessage() + "'";
                Log.e("getAuthErrMsg", "Unhandled case: " + msg);
                return context.getString(R.string.auth_unhandled_error);
        }
    }

    /**
     * Filling the TextViews for password and email with the
     * values from the ViewModel
     */
    private void prepareTextViews() {
        checkbox.setChecked(model.getSaveState());
        txtEmail.setText(model.getEmail());
        txtPwd.setText(model.getPassword());
    }
}
