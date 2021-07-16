package com.example.broadcastreceiverdemo1;

import java.util.Calendar;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Bundle;
import android.text.format.DateFormat;
import android.util.Log;
import android.view.Menu;
import android.widget.TextView;

public class MainActivity extends Activity {

    // broadcast receiver (receiver) is an Android
    // component which allows you to register for system or application events

    // creates a new BroadcastReceiver component object to handle each broadcast that it receives
    //This object is valid only for the duration of the call to onReceive(Context, Intent)
    BroadcastReceiver receiver;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }

    @Override
    public void onResume() {
        super.onResume();
        setTime();

       // Create an instance of BroadcastReceiver
        // the receiver class is instantiated and registered every time  the activity's

        ////Receiving the result
        receiver = new BroadcastReceiver() {

            //This method is called when the BroadcastReceiver is receiving an Intent broadcast.
            //The broadcast receiver in the example calls the setTime() method:
            @Override
            public void onReceive(Context context, Intent intent) {
                setTime();
            }
        };

        //Create an IntentFilter and Register broadcast receiver and filter results
        //it specify an intent action that will cause the receiver to trigger
        IntentFilter intentFilter = new IntentFilter(
                Intent.ACTION_TIME_TICK);

        // register a receiver by passing the receiver and intentFilter
        //Context-registered receivers receive broadcasts as long as their registering context is valid
        //it is receive for any local broadcasts that match the given IntentFilter.
        this.registerReceiver(receiver, intentFilter);
    }

    public void onPause() {

        //Unregister a previously registered BroadcastReceiver.
        // All filters that have been registered for this BroadcastReceiver will be removed.
        this.unregisterReceiver(receiver);
        super.onPause();
    }
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_main, menu);
        return true;
    }

    private void setTime() {
        //Calendar class used to get a calendar using the current time
        Calendar calendar = Calendar.getInstance();
        CharSequence newTime = DateFormat.format(
                "kk:mm", calendar);
        TextView textView = (TextView) findViewById(
                R.id.textView1);
        //update textview
        textView.setText(newTime);
    }
}