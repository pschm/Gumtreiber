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
import com.google.firebase.auth.FirebaseUser;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.data.Firebase;
import de.psst.gumtreiber.ui.LoginActivity;
import de.psst.gumtreiber.ui.RegisterActivity;
import de.psst.gumtreiber.viewmodels.SettingsViewModel;

public class SettingsManipulatorNickname extends SettingsManipulatorFragment {

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);

        initLabels();
        initCompleteButton();
    }

    private void initLabels() {
        txtTitle.setText(getString(R.string.change_nickname));
        txtUserInput1.setHint(getString(R.string.new_nickname));
        txtUserInput1.setInputType(InputType.TYPE_CLASS_TEXT | InputType.TYPE_TEXT_VARIATION_PERSON_NAME);
    }

    private void initCompleteButton() {
        //Set listener on "FERTIG" button
        setToolbarBtnListener(new MenuItem.OnMenuItemClickListener() {
            @Override
            public boolean onMenuItemClick(MenuItem item) {
                String newName = txtUserInput1.getText().toString();
                newName = newName.trim();
                txtUserInput1.setText(newName);
                if (TextUtils.isEmpty(newName)) {
                    txtUserInput1.setError(getString(R.string.required_field));
                    return false;
                } else {

                    String validationCode = SettingsViewModel.validateUserName(activity, newName);
                    if(!validationCode.equals(SettingsViewModel.USERNAME_VALID_CODE)) {
                        txtUserInput1.setError(validationCode);
                        return false;
                    } else {
                        txtUserInput1.setError(null);
                    }

                }

                FirebaseUser currentUser = FirebaseAuth.getInstance().getCurrentUser();
                if(currentUser == null) throw new RuntimeException("Trying to change nickname when current user is null!");

                RegisterActivity.updateDisplayName(currentUser, newName).addOnCompleteListener(new OnCompleteListener<Void>() {
                    @Override
                    public void onComplete(@NonNull Task<Void> task) {
                        //Wenn Änderung erfolgreich, alles supi. Zurück zum vorherigem Menü
                        if(task.isSuccessful()) {
                            Firebase.changeName(currentUser.getUid(), currentUser.getDisplayName());
                            Toast.makeText(activity, getString(R.string.update_successful), Toast.LENGTH_SHORT).show();
                            //TODO updated das auch bei anderen nutzern "sofort"? Oder ist uns das egal?
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
