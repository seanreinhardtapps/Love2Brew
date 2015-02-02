package com.apps.reinhardt2.love2brew;

import android.app.Dialog;
import android.app.DialogFragment;
import android.app.ProgressDialog;
import android.os.Bundle;

/**
 * SEANREINHARDTAPPS
 * Created by Sean Reinhardt on 10/19/2014.
 * Java Android Application
 * This file is a module in the application: Love2Brew
 * Project host at https://www.github.com/SeanReinhardtApps/Love2Brew
 *
 * ProcessFragActivity - Produces a Dialog Fragment
 *
 * 2014
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
