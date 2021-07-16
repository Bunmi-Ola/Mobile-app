package com.example.notificationdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;

public class MainActivity extends Activity {
    int notificationId = 1001;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    public void setNotification(View view) {
        // Create an action to invoke when user taps the notification message
        Intent intent = new Intent(this, SecondActivity.class);

        //pending intent is an intent that your app can pass to other applications
        //getActivity method returns an instance that can start an activity
        //in this case, SecondActivity
        PendingIntent pendingIntent =
                PendingIntent.getActivity(this, 0, intent, 0);

        //This enables you to build a notification with specific content and features
        Notification notification  = new Notification.Builder(this)
                .setContentTitle("New notification")// set notification title
                .setContentText("You've got a notification!")//set notification text
                .setSmallIcon(android.R.drawable.star_on)//Sets which icon to display as a badge for this notification.
                .setContentIntent(pendingIntent)//Supply a PendingIntent to be sent when the notification is clicked
                .setAutoCancel(true) //make the notification disapper when the user click on it
                //Actions are typically displayed by the system as a button adjacent to the notification content.
                .addAction(android.R.drawable.ic_menu_gallery,
                        "Open", pendingIntent)
                .build();

        // obtain an instance of the NotificationManager class
        // and create an instance of the NotificationCompat.Builder class:
        NotificationManager notificationManager = (NotificationManager) getSystemService(
                        NOTIFICATION_SERVICE);

        //display the notification using the notify() method:
        notificationManager.notify(notificationId, notification);
    }

    //method to clear notification by stating the notification Id to be clear
    public void clearNotification(View view) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(
                        NOTIFICATION_SERVICE);

        //cancel the notification using the cancel() method:
        notificationManager.cancel(notificationId);
    }
}