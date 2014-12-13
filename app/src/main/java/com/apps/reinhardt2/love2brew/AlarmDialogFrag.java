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


        return v;
    }


    public void onClk(View view) {
        switch(view.getId()){
            case(R.id.button1):
                MainActivity.mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + MainActivity.TWELVE_HOUR_ALARM_DELAY,
                        MainActivity.mCoffeeReceiverPendingIntent);
                break;
            case(R.id.button2):
                MainActivity.mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + MainActivity.TWENTYFOUR_HOUR_ALARM_DELAY,
                        MainActivity.mCoffeeReceiverPendingIntent);
                break;
            case(R.id.button3):
                MainActivity.mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + MainActivity.FIVE_MIN_ALARM_DELAY,
                        MainActivity.mCoffeeReceiverPendingIntent);
                break;
            case(R.id.button4):
                MainActivity.mCoffeeAlarmManager.set(AlarmManager.RTC_WAKEUP,
                        System.currentTimeMillis() + MainActivity.NINETY_SEC_ALARM_DELAY,
                        MainActivity.mCoffeeReceiverPendingIntent);
                break;
        }

    }
}//end - AlertDialogFrag Class
