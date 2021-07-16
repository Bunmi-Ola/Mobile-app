package com.example.providernew;


import android.Manifest;
import android.app.ListActivity;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import android.support.v4.app.ActivityCompat;
import android.support.v4.content.ContextCompat;
import android.support.v4.content.CursorLoader;
import android.support.v4.widget.CursorAdapter;
import android.support.v4.widget.SimpleCursorAdapter;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

public class MainActivity extends ListActivity {
    final private int REQUEST_READ_CONTACTS = 123;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        if (ContextCompat.checkSelfPermission(this, Manifest.permission.READ_CONTACTS) != PackageManager.PERMISSION_GRANTED) {

            ActivityCompat.requestPermissions(this,
                    new String[]{Manifest.permission.READ_CONTACTS},
                    REQUEST_READ_CONTACTS);
        } else {
            ListContacts();
        }


    }

    @Override
    public void onRequestPermissionsResult(int requestCode, String[] permissions, int[] grantResults) {
        switch (requestCode) {
            case REQUEST_READ_CONTACTS:
                if (grantResults[0] == PackageManager.PERMISSION_GRANTED) {

                    ListContacts();

                } else {
                    Toast.makeText(MainActivity.this, "Permission Denied", Toast.LENGTH_SHORT).show();
                }
                break;
            default:
                super.onRequestPermissionsResult(requestCode, permissions, grantResults);
        }
    }

    private void PrintContacts(Cursor c) {
        if (c.moveToFirst()) {
            do {

                String contactID = c.getString(c.getColumnIndex(
                        ContactsContract.Contacts._ID));
                String contactDisplayName =
                        c.getString(c.getColumnIndex(
                                ContactsContract.Contacts.DISPLAY_NAME));
                Log.v("Content Providers", contactID + "," +
                        contactDisplayName);
                //---get phone number---
                //int hasPhone = c.getInt(c.getColumnIndex(ContactsContract.Contacts.HAS_PHONE_NUMBER));
//The external application can not directly access ContentProvider. For that, you need to first interact
// with another class called ContentResolver Think ContentResolver as a ContentProvider finder.
                /* In the below statement, a cursor is initialized. It queries the built-in Contacts content provider.
                In the query method, the ContactsContract.CommonDataKinds.Phone.CONTENT_URI
                 is a URI that retrieves all records of the content provider into the cursor object */
                // Does a query against the table and returns a Cursor object
                   Cursor phoneCursor =
                        getContentResolver().query(
                                ContactsContract.CommonDataKinds.Phone.CONTENT_URI, // The content URI of the table
                                null, // The columns to return for each row
                                ContactsContract.CommonDataKinds.Phone.CONTACT_ID + " = " +
                                        contactID, // specifies the criteria for selecting rows.
                                null, // Selection criteria
                                null);  // The sort order for the returned rows

               //The return value of the query method is a Cursor object.
                //The cursor is used to navigate between the rows of the result and to read the columns of the current row


                while (phoneCursor.moveToNext()) {
                    //used to log verbose messages
                    //Verbose logging is a computer logging mode that records more information than the usual logging mode
                    Log.v("Content Providers",
                            phoneCursor.getString(
                                    phoneCursor.getColumnIndex(
                                            ContactsContract.CommonDataKinds.Phone.NUMBER)));
                }
                phoneCursor.close();

            } while (c.moveToNext());
        }
    }


    protected void ListContacts() {
        Uri allContacts = Uri.parse("content://contacts/people");
        Cursor c;
        CursorLoader cursorLoader = new CursorLoader(
                this,
                allContacts,
                null,
                null,
                null,
                null);
        c = cursorLoader.loadInBackground();

        PrintContacts(c);

        String[] columns = new String[]{
                ContactsContract.Contacts.DISPLAY_NAME,
                ContactsContract.Contacts._ID};

        int[] views = new int[]{R.id.contactName, R.id.contactID};
        SimpleCursorAdapter adapter;

        adapter = new SimpleCursorAdapter(
                this, R.layout.activity_main, c, columns, views,
                CursorAdapter.FLAG_REGISTER_CONTENT_OBSERVER);

        this.setListAdapter(adapter);


    }
}