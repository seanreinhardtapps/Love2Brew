package com.apps.reinhardt2.love2brew;

import android.app.AlarmManager;
import android.app.AlertDialog;
import android.app.Dialog;
import android.app.DialogFragment;
import android.content.DialogInterface;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.Toast;

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
 Produces a Dialog Fragment to select Reminder Duration or cancel
 ***********************************************************************************************/
public class AlarmDialogFrag extends DialogFragment {

    /********************************************************************************************
     onCreateDialog()
     Dialog Builder is called to display Dialog Fragment
     *******************************************************************************************/
    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
        View v = inflater.inflate(R.layout.fragment_alarm, container, false);
        Button button1 = (Button)v.findViewById(R.id.button1);
        Button button2 = (Button)v.findViewById(R.id.button2);
        Button button3 = (Button)v.findViewById(R.id.button3);
        Button button4 = (Button)v.findViewById(R.id.button4);

        getDialog().setTitle("Coffee Reminders");

        button1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + MainActivity.TWELVE_HOUR_ALARM_DELAY,
                        MainActivity.mCoffeeReceiverPendingIntent);
                dismiss();
            }
        });
        button2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + MainActivity.TWENTYFOUR_HOUR_ALARM_DELAY,
                        MainActivity.mCoffeeReceiverPendingIntent);
                dismiss();
            }
        });
        button3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + MainActivity.FIVE_MIN_ALARM_DELAY,
                        MainActivity.mCoffeeReceiverPendingIntent);
                dismiss();
            }
        });
        button4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                MainActivity.mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + MainActivity.NINETY_SEC_ALARM_DELAY,
                        MainActivity.mCoffeeReceiverPendingIntent);
                ((MainActivity)getActivity()).PopToast("Reminder Set");
                dismiss();
            }
        });
        return v;
    }



}//end - AlertDialogFrag Class
