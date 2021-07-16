package com.example.contentprovidersnew;


import android.content.ContentProvider;
import android.content.ContentUris;
import android.content.ContentValues;
import android.content.Context;
import android.content.UriMatcher;
import android.database.Cursor;
import android.database.SQLException;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.database.sqlite.SQLiteQueryBuilder;
import android.net.Uri;
import android.text.TextUtils;
import android.util.Log;
//Content Provider facilitates access to a central data store
// or warehouse to allow data sharing and data manipulation across different applications.
public class BooksProvider extends ContentProvider {
//Accessing a Content Provider, is a name for the entire content provider
// It communicates with provider object,
// which in turn accepts the request to access data and returns the desired results.
    static final String PROVIDER_NAME = "com.example.contentprovidersnew.Books";
    // Content URI is one of the arguments used to identify the data in a provider
    // the url contains the authority which is the symbolic name of the provider, and is unique for every content provider
    //the authority serves as the Android-internal name
    //the scheme in URL for content providers is always "content"
    //also contains the Path: Path helps distinguish the required data from the complete database
    //for example, distinguishes between audio files, video files and images using different paths for each of these types of media
    //the path component of URI is used to select the correct (requested) data table
    //the optional id part points to an individual row in a table (but not in this example)

    /*There are two types of URIs: directory- and id-based URIs. If no id is specified a URI is automatically a directory-based URI.
    You use directory-based URIs to access multiple elements of the same type (e.g. all songs of a band). All CRUD-operations are possible with directory-based URIs.
    You use id-based URIs if you want to access a specific element. You cannot create objects using an id-based URI - but reading, updating and deleting is possible.
*/
//scheme//authotity/path
    static final Uri CONTENT_URI = Uri.parse("content://"+ PROVIDER_NAME + "/books");
    //colums in the table
    static final String _ID = "_id";
    static final String TITLE = "title";
    static final String ISBN = "isbn";
    static final int BOOKS = 1;
    static final int BOOK_ID = 2;

    //the uriMatcher help to choose which action to take for an incoming content URI
    // or determine witch database request is being made
    private static final UriMatcher uriMatcher;
    // prepare the UriMatcher
    //You pass the authority, a path pattern and an int value to the addURI() method
    //When you initialize the UriMatcher you state for each URI which int value belongs to it.
    // Now whenever you need to react differently depending on the URI you use the UriMatcher
    static{
        // Creates a UriMatcher object.
        uriMatcher = new UriMatcher(UriMatcher.NO_MATCH);
        //Sets the integer value for multiple rows in the book table to BOOKS
        uriMatcher.addURI(PROVIDER_NAME, "books", BOOKS);
        //Matches a content URI for single rows in books table to BOOK_ID
        uriMatcher.addURI(PROVIDER_NAME, "books/#", BOOK_ID);
    }
    //---for database use---
    SQLiteDatabase booksDB;
    // Database Name
    static final String DATABASE_NAME = "Books";
    // Table Name
    static final String DATABASE_TABLE = "titles";
    // Database Version
    static final int DATABASE_VERSION = 1;
    static final String DATABASE_CREATE =
            "create table " + DATABASE_TABLE +
                    " (_id integer primary key autoincrement, "
                    + "title text not null, isbn text not null);";

    //SQLiteOpenHelper keeps just one SQLiteDatabase object per SQLiteSession
    private static class DatabaseHelper extends SQLiteOpenHelper
    {
        DatabaseHelper(Context context) {
            super(context, DATABASE_NAME, null, DATABASE_VERSION);
        }

        //only onCreate() runs when the provider is started // database is first created.
        //SQLiteDatabase class is the class that represents the actual database
      //SQLiteOpenHelper class, however, is where most of the action takes place.
        // This class will enable us to get access to a database and initialize an instance of SQLiteDatabase

        @Override
        public void onCreate(SQLiteDatabase db)
        {
            db.execSQL(DATABASE_CREATE);
        }

        //onUpgrade() is called when the database file exists or whenever DATABASE_VERSION is incremented

        @Override
        public void onUpgrade(SQLiteDatabase db, int oldVersion,
                              int newVersion) {
            Log.w("Provider database",
                    "Upgrading database from version " +
                            oldVersion + " to " + newVersion +
                            ", which will destroy all old data");
            db.execSQL("DROP TABLE IF EXISTS titles");
            onCreate(db);
        }
    }
//This method deletes an existing record from the content provider.
    @Override
    public int delete(Uri arg0, String arg1, String[] arg2) {
        // arg0 = uri
        // arg1 = selection
        // arg2 = selectionArgs

        //First you once again use your UriMatcher to distinguish between dir based and id based URIs
        int count=0; // Number of rows effected
        switch (uriMatcher.match(arg0)){
// determine action to take for an incoming content URI
            // delete all records from the table
            case BOOKS:
                count = booksDB.delete(
                        DATABASE_TABLE,
                        arg1,
                        arg2);
                break;
            // Code to delete a single row in the table

             case BOOK_ID:
                String id = arg0.getPathSegments().get(1);
                count = booksDB.delete(
                        DATABASE_TABLE,
                        _ID + " = " + id +
                                (!TextUtils.isEmpty(arg1) ? " AND (" +
                                        arg1 + ')' : ""),
                arg2);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + arg0);
        }

        // Use this on the URI passed into the function to notify any content observers that the uri has changed.
        getContext().getContentResolver().notifyChange(arg0, null);
        return count;
    }
    //This method returns the string representing the type of data you are returning,
    // either a directory of multiple results, or an individual item:
    //The returned MIME type should start with vnd.android.cursor.item for a single record,
    // or vnd.android.cursor.dir/ for multiple items.
    @Override
    public String getType(Uri uri) {
        switch (uriMatcher.match(uri)){
            //---get all books---
            case BOOKS:
                return "vnd.android.cursor.dir/vnd.learn2develop.books ";

            //---get a particular book---
            case BOOK_ID:
                return "vnd.android.cursor.item/vnd.learn2develop.books ";

            default:
                throw new IllegalArgumentException("Unsupported URI: " + uri);
        }
    }
    //This method inserts a new record into the content provider.

    //URI: The URI of the Content Provider
    //ContentValues: This contains the values that would be included.
    @Override
    public Uri insert(Uri uri, ContentValues values) {
        //---add a new book---
        long rowID = booksDB.insert(
                DATABASE_TABLE,
                "",
                values);
        //---if added successfully---
        if (rowID>0)
        {
            //Appends the given ID to the end of the path.
            Uri _uri = ContentUris.withAppendedId(CONTENT_URI, rowID);
            // Use this on the URI passed into the function to notify any observers that the uri has
            // changed.
            getContext().getContentResolver().notifyChange(_uri, null);
            return _uri;
        }
        throw new SQLException("Failed to insert row into " + uri);
    }
    @Override
    public boolean onCreate() {
        Context context = getContext();
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        booksDB = dbHelper.getWritableDatabase();
        return (booksDB == null)? false:true;
    }

    //URL: This is the only argument that must not be null. IT IS The URI of the object(s) to access
    //Projection: The query should return a set of columns from the entire database table.
    //Selection Clause: A filter declaring which rows/records  to return
    //eg if you enter an alphabet (say ‘P’) in the search column of your address book,
    // then it would return all the contact details starting with ‘P
    //String[]	selectionArgs: The binding parameters to the previous selection argument
    //SortOrder: If the result should be ordered you must use this argument to determine the sort order

    //This method receives a request from a client and return value of the query method is a Cursor object.
    // The cursor is used to navigate between the rows of the result and to read the columns of the current row.

    //Queries for all objects that fit the URI
    @Override
    public Cursor query(Uri uri, String[] projection, String selection,
                        String[] selectionArgs, String sortOrder) {
//This is a convenience class that helps build SQL queries to be sent to SQLiteDatabase objects.
        SQLiteQueryBuilder sqlBuilder = new SQLiteQueryBuilder();
        // Set the table we're querying.
        sqlBuilder.setTables(DATABASE_TABLE);

        // If the query ends in a specific record number(BOOK_ID), we're
        // being asked for a specific record, so set the
        // WHERE clause in our query.
        if (uriMatcher.match(uri) == BOOK_ID)
            //---if getting a particular book---
            sqlBuilder.appendWhere(
                    _ID + " = " + uri.getPathSegments().get(1));
        //sort using title
        if (sortOrder==null || sortOrder=="")
            sortOrder = TITLE;
        // Make the query.
        Cursor c = sqlBuilder.query(
                booksDB,
                projection,
                selection,
                selectionArgs,
                null,
                null,
                sortOrder);
        //---register to watch a content URI for changes---
        // Set the notification URI for the cursor to the one passed into the function. This
        // causes the cursor to register a content observer to watch for changes that happen to
        // this URI and any of it's descendants. By descendants, we mean any URI that begins
        // with this path.
        c.setNotificationUri(getContext().getContentResolver(), uri);

        return c;
    }

    //URI: The URI of the Content Provider
    //ContentValues: This contains the values that would replace the existing data.
    //Selection: This can help select the specific records to update

    //this method updates an existing record from the content provider
    @Override
    public int update(Uri uri, ContentValues values, String selection,
                      String[] selectionArgs) {
        int count = 0; // Number of rows effected
        // Code to update all rows in the table case

        switch (uriMatcher.match(uri)){
            case BOOKS:
                count = booksDB.update(
                        DATABASE_TABLE,
                        values,
                        selection,
                        selectionArgs);
                break;
            // Code to update a single row in the table

            case BOOK_ID:
                count = booksDB.update(
                        DATABASE_TABLE,
                        values,
                        _ID + " = " + uri.getPathSegments().get(1) +
                                (!TextUtils.isEmpty(selection) ? " AND (" +
                                        selection + ')' : ""),
                selectionArgs);
                break;
            default: throw new IllegalArgumentException("Unknown URI " + uri);
        }
        // Use this on the URI passed into the function to notify any content observers that the uri has changed.
        getContext().getContentResolver().notifyChange(uri, null);
        return count;
    }
}
