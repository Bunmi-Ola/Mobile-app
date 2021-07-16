package com.example.accesscontentprovider;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.app.ActivityCompat;
import androidx.core.content.ContextCompat;

import android.Manifest;
import android.app.ListActivity;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.content.pm.PackageManager;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.provider.ContactsContract;
import android.view.View;
import android.widget.CursorAdapter;
import android.widget.EditText;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {
    final private int REQUEST_READ_CONTACTS = 123;
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

    }
    public void onClickAddDetails(View view) {
        ContentValues values = new ContentValues();
        values.put("title", ((EditText)
                findViewById(R.id.txtTitle)).getText().toString());
        values.put("isbn", ((EditText)
                findViewById(R.id.txtISBN)).getText().toString());
        Uri uri = getContentResolver().insert(
                Uri.parse(
                        "content://com.example.contentprovidersnew.Books/books"),
                values);
        Toast.makeText(getBaseContext(), "New Record Inserted", Toast.LENGTH_LONG).show();
    }


    public void onClickShowDetails(View view) {
        // Retrieve employee records
        TextView resultView = (TextView) findViewById(R.id.res);

        Uri allTitles = Uri.parse(
                "content://com.example.contentprovidersnew.Books/books");
        Cursor cursor;

        android.content.CursorLoader cursorLoader = new CursorLoader(
                this,
                allTitles, null, null, null,
                "title desc");
        //Called on a worker thread to perform the actual load
        // and to return the result of the load operation
        cursor = cursorLoader.loadInBackground();

        if (cursor.moveToFirst()) {
            StringBuilder strBuild = new StringBuilder();
            do{
                //toast provides simple feedback about the above operation in a small popup
                strBuild.append("\n" + cursor.getString(cursor.getColumnIndex("isbn")) +
                        "-" + cursor.getString(cursor.getColumnIndex("title")));
            } while (cursor.moveToNext());

            resultView.setText(strBuild);
        } else {
            resultView.setText("No Records Found");
        }
    }
}