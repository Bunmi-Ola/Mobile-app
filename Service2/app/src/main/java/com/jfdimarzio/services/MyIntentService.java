package com.jfdimarzio.services;


import android.app.IntentService;
import android.content.Intent;
import android.util.Log;
import android.widget.Toast;

import java.net.MalformedURLException;
import java.net.URL;

public class MyIntentService extends IntentService {

    private Thread thread = new Thread();

    public MyIntentService() {
        super("MyIntentServiceName");
    }
    @Override
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

    @Override
    public void onDestroy() {

        thread.interrupt();

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();

        super.onDestroy();
    }

}
