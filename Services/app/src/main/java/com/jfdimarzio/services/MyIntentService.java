package com.jfdimarzio.services;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MyIntentService extends IntentService {

    private Thread thread = new Thread();
  /*  IntentService class is a base class for Service that handles asynchronous requests
  (expressed as Intents) on demand.
    It is started just like a normal service; and it executes its task within a worker thread and
    terminates itself when the task is completed.*/
    public MyIntentService() {
        super("MyIntentServiceName");
    }
    @Override
  // The onHandleIntent() method is where you place the code that needs to be executed on a separate thread
    //like  in this case, downloading a file from a server
    //When the code has finished executing, the thread is terminated and the service is stopped automatically
    protected void onHandleIntent(Intent intent) {
        try {
            int result =
                    DownloadFile(new URL("http://www.amazon.com/somefile.pdf"));
           thread.start();
            Log.d("IntentService", "Downloaded " + result + " bytes");

            //---send a broadcast to inform the activity
            // that the file has been downloaded---
            Intent broadcastIntent = new Intent();
            broadcastIntent.setAction("FILE_DOWNLOADED_ACTION");
            getBaseContext().sendBroadcast(broadcastIntent);

        } catch (MalformedURLException e) {
            e.printStackTrace();
        }
    }

    //this method returns the total number of bytes downloaded (which is hardcoded as 100)
    private int DownloadFile(URL url) {
        try {
            //---simulate taking some time to download a file---
            thread.sleep(5000);
        } catch (InterruptedException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return 100;
    }

    //onDestroy() method is called when the service is stopped using the stopService() method
    @Override
    public void onDestroy() {

        thread.interrupt();

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        super.onDestroy();
    }

}
