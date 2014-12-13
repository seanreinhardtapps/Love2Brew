package com.apps.reinhardt2.love2brew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
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
 AlertDialogFrag Class
 Produces a Dialog Fragment
 ***********************************************************************************************/
public class AlertDialogFrag extends DialogFragment {
    String mTitle = "";
    String mMessage = "";

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

    /********************************************************************************************
     onCreateDialog()
     Dialog Builder is called to display Dialog Fragment
     *******************************************************************************************/
    @Override
    public Dialog onCreateDialog(Bundle savedInstanceState) {
        AlertDialog.Builder builder = new AlertDialog.Builder(getActivity());
        builder.setIcon(getResources().getDrawable(R.drawable.ic_launcher))
                .setTitle(mTitle)
                .setMessage(mMessage)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
        return builder.create();
    }// end - onCreateDialog

}//end - AlertDialogFrag Class
