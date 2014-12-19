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
 * 2014
 */

/**********************************************************************************************
 AboutFragActivity - Produces a Dialog Fragment
***********************************************************************************************/
public class AboutFragActivity extends DialogFragment {

    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        String msg = "Love2Brew Android Application\nThis App downloads info about various " +
                "coffee brewers from a from a web service.  The selected brewing technique is displayed " +
                "in a tabbed fragment activity.  This app can also set a coffee brewing reminder that sends " +
                "a notification when the user selects.\n\nDesigned by Sean Reinhardt from Orange County, " +
                "California, United States.";

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("About");
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        return dialog;
    }
}// end - ProgressFragActivity
