package com.brubaker.d308.utils;

import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Context;
import android.os.Build;

import androidx.core.app.NotificationCompat;

import com.brubaker.d308.R;

public class NotificationHelper {
    private static final String CHANNEL_ID = "vacation_alerts";

    public static void createNotification(Context context, String title, String message) {
        // Create a NotificationManager to handle notifications
        NotificationManager notificationManager =
                (NotificationManager) context.getSystemService(Context.NOTIFICATION_SERVICE);

        // For Android 8.0+ (API 26+), create a NotificationChannel
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.O) {
            // A channel groups related notifications and sets their importance level
            NotificationChannel channel = new NotificationChannel(
                    CHANNEL_ID,                   // Unique channel ID
                    "Vacation Alerts",            // Channel name (user-facing)
                    NotificationManager.IMPORTANCE_HIGH // High importance for pop-up notifications
            );
            notificationManager.createNotificationChannel(channel); // Register the channel
        }

        // Use NotificationCompat.Builder to create the notification
        NotificationCompat.Builder builder = new NotificationCompat.Builder(context, CHANNEL_ID)
                .setSmallIcon(R.drawable.ic_launcher_foreground) // Icon shown in the notification bar
                .setContentTitle(title)                  // The main title of the notification
                .setContentText(message)                 // The message (body) of the notification
                .setPriority(NotificationCompat.PRIORITY_HIGH) // Ensures high visibility for the notification
                .setAutoCancel(true);                    // Automatically dismiss the notification when tapped

        // Step 4: Use the NotificationManager to display the notification
        notificationManager.notify((int) System.currentTimeMillis(), builder.build());
    }
}