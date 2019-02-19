package de.psst.gumtreiber.ui.fragments.settings;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.os.Bundle;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.firebase.auth.AuthCredential;
import com.google.firebase.auth.EmailAuthProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import de.psst.gumtreiber.R;
import de.psst.gumtreiber.ui.MainActivity;

public abstract class SettingsManipulatorFragment extends Fragment {

    MainActivity activity;

    TextView txtTitle;
    EditText txtUserInput1, txtUserInput2;

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {
        super.onCreateView(inflater, container, savedInstanceState);
        activity = (MainActivity) getActivity();
        return inflater.inflate(R.layout.fragment_settings_manipulator, container, false);
    }

    @Override
    public void onDestroyView() {
        activity.getToolbarDoneBTN().setOnMenuItemClickListener(null);
        activity.getToolbarDoneBTN().setVisible(false);
        activity.resetActionBarTitle();
        super.onDestroyView();
    }

    @Override
    public void onViewCreated(@NonNull View view, @Nullable Bundle savedInstanceState) {
        super.onViewCreated(view, savedInstanceState);
        activity.setActionBarTitle("");

        txtTitle = activity.findViewById(R.id.txtSettingsTitle);
        txtUserInput1 = activity.findViewById(R.id.txtUserInput1);
        txtUserInput2 = activity.findViewById(R.id.txtUserInput2);

        txtUserInput2.setVisibility(View.GONE);

    }

    void setToolbarBtnListener(MenuItem.OnMenuItemClickListener listener) {
        activity.getToolbarDoneBTN().setOnMenuItemClickListener(listener);
        activity.getToolbarDoneBTN().setVisible(true);
    }

    void promptReauthentication(OnReauthPromptCompleteListener listener) {


        //AltertDialog für re-login
        final AlertDialog.Builder alertDialogBuilder = new AlertDialog.Builder(activity);
        alertDialogBuilder.setTitle("Identität bestätigen");

        // Set prompt layout to get user input
        View view = getLayoutInflater().inflate(R.layout.layout_reauth_prompt, null);
        final TextView inputEmail = view.findViewById(R.id.promptEmail);
        final TextView inputPwd = view.findViewById(R.id.promptPassword);

        alertDialogBuilder.setView(view);


        alertDialogBuilder.setPositiveButton("OK", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                String email = inputEmail.getText().toString();
                String pwd = inputPwd.getText().toString();

                if(TextUtils.isEmpty(email) || TextUtils.isEmpty(pwd)) {
                    Toast.makeText(activity, "Anmeldedaten unvollständig - Aktion abgebrochen", Toast.LENGTH_LONG).show();
                    listener.onComplete(null);

                } else {
                    AuthCredential credential = EmailAuthProvider.getCredential(email, pwd);
                    listener.onComplete(credential);
                }
            }
        });

        alertDialogBuilder.setNegativeButton("Abbruch", new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialogInterface, int i) {
                dialogInterface.cancel();
                listener.onComplete(null);
            }
        });
        alertDialogBuilder.show();
    }



    interface OnReauthPromptCompleteListener {
        void onComplete(@Nullable AuthCredential credential);
    }
}









