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
        String msg = "Love2Brew Android Application\n\nWould you like to learn some new ways to brew " +
                "your coffee?  This App downloads info and step-by-step instructions about some fun coffee brewers."+
                "\nSelect a  brewing technique "+
                "to learn more about it.  \nTo set a reminder for your coffee, click \"Set A Reminder\" "+
                "from the menu.  The reminder will be sent to your notification bar.\n\nDesigned by SeanReinhardtApps.";

        final ProgressDialog dialog = new ProgressDialog(getActivity());
        dialog.setTitle("About");
        dialog.setMessage(msg);
        dialog.setCancelable(false);
        return dialog;
    }
}// end - ProgressFragActivity
