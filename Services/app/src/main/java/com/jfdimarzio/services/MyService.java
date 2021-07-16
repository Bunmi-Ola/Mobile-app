package com.jfdimarzio.services;

import android.app.Service;
        import android.content.Intent;
        import android.os.AsyncTask;
        import android.os.Binder;
        import android.os.IBinder;
        import android.util.Log;
        import android.widget.Toast;

        import java.net.MalformedURLException;
        import java.net.URL;
        import java.util.Timer;
        import java.util.TimerTask;

// Example 1 - First Version TRY IT OUT Creating a Simple Service (Services.zip)
/*
public class MyService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        return START_STICKY;
    }
    @Override
    public void onDestroy() {

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}

*/
/*
// Example 2 - Second Version: TRY IT OUT Making Your Service Useful
public class MyService extends Service {
    @Override
    public IBinder onBind(Intent arg0) {
        return null;
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        // Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        try {
            int result = DownloadFile(new URL("http://www.amazon.com/somefile.pdf"));
            Log.v("Services App", "Downloaded " + result + " bytes");
            Toast.makeText(getBaseContext(),"Downloaded " + result + " bytes", Toast.LENGTH_LONG).show();
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return START_STICKY;
    }

    private int DownloadFile(URL url) {
        try {
            //---simulate taking some time to download a file---
            Thread.sleep(5000); // This will pause the UI thread. UI and service share same thread
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        //---return an arbitrary number representing
        // the size of the file downloaded---
        return 100;
    }
    @Override

    public void onDestroy() {

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}

*/


// Example 3 - Third Version: TRY IT OUT Performing Tasks in a Service Asynchronously (Services.zip)


//Service class is run in the application’s main thread which may reduce the application performance
// hence, the need to call the AsyncTask class which enables you to perform background execution
//as seen in this example
public class MyService extends Service {
    @Override
   //The onBind() method enables you to bind an activity to a service
    //This in turn enables an activity to directly access members and methods inside a service
    public IBinder onBind(Intent arg0) {
        return null;
    }

    //The onStartCommand() method is called when you start the service explicitly using the startService()
    //it returned the constant START_STICKY so that the service continues to run until it is explicitly stopped
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        // Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();
        try {
            //causes the service to download the files in the background,
            // and reports the progress as a percentage of files downloaded
            //the activity remains responsive while the files are downloaded in the background,
            // on a separate thread
            new DoBackgroundTask().execute(
                    new URL("http://www.amazon.com/somefiles.pdf"),
                    new URL("http://www.wrox.com/somefiles.pdf"),
                    new URL("http://www.google.com/somefiles.pdf"),
                    new URL("http://www.learn2develop.net/somefiles.pdf"));
        }
        catch (MalformedURLException e) {
            // TODO Auto-generated catch block
            e.printStackTrace();
        }
        return START_STICKY;
    }
//this method returns the total number of bytes downloaded (which is hardcoded as 100)
    private int DownloadFile(URL url) {
        try {
            //---simulate taking some time to download a file---
            Thread.sleep(5000);
        }
        catch (InterruptedException e) {
            e.printStackTrace();
        }
        //---return an arbitrary number representing
        // the size of the file downloaded---
        return 100;
        }

        //The AsyncTask class enables you to perform background execution
        // without needing to manually handle threads and handlers.
    //URL, Integer, and Long generic types specified data type
        // used by the three methods implemented in an AsyncTask class:
        // doInBackground(), onProgressUpdate() and onPostExecute()
        private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {


//method accepts an array URL, is also executed in the background thread
// and is where you put your long-running code
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++) {
                totalBytesDownloaded += DownloadFile(urls[i]);
                //---calculate percentage downloaded and
                // report its progress---
                //report the progress of your task and invokes the next method, onProgressUpdate()
                publishProgress((int) (((i+1) / (float) count) * 100));
            }
            return totalBytesDownloaded;
        }

        //This method is invoked in the UI thread and
        // it is to report the progress of the background task to the user
        protected void onProgressUpdate(Integer... progress) {
            Log.d("Downloading files", String.valueOf(progress[0]) + "% downloaded");
            Toast.makeText(getBaseContext(),
                    String.valueOf(progress[0]) + "% downloaded",
                    Toast.LENGTH_LONG).show();
        }

        //This method is invoked in the UI thread and
        // is called when the doInBackground() method has finished execution.
        protected void onPostExecute(Long result) {
            Toast.makeText(getBaseContext(),
                    "Downloaded " + result + " bytes",
                    Toast.LENGTH_LONG).show();
            //stopSelf() method is the equivalent of calling the stopService() method to stop the service
            stopSelf();
        }
    }

    //onDestroy() method is called when the service is stopped using the stopService() method

 /*   The system invokes this method when the service is no longer used and is being destroyed.
    Your service should implement this to clean up any resources such as threads, registered listeners,
    or receivers. This is the last call that the service receives.*/
    @Override
    public void onDestroy() {
        super.onDestroy();
        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }
}

/* // Original Code
public class MyService extends Service {
    int counter = 0;
    URL[] urls;
    static final int UPDATE_INTERVAL = 1000;
    private Timer timer = new Timer();
    private final IBinder binder = new MyBinder();
    public class MyBinder extends Binder {
        MyService getService() {
            return MyService.this;
        }
    }
    @Override
    public IBinder onBind(Intent arg0) {
        return binder;
    }
    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        // We want this service to continue running until it is explicitly
        // stopped, so return sticky.
        Toast.makeText(this, "Service Started", Toast.LENGTH_LONG).show();

        //doSomethingRepeatedly();

        new DoBackgroundTask().execute(urls);
        return START_STICKY;
        //return START_NOT_STICKY;
    }

    private void doSomethingRepeatedly() {
        timer.scheduleAtFixedRate(new TimerTask() {
            public void run() {
                Log.d("MyService", String.valueOf(++counter));
            }
        }, 0, UPDATE_INTERVAL);
    }

    private int DownloadFile(URL url) {
        try {
            //---simulate taking some time to download a file---
            Thread.sleep(5000);
        } catch (InterruptedException e) {
            e.printStackTrace();
        }
        //---return an arbitrary number representing
        // the size of the file downloaded---
        return 100;
    }
    private class DoBackgroundTask extends AsyncTask<URL, Integer, Long> {
        protected Long doInBackground(URL... urls) {
            int count = urls.length;
            long totalBytesDownloaded = 0;
            for (int i = 0; i < count; i++) {
                totalBytesDownloaded += DownloadFile(urls[i]);
                //---calculate percentage downloaded and
                // report its progress---
                publishProgress((int) (((i+1) / (float) count) * 100));
            }
            return totalBytesDownloaded;
        }
        protected void onProgressUpdate(Integer... progress) {
            Log.d("Downloading files",
                    String.valueOf(progress[0]) + "% downloaded");
            Toast.makeText(getBaseContext(),
                    String.valueOf(progress[0]) + "% downloaded",
                    Toast.LENGTH_LONG).show();
        }
        protected void onPostExecute(Long result) {
            Toast.makeText(getBaseContext(),
                    "Downloaded " + result + " bytes",
                    Toast.LENGTH_LONG).show();
            stopSelf();
        }
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        if (timer != null){
            timer.cancel();
        }

        Toast.makeText(this, "Service Destroyed", Toast.LENGTH_LONG).show();
    }

}

     */