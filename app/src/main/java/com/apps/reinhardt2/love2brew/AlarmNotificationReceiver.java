package com.apps.reinhardt2.love2brew;

import java.text.DateFormat;
import java.util.Date;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.util.Log;

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
 AlarmNotificationReceiver
 Sets up the intent that will execute when the AlarmReciever is triggered by the system
 Publishes a notification to the notification center
 ***********************************************************************************************/
public class AlarmNotificationReceiver extends BroadcastReceiver {
	// Notification ID to allow for future updates
	private static final int ALARM_NOTIFICATION_ID = 11;

	// Notification Text Elements
	private final CharSequence tickerText = "Coffee Reminder";
	private final CharSequence contentTitle = "Love 2 Brew";
	private final CharSequence contentText = "Your Coffee is ready";

	// Notification Action Elements
	private Intent mNotificationIntent;
	private PendingIntent mContentIntent;

	// Notification Sound and Vibration on Arrival
	/*private final Uri soundURI = Uri
			.parse("android.resource://course.examples.Alarms.AlarmCreate/"
					+ R.raw.alarm_rooster);*/
	private final long[] mVibratePattern = { 0, 200, 200, 300 };

	@Override
	public void onReceive(Context context, Intent intent) {

		// The Intent to be used when the user clicks on the Notification View
		mNotificationIntent = new Intent(context, MainActivity.class);

		// The PendingIntent that wraps the underlying Intent
		mContentIntent = PendingIntent.getActivity(context, 0,
				mNotificationIntent, 0);

		// Build the Notification
		Notification.Builder notificationBuilder = new Notification.Builder(
				context)
                .setSmallIcon(R.drawable.ic_menu_recent_history)
				.setAutoCancel(true)
                .setContentTitle(contentTitle)
				.setContentText(contentText)
                .setContentIntent(mContentIntent)
				//.setSound(soundURI)
                .setVibrate(mVibratePattern)
                .setTicker(tickerText);

		// Get the NotificationManager
		NotificationManager mNotificationManager = (NotificationManager) context
				.getSystemService(Context.NOTIFICATION_SERVICE);

		// Pass the Notification to the NotificationManager:
		mNotificationManager.notify(ALARM_NOTIFICATION_ID,
				notificationBuilder.build());

		// Log occurence of notify() call
		Log.i(MainActivity.ATAG, "Sending notification at:"
				+ DateFormat.getDateTimeInstance().format(new Date()));

	}
}
