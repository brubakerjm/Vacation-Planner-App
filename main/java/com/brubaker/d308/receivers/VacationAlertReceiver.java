package com.brubaker.d308.receivers;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;

import com.brubaker.d308.utils.NotificationHelper;

public class VacationAlertReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        // Retrieve data from the Intent
        String title = intent.getStringExtra("title"); // Get the vacation title
        String type = intent.getStringExtra("type");   // Get the alert type ("starting" or "ending")

        // Use the NotificationHelper to display the notification
        String message = "Your vacation \"" + title + "\" is " + type + "."; // Compose the message
        NotificationHelper.createNotification(context, "Vacation Alert", message);
    }
}