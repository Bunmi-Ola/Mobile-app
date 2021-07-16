package com.example.cancelnotificationdemo;

import android.app.Activity;
import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.view.Menu;
import android.view.View;


public class MainActivity extends Activity {

    //a contant to pass a message from activity to the service
    private static final String CANCEL_NOTIFICATION_ACTION
            = "cancel_notification";

    //notification ID to identify notification
   /* The notification ID is an integer that one can choose.
    It is needed just in case you want to cancel the notification,
    in which case you pass the ID to the cancel method of theNotificationManage*/
    int notificationId = 1002;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //when the Dismiss button is clicked, a broadcast will be
        // sent and received by the receiver in the activity.
        // As a result, the notification will be canceled
        BroadcastReceiver receiver = new BroadcastReceiver() {
            @Override
            public void onReceive(Context context, Intent intent) {
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(
                                NOTIFICATION_SERVICE);
                notificationManager.cancel(notificationId);
            }
        };
        // Register the filter for listening to broadcast.
        //create an IntentFilter that specifies a user-defined action (CANCEL_NOTIFICATION_ACTION)
        //it specifies what intent should cause the receiver to be triggered
        IntentFilter filter = new IntentFilter();
        filter.addAction(CANCEL_NOTIFICATION_ACTION);

        //register the receiver
        this.registerReceiver(receiver, filter);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    // Create an action to invoke when user taps the notification message
    //In this case, we’re going to cancel notification.
    public void setNotification(View view) {
        Intent cancelIntent = new Intent("cancel_notification");

        // create a PendingIntent to hold intent to be invoked later.
        //call a broadcast receiver when the dismiss button is clicked
        PendingIntent cancelPendingIntent =
                PendingIntent.getBroadcast(this, 100,
                        cancelIntent, 0);

// Create a Notification Builder (for Android 25 and older)
        //This enables you to build a notification with specific content and features
        Notification notification  = new Notification.Builder(this)
                .setContentTitle("Stop Press")// set notification title
                .setContentText(
                        "Everyone gets extra vacation week!")//set notification text
                //Sets which icon to display as a badge for this notification.
                .setSmallIcon(android.R.drawable.star_on)
                .setAutoCancel(true)//make the notification disapper when the user click on it
                .addAction(android.R.drawable.btn_dialog,
                        "Dismiss", cancelPendingIntent)
                .build();

                 // obtain an instance of the NotificationManager class
                // now issue/send the notification via a notification manager
                //To  publish  a  notification, we can use  the NotificationManager,  which  is  one  of  the  built-in
                //services in the Android system. As it is an existing system service,
                // we can obtain it by calling the getSystemService method on an activity
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(
                        NOTIFICATION_SERVICE);

        // now send the notification
        //The notification ID is used to identify the notification
        //display/publish notification using notification manager
        notificationManager.notify(notificationId, notification);
    }

    //method to clear notification by stating the notification Id to be clear
    public void clearNotification(View view) {
        NotificationManager notificationManager =
                (NotificationManager) getSystemService(
                        NOTIFICATION_SERVICE);

        //cancel the notification using the cancel() method by passing the notification ID
        notificationManager.cancel(notificationId);
    }
}