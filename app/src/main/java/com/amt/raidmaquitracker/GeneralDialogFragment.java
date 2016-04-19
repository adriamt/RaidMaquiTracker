package com.amt.raidmaquitracker;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

public class GeneralDialogFragment extends DialogFragment{
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        // Use the Builder class for convenient dialog construction
        Bundle mArgs = getArguments();
        String msg = mArgs.getString("msg");
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setTitle("Missatge");
        builder.setMessage(msg).setNeutralButton(getResources().getString(R.string.ok_btn),new DialogInterface.OnClickListener() {
            public void onClick(DialogInterface dialog, int id) {
            }
        });
        return builder.create();
    }
}