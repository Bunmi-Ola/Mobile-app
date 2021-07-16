package com.hfad.joke;

import android.app.IntentService;
import android.app.NotificationManager;
import android.app.PendingIntent;
import android.content.Intent;
import android.os.Handler;
import android.os.Looper;
import android.support.v4.app.NotificationCompat;
import android.util.Log;
import android.widget.Toast;

public class DelayedMessageService extends IntentService {

    public static final String EXTRA_MESSAGE = "message";
    public static final int NOTIFICATION_ID = 5453;


    public DelayedMessageService() {
        super("DelayedMessageService");
    }

    //onHandleIntent() method contain the code to run each time the service is passed an intent
    @Override
    protected void onHandleIntent(Intent intent) {
        synchronized (this) {
            try {
                wait(2000);
            } catch (InterruptedException e) {
                e.printStackTrace();
            }
        }
        //get text from intent
        String text = intent.getStringExtra(EXTRA_MESSAGE);
        //call showtext() method
        showText(text);
        //call toast method
        showToast("MyService is handling intent.");
    }

    private void showText(final String text) {
        //tag is use to identify the source of the message, msg: the message itself.
        //this message will be show in logcat
        Log.v("DelayedMessageService", "The message is: " + text);



    }

    public void showToast(String message) {
        final String msg = message;
        // Handler object receives messages and runs code to handle the messages
        // Handler enqueues task in the MessageQueue using Looper
        // and also executes them when the task comes out of the MessageQueue.
        //Looper keeps thread alive, loops through
        //in this case, the Handler object is attached to the UI thread,
        // so the code that handles messages will run on the UI thread
        new Handler(Looper.getMainLooper()).post(new Runnable() {
            @Override
            public void run() {
                Toast.makeText(getApplicationContext(), msg, Toast.LENGTH_LONG).show();
            }
        });
    }


}
