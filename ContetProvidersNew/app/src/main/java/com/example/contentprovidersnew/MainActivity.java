package com.example.contentprovidersnew;
import android.content.ContentValues;
import android.content.CursorLoader;
import android.database.Cursor;
import android.net.Uri;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

public class MainActivity extends AppCompatActivity {

    //onCreate() method is a lifecycle method and runs on the UI thread
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
    }
    public void onClickAddTitle(View view) {
        //---add a book---
        //create a new contentvlaue object and populate with various information about the book
        ContentValues values = new ContentValues();
        values.put(BooksProvider.TITLE, ((EditText)
                findViewById(R.id.txtTitle)).getText().toString());
        values.put(BooksProvider.ISBN, ((EditText)
                findViewById(R.id.txtISBN)).getText().toString());

        // ContentResolver with arguments that are passed to the corresponding method of ContentProvider.
        //Inserts a row into a table at the given URL  into the content provider.
        Uri uri = getContentResolver().insert(
                BooksProvider.CONTENT_URI, values);
        Toast.makeText(getBaseContext(),uri.toString(),
                Toast.LENGTH_LONG).show();
    }
    public void onClickRetrieveTitles(View view) {
        //---retrieve the titles---
        Uri allTitles = Uri.parse(
                "content://com.example.contentprovidersnew.Books/books");
        Cursor c;
        //CursorLoader uses a ContentProvider to run a query against a database,
        // then returns the cursor produced from the ContentProvider back to the activity
        //The cursorLoader query is handled on a background thread
        // (courtesy of being built on AsyncTaskLoader) so that large data queries do not block the UI
        //A loader that queries the ContentResolver and returns a Cursor
        CursorLoader cursorLoader = new CursorLoader(
                this,
                allTitles, null, null, null,
                "title desc");
        //Called on a worker thread to perform the actual load
        // and to return the result of the load operation
        c = cursorLoader.loadInBackground();
        if (c.moveToFirst()) {
            do{
                //toast provides simple feedback about the above operation in a small popup
                Toast.makeText(this,
                        c.getString(c.getColumnIndex(
                                BooksProvider._ID)) + ", " +
                                c.getString(c.getColumnIndex(
                                        BooksProvider.TITLE)) + ", " +
                                c.getString(c.getColumnIndex(
                                        BooksProvider.ISBN)),
                        Toast.LENGTH_SHORT).show();
            } while (c.moveToNext());
        }
    }
}
