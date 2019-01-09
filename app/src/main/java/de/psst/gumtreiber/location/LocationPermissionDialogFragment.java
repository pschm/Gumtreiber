package de.psst.gumtreiber.location;

import android.app.AlertDialog;
import android.app.Dialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.net.Uri;
import android.os.Bundle;
import android.provider.Settings;
import android.support.annotation.NonNull;
import android.support.v4.app.DialogFragment;

import java.util.Objects;

import de.psst.gumtreiber.R;

public class LocationPermissionDialogFragment extends DialogFragment {
    @NonNull
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle(getString(R.string.no_location_title))
                .setMessage(getString(R.string.no_location_msg))
                .setPositiveButton(getString(R.string.settings), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {

                        Intent intent = new Intent();
                        intent.setAction(Settings.ACTION_APPLICATION_DETAILS_SETTINGS);
                        Uri uri = Uri.fromParts("package", Objects.requireNonNull(getActivity()).getPackageName(), null);
                        intent.setData(uri);
                        getActivity().startActivity(intent);
                        dismiss();
                    }
                })
                .setNegativeButton(getString(R.string.continue_limited), new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        // User cancelled the dialog
                        //dismiss();
                    }
                });
        // Create the AlertDialog object and return it
        return builder.create();
    }
}
