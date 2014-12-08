package com.apps.reinhardt2.love2brew;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * Created by Sean on 12/6/2014.
 */
public class ProcessFragActivity extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = getArguments().getString("msg");

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("Downloading Latest Updates..");
        dialog.setMessage(msg);
        dialog.setIndeterminate(true);
        dialog.setCancelable(false);
        return dialog;
    }
}// end - ProgressFragActivity
