package com.comp262.bunmi.comp262finalproject;

import android.app.Notification;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Context;
import android.content.Intent;
import android.util.Log;

import androidx.localbroadcastmanager.content.LocalBroadcastManager;
import com.google.firebase.messaging.FirebaseMessagingService;
import com.google.firebase.messaging.RemoteMessage;

import org.json.JSONException;
import org.json.JSONObject;

//Firebase Cloud Messaging, formerly known as Google Cloud Messaging,
// is a cross-platform cloud solution for messages and notifications for Android, iOS, and web applications

//Extending this class is required to be able to handle downstream messages.
// It also provides functionality to automatically display notifications,
// and has methods that are invoked to give the status of upstream messages
public class MyFirebaseMessagingService extends FirebaseMessagingService {

    //Class.getSimpleName() returns the simple name of the underlying class as given in the source code
    private static final String TAG = MyFirebaseMessagingService.class.getSimpleName();
    //declaring coordinates
    private double longitude = 0;
    private double latitude = 0;


    //this is called when a message is received.
   // By overriding the method FirebaseMessagingService.onMessageReceived,
   // one can perform actions based on the received RemoteMessage object and get the message data
    @Override
    public void onMessageReceived(RemoteMessage remoteMessage) {
        //Gets the body of the notification
        String message = remoteMessage.getNotification().getBody();
        //Gets the title of the notification
        String Title = remoteMessage.getNotification().getTitle();

        // Check if message contains a data payload.
        if (remoteMessage.getData().size() > 0) {
            //Gets the message payload data.
            String dataPayload = remoteMessage.getData().toString();
            //create a json object
            JSONObject JSON = null;
            try {
                //insert the payload data into the Json object
                JSON = new JSONObject(dataPayload);
                //assigned values to the coordinates
                latitude = Double.valueOf(JSON.getString("lat"));
                longitude = Double.valueOf(JSON.getString("long"));

            //catch errors related to the JSON
            } catch (JSONException e) {
                e.printStackTrace();
            }
            //create a new intent object, this will hold information concerning the operation to perform by mapActivity
            Intent intent = new Intent(Global.PUSH_NOTIFICATION);
            //putExtra() adds extended data to the intent: in this case: payload information, longtitude, latitude and title
            intent.putExtra(Global.EXTRA_MESSAGE, dataPayload);
            intent.putExtra(Global.longitude, longitude);
            intent.putExtra(Global.latitude, latitude);
            intent.putExtra(Global.Title, message);

            // app is in foreground, broadcast the push message
            //this is used to register and send a broadcast of intents to local objects in your process.
            LocalBroadcastManager.getInstance(this).sendBroadcast(intent);
            //log info in logcat
            Log.i(TAG, "Data Message= " + dataPayload);


            // Check if message contains a notification payload and send broadcast to mainactivity
            if (remoteMessage.getNotification() != null) {
                //get notification body
                String notificationPayload = remoteMessage.getNotification().getBody();
                //create intent with context for application and MapsActivity class(the MapsActivity
                //will be receiving the information in the intent.
                Intent NotificationIntent = new Intent(getApplicationContext(), MapsActivity.class);
                //adding extended data to the intent:
                NotificationIntent.putExtra(Global.EXTRA_MESSAGE, dataPayload);
                NotificationIntent.putExtra(Global.longitude, longitude);
                NotificationIntent.putExtra(Global.latitude, latitude);
                NotificationIntent.putExtra(Global.Title, message);

                //PendingIntent communicate with the map application
                // by telling the map application to execute some predefined code
                // by using the listed permissions
                PendingIntent pendingIntent = PendingIntent.getActivity(getApplicationContext(),
                        0, NotificationIntent, PendingIntent.FLAG_UPDATE_CURRENT);

                //creating my notification
                //This enables you to build a notification with specific content and features
                Notification.Builder notification = new Notification.Builder(this)
                        .setContentTitle(Title)// set notification title
                        .setContentText(message)//set notification text
                        .setSmallIcon(android.R.drawable.star_on)//Sets which icon to display as a badge for this notification.
                        .setContentIntent(pendingIntent)//Supply a PendingIntent to be sent when the notification is clicked
                        .setAutoCancel(true);//make the notification disapper when the user click on it
                //Actions are typically displayed by the system as a button adjacent to the notification content.x

                // to retrieve a NotificationManager for informing the user of background events
                NotificationManager notificationManager =
                        (NotificationManager) getSystemService(Context.NOTIFICATION_SERVICE);
                //Post a notification to be shown in the status bar.
                notificationManager.notify(1 /* ID of notification */, notification.build());
            }
        }
    }
}
