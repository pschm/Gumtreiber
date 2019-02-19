package de.psst.gumtreiber.ui.fragments.settings;

import android.os.Bundle;
import android.text.InputType;
import android.text.TextUtils;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.google.android.gms.tasks.OnCompleteListener;
import com.google.android.gms.tasks.Task;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseAuthException;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.psst.gumtreiber.ui.LoginActivity;
import de.psst.gumtreiber.ui.RegisterActivity;

public class SettingsManipulatorNickname extends SettingsManipulatorFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLabels();
        initCompleteButton();
    }

    private void initLabels() {
        txtTitle.setText("Nicknamen ändern");
        txtUserInput1.setHint("Neuer Nickname");
        txtUserInput1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    }

    private void initCompleteButton() {
        //Set listener on "FERTIG" button
        setToolbarBtnListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String newName = txtUserInput1.getText().toString();
                //Wenn inputfeld leer, abbruch
                if(TextUtils.isEmpty(newName)) { //TODO Nickname restriktionen anwenden!
                    txtUserInput1.setError("Pflichtfeld!");
                    return false;
                }


                RegisterActivity.updateDisplayName(FirebaseAuth.getInstance().getCurrentUser(), newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Wenn Änderung erfolgreich, alles supi. Zurück zum vorherigem Menü
                        if(task.isSuccessful()) {
                            Toast.makeText(activity, "Update erfolgreich!", Toast.LENGTH_SHORT).show(); //TODO updated das auch bei anderen nutzern "sofort"
                            activity.onBackPressed();

                        } else {
                            String msg = LoginActivity.getFirebaseAuthErrorMsg(activity, (FirebaseAuthException)task.getException());
                            Toast.makeText(activity, msg, Toast.LENGTH_LONG).show();

                            task.getException().printStackTrace();
                        }
                    }
                });

                return true;
            }
        });
    }
}
