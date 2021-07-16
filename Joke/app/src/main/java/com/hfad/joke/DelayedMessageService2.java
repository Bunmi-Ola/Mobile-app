package com.hfad.joke;

import android.app.IntentService;
import android.app.NotificationChannel;
import android.app.NotificationManager;
import android.content.Intent;
import android.graphics.Color;
import android.os.Handler;
import android.os.Looper;
//import android.util.Log;
import android.widget.Toast;
import android.support.v4.app.NotificationCompat;
import android.app.PendingIntent;



public class DelayedMessageService2 extends IntentService {

    //a contant to pass a message from activity to the service
    public static final String EXTRA_MESSAGE = "message";

    //notification ID to identify notification
   /* The notification ID is an integer that one can choose.
    It is needed just in case you want to cancel the notification,
    in which case you pass the ID to the cancel method of theNotificationManage*/

    public static final int NOTIFICATION_ID = 5453;

    public DelayedMessageService2() {
        super("DelayedMessageService");
    }

    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            try {
                wait(10000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        String text = intent.getStringExtra(EXTRA_MESSAGE);
        showText(text);

    }

    private void showText(final String text) {
       /* Log.v("DelayedMessageService", "The message is: " + text);
        showToast("The message is: "+ text);*/


        // We want to also send a notification

        // Create a Notification Builder (for Android 25 and older)
        //This enables you to build a notification with specific content and features
        NotificationCompat.Builder builder = new NotificationCompat.Builder(this)
                //Sets which icon to display as a badge for this notification.
                //display a small icon, in this case built-in Android icon
                .setSmallIcon(android.R.drawable.sym_def_app_icon)
                .setContentTitle(getString(R.string.question)) // set title
                .setContentText(text) //set text
                .setPriority(NotificationCompat.PRIORITY_HIGH)//Set the relative priority for this notification.
                .setVibrate(new long[] {0,1000}) //wait 10 seconds and vibrate the device
                .setAutoCancel(true); //make the notification disapper when the user click on it

        // Create an action to invoke when user taps the notification message
        //In this case, we’re going to start MainActivity.
        Intent actionIntent = new Intent(this, MainActivity.class);

        // create a PendingIntent to hold intent to be invoked later.
        //pending intent is an intent that your app can pass to other applications
        //getActivity method returns an instance that can start an activity
        PendingIntent actionPendingIntent = PendingIntent.getActivity(
                                                this, //current service
                                                0,//flag to indicate whether to retrieve pending intent, in this case, there is no need
                                                actionIntent,
                                                PendingIntent.FLAG_UPDATE_CURRENT);//if there is a matching pending intent, it will be updated

        // now attach the pending intent to the notification
        // when the user touches the notification, the PendingIntent will be invoke
        builder.setContentIntent(actionPendingIntent);

        // obtain an instance of the NotificationManager class
        // now issue/send the notification via a notification manager
        //To  publish  a  notification, we can use  the NotificationManager,  which  is  one  of  the  built-in
        //services in the Android system. As it is an existing system service,
        // we can obtain it by calling the getSystemService method on an activity
        NotificationManager notificationManager = (NotificationManager)getSystemService(NOTIFICATION_SERVICE);

        // now send the notification
        //The notification ID is used to identify the notification
        //display/publish notification using notification manager
        notificationManager.notify(NOTIFICATION_ID, builder.build());

    }

   /* public void showToast(String message) {
        final String msg = message;
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }*/

}
