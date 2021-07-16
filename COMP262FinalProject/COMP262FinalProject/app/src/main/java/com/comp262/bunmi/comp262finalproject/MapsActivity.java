package com.comp262.bunmi.comp262finalproject;

import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;
import androidx.fragment.app.FragmentActivity;
import androidx.localbroadcastmanager.content.LocalBroadcastManager;

import android.Manifest;
import android.app.AlertDialog;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.content.pm.PackageManager;
import android.location.LocationListener;
import android.location.LocationManager;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;
import android.os.Bundle;
import android.provider.Settings;
import android.util.Log;
import android.widget.Toast;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.tasks.OnSuccessListener;
import com.google.firebase.iid.FirebaseInstanceId;
import com.google.firebase.iid.InstanceIdResult;

import java.util.HashMap;

//The GoogleMap object is the internal representation of the map itself
public class MapsActivity extends FragmentActivity implements OnMapReadyCallback {
    //create map object
    private GoogleMap mMap;
    // returns the simple name of the underlying class
    private static final String TAG = MapsActivity.class.getSimpleName();
    //create HashMap for storing of objects
    public static HashMap<String, Object> items = new HashMap<String, Object>();

    //coordinates
    private double latitude = -33.865143;
    private double longitude = 151.209900;

    private String token = "";
    //other notification information
    private String Location = "";
    private String Title = "Marker in Sydney";
//A broadcast receiver (receiver) is an Android component which allows you to register for system or application events.
// All registered receivers for an event are notified by the Android runtime once this event happens.
    private BroadcastReceiver mRegistrationBroadcastReceiver;
    //Set variables for permission
    final private int REQUEST_COURSE_ACCESS = 123;
    boolean permissionGranted = false;
    //create locationManager classs
    // is the main class through which your application can access location services
    LocationManager lm;

//initialize your activity. When Activity is started and application is not loaded,
// then both onCreate() methods will be called
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        //Set the activity content from a layout resource
        setContentView(R.layout.activity_maps);

        Log.i(TAG, "THIS IS HOW MANY TIMES ONCREATE FIRES=");
        //get the android_ID
        Global.DEVICE_NAME = Settings.Secure.getString(this.getContentResolver(), Settings.Secure.ANDROID_ID);
        // Set a name for the device and truncate to last four characters
        Global.DEVICE_NAME = Global.DEVICE_NAME.substring(Global.DEVICE_NAME.length() - 4, Global.DEVICE_NAME.length());

        // Display the token if the device is already registered
        //Returns a token that authorizes a sender ID to perform an action on behalf of the application identified by Instance ID.
        FirebaseInstanceId.getInstance().getInstanceId().addOnSuccessListener(new OnSuccessListener<InstanceIdResult>() {
            @Override
            public void onSuccess(InstanceIdResult instanceIdResult) {
                token = instanceIdResult.getToken();
                Log.i(TAG, "Token=" + token);
            }
        });
//call the isConnected() method to check if data connection is available
        //if not, notify the user through an alert box.
        if (!isConnected()) {
            AlertDialog alertDialog = new AlertDialog.Builder(MapsActivity.this).create();
            alertDialog.setTitle("Connection");
            alertDialog.setIcon(android.R.drawable.stat_sys_warning);
            alertDialog.setMessage("Data connection not Available.");
            alertDialog.setCancelable(false);
            alertDialog.setButton(0, "OK", new DialogInterface.OnClickListener() {
                public void onClick(DialogInterface dialog, int which) {
                }
            });
            alertDialog.show();
        }
        //check if there is any incoming intent
        if (getIntent().getExtras() != null) {
            //if there is an incoming intent, get information and store in global variables
            Intent intent = getIntent();
            latitude = intent.getFloatExtra("lat", 0);
          longitude =  intent.getFloatExtra("long", 0);
          Title =  intent.getStringExtra(Global.Title);

        }
    //initialize broadcastreceiver
        //broadcast message is usually wrap in an Intent
        mRegistrationBroadcastReceiver = new BroadcastReceiver() {
            @Override
            //receive broadcast
            public void onReceive(Context context, Intent intent) {
                // Check the intent type
                if (intent.getAction().equals(Global.PUSH_NOTIFICATION)) {
                    // FCM message received
                    Log.i(TAG, "Receiver=" + Global.PUSH_NOTIFICATION);
                    //get payload information
                    String newMessage = intent.getStringExtra(Global.EXTRA_MESSAGE);
                    //check if intent is not null, then extract information
                    if (intent.getExtras() != null) {

                        for (String key : intent.getExtras().keySet()) {
                            Object value = intent.getExtras().get(key);
                            items.put(key, value);
                        }

                         latitude = Float.valueOf(String.valueOf(items.get(Global.latitude)));
                        longitude = Float.valueOf(String.valueOf(items.get(Global.longitude)));
                        Title = String.valueOf(items.get(Global.Title));
                        //quick display of info to user
                        Toast.makeText(getApplicationContext(), "LONGITUDE: " + longitude + " LATITUDE :" + latitude, Toast.LENGTH_LONG).show();

                    }
                    //call setLocation method to change map location
                    setLocation(latitude, longitude, Title);

                }

            }
        };

    }
//onResume() is called when the activity that was hidden comes back to view on the screen
    @Override
    protected void onResume() {
        super.onResume();

        // Obtain the SupportMapFragment and get notified
        // when the map is ready to be used.
        SupportMapFragment mapFragment =
                (SupportMapFragment) getSupportFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);


        // FCM "new message" receiver
        //Register a receive for any local broadcasts that match the given IntentFilter.
        LocalBroadcastManager.getInstance(this).registerReceiver(mRegistrationBroadcastReceiver,
                new IntentFilter(Global.PUSH_NOTIFICATION));
    }
//onPause() is where you deal with the user pausing active interaction with the activity
    @Override
    protected void onPause() {
        //Unregister a previously registered BroadcastReceiver
        LocalBroadcastManager.getInstance(this).unregisterReceiver(mRegistrationBroadcastReceiver);
        super.onPause();
        //---remove the location listener---
        //check permission
        if (ContextCompat.checkSelfPermission(this,
                android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(
                this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_COURSE_ACCESS);
            return;
        } else {
            permissionGranted = true;
        }

    }
//method to change location of the map base on coordinates received
    public void setLocation(double Latitude, double Longitude, String Title) {
        //create a LatLng object
        //it is a immutable class representing a pair of latitude and longitude coordinates, stored as degrees
        LatLng newLocation = new LatLng(
                (double) (Latitude),
                (double) (Longitude));
        //set a marker with location description as the Title
        //Markers indicate single locations on the map
        mMap.addMarker(new MarkerOptions().position(newLocation).title(Title));
        // change which part of the world is visible on the map
        mMap.moveCamera(CameraUpdateFactory.newLatLngZoom(newLocation, 10));
        // mMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        Log.i(TAG, "SETTING NEW LOCATION " + Location);

    }


    /**
     * Manipulates the map once available.
     * This callback is triggered when the map is ready to be used.
     * This is where we can add markers or lines, add listeners or move the camera. In this case,
     * we just add a marker near Sydney, Australia.
     * If Google Play services is not installed on the device, the user will be prompted to install
     * it inside the SupportMapFragment. This method will only be triggered once the user has
     * installed Google Play services and returned to the app.
     */
    @Override
    public void onMapReady(GoogleMap googleMap) {
//initialised googlemap
        //it Connect to the Google Maps service and also helps display controls on the map
        mMap = googleMap;
//Get a Reference to LocationManager
        lm = (LocationManager) getSystemService(getApplicationContext().LOCATION_SERVICE);

        // get location if there is an intent from a user-tapped notification
        //check if there is an incoming intent
        if (getIntent().getExtras() != null) {

            Intent intent = getIntent();
//extract informtion from intent
            for (String key : getIntent().getExtras().keySet()) {

                    Object value = intent.getExtras().get(key);
                    items.put(key, value);
                }
                //set the variables
                latitude = Float.valueOf(String.valueOf(items.get(Global.latitude)));
                longitude = Float.valueOf(String.valueOf(items.get(Global.longitude)));
                Title = String.valueOf(items.get(Global.Title));
        }

            // Sets map position.
            LatLng position = new LatLng(latitude, longitude);
            mMap.addMarker(new MarkerOptions().position(position).title(Title));
            mMap.moveCamera(CameraUpdateFactory.newLatLng(position));



        //check permission from user
        if (ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION)
                != PackageManager.PERMISSION_GRANTED && ContextCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION)
                != PackageManager.PERMISSION_GRANTED) {
            //request the missing permissions
            ActivityCompat.requestPermissions(this, new String[]{android.Manifest.permission.ACCESS_COARSE_LOCATION, Manifest.permission.ACCESS_FINE_LOCATION}, REQUEST_COURSE_ACCESS);
            return;
        } else {
            // permission has been granted, continue as usual
            permissionGranted = true;

        }
        //call setLocation() method to set location on map
        setLocation(latitude, longitude, Title);
        //short display of information on application
     Toast.makeText(getApplicationContext(), "LONGITUDE: " + longitude + " LATITUDE :" + latitude, Toast.LENGTH_LONG).show();


    }
//method to check connectivity
    private boolean isConnected() {
        ConnectivityManager connectivity = (ConnectivityManager) getSystemService(Context.CONNECTIVITY_SERVICE);
        if (connectivity != null) {
            NetworkInfo[] info = connectivity.getAllNetworkInfo();
            if (info != null)
                for (int i = 0; i < info.length; i++)
                    if (info[i].getState() == NetworkInfo.State.CONNECTED) {
                        return true;
                    }
        }
        return false;
    }
//use for  receiving the results for permission requests.
    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_COURSE_ACCESS:
                if (grantResults.length != 0 && grantResults[0] == PackageManager.PERMISSION_GRANTED) {
                    permissionGranted = true;
                } else {
                    permissionGranted = false;
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions,
                        grantResults);
        }
    }

}
