package de.psst.gumtreiber.ui.fragments.settings;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.AuthResult;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.ui.LoginActivity;

public class SettingsManipulatorPwd extends SettingsManipulatorFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLabels();
        initCompleteButton();
    }

    private void initLabels() {
        txtTitle.setText(getString(R.string.change_password));
        txtUserInput1.setHint(getString(R.string.new_password));
        txtUserInput1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);

        txtUserInput2.setHint(getString(R.string.new_password_rpt));
        txtUserInput2.setVisibility(View.VISIBLE);
        txtUserInput2.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PASSWORD);
    }

    private boolean validateInputs() {
        boolean valid = true;

        String pwd1 = txtUserInput1.getText().toString();
        String pwd2 = txtUserInput2.getText().toString();

        if(TextUtils.isEmpty(pwd1)) {
            txtUserInput1.setError(getString(R.string.required_field));
            valid = false;
        }

        if(TextUtils.isEmpty(pwd2)) {
            txtUserInput2.setError(getString(R.string.required_field));
            valid = false;

        } else if(!pwd2.equals(pwd1)) {
            txtUserInput2.setError(getString(R.string.no_match));
            valid = false;
        }

        return valid;
    }

    private void initCompleteButton() {
        //Set listener on "FERTIG" button
        setToolbarBtnListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                //Wenn inputfeld leer, abbruch
                if(!validateInputs()) return false;


                //Wenn FERTIG geklickt, fordere Nutzer auf, sich neu zu autentifizieren (Must have)
                promptReauthentication(new OnReauthPromptCompleteListener() {

                    //Wenn User das Popup beendet hat, feuert dieser Listener, weiter geht's:
                    @Override
                    public void onComplete(@Nullable AuthCredential credential) {
                        //Wenn nutzer nix eingeben hat, hier Ende
                        if(credential == null) return;
                        FirebaseUser user = FirebaseAuth.getInstance().getCurrentUser();
                        if(user == null) return;

                        //Ansonsten, versuche mit eingegebenden Daten, sich zu re-authentifizieren
                        user.reauthenticateAndRetrieveData(credential).addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                            @Override
                            public void onComplete(@NonNull Task<AuthResult> task) {

                                task.addOnCompleteListener(new OnCompleteListener<AuthResult>() {
                                    //re-auth hat fertig
                                    @Override
                                    public void onComplete(@NonNull Task<AuthResult> task) {
                                        //Wenn re-auth fertig und erfolgreich, starte pwd Änderungs-routine
                                        if(task.isSuccessful()) {

                                            user.updatePassword(txtUserInput1.getText().toString()).addOnCompleteListener(new OnCompleteListener<Void>() {
                                                @Override
                                                public void onComplete(@NonNull Task<Void> task) {
                                                    //Wenn Änderung erfolgreich, alles supi. Zurück zum vorherigem Menü
                                                    if(task.isSuccessful()) {
                                                        Toast.makeText(activity, getString(R.string.update_successful), Toast.LENGTH_SHORT).show();

                                                        loginModel.setPassword(txtUserInput1.getText().toString());

                                                        activity.onBackPressed();

                                                    } else {
                                                        String msg = LoginActivity.getFirebaseAuthErrorMsg(activity, (FirebaseAuthException)task.getException());
                                                        Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();

                                                        task.getException().printStackTrace();
                                                    }

                                                }
                                            });

                                            //re-auth fehlgeschlagen - abbruch
                                        } else {
                                            String msg = LoginActivity.getFirebaseAuthErrorMsg(activity, (FirebaseAuthException)task.getException());
                                            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();

                                            task.getException().printStackTrace();

                                        }
                                    }
                                });
                            }
                        });
                    }
                });
                return true;
            }
        });
    }
}
