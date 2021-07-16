package com.jfdimarzio.notifications;

import android.app.Activity;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Bundle;
import android.support.v4.app.NotificationCompat;
import android.view.View;

public class MainActivity extends Activity {
    int notificationID = 1;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClick(View view) {
        displayNotification();
    }

    protected void displayNotification()
    {
        //---PendingIntent to launch activity if the user selects
        // this notification---
        //created an Intent object to point to the NotificationView class
        //It is used to launch another activity when the user selects a notification from the list.
        Intent i = new Intent(this, NotificationView.class);
        i.putExtra("notificationID", notificationID);

        //A PendingIntent object helps to perform an action on the application's behalf,
        // often at a later time, regardless of whether your application is running
        PendingIntent pendingIntent = PendingIntent.getActivity(this, 0, i, 0);

        // obtain an instance of the NotificationManager class
        //To  publish  a  notification, we can use  the NotificationManager,  which  is  one  of  the  built-in
        //services in the Android system. As it is an existing system service,
        // we can obtain it by calling the getSystemService method on an activity
        // and create an instance of the NotificationCompat.Builder class:
        NotificationManager nm = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);


        //This enables you to build a notification with specific content and features
        NotificationCompat.Builder notifBuilder;
        notifBuilder = new NotificationCompat.Builder(this)
                .setSmallIcon(R.mipmap.ic_launcher) //Sets which icon to display as a badge for this notification.
                .setContentTitle("Meeting Reminder") // set notification title
                .setContentText("Reminder: Meeting starts in 5 minutes"); //set notification text

        // now attach the pending intent to the notification
        notifBuilder.setContentIntent(pendingIntent);

        //display the notification using the notify() method:
        nm.notify(notificationID, notifBuilder.build());
    }
}
