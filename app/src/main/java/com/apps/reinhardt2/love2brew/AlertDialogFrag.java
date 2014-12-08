package com.apps.reinhardt2.love2brew;

import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;

/**
 * Created by Sean on 10/19/2014.
 */
public class AlertDialogFrag extends DialogFragment {
    String mTitle = "";
    String mMessage = "";

    public void setTitle(String title) {
        this.mTitle = title;
    }

    public void setMessage(String message) {
        this.mMessage = message;
    }

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
