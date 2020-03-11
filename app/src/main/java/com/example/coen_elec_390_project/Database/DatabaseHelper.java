package com.example.coen_elec_390_project.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;

import com.example.coen_elec_390_project.Model.User;

public class DatabaseHelper extends SQLiteOpenHelper {
    private Context context;
    private static final String TAG = "DatabaseHelper";

    public DatabaseHelper(Context context) {
        super(context, Config.DATABASE_NAME, null, Config.DATABASE_VERSION);
        this.context = context;
    }

    /**Called when the database is first created*/
    @Override
    public void onCreate(SQLiteDatabase db) {
        /**Query updating the user table*/
        String CREATE_TABLE_USER = "CREATE TABLE " + Config.USER_TABLE_NAME
                + " (" + Config.COLUMN_USER_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_USER_FULLNAME + " TEXT NOT NULL,"
                + Config.COLUMN_USER_EMAIL + " TEXT NOT NULL,"
                + Config.COLUMN_USER_PASSWORD + " TEXT NOT NULL)";

        Log.d(TAG, CREATE_TABLE_USER);

        /**Execute the SQL query*/
        db.execSQL(CREATE_TABLE_USER);

        Log.d(TAG, "User database created");
    }

    public long insertUser(User user) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /**We put the value from the user into the database*/
        contentValues.put(Config.COLUMN_USER_FULLNAME, user.getFullname());
        contentValues.put(Config.COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(Config.COLUMN_USER_PASSWORD, user.getPassword());

        /**We try to insert it*/
        try {
            id = db.insertOrThrow(Config.USER_TABLE_NAME, null, contentValues);
        }

        catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }

        /**We close the database*/
        finally {
            db.close();
        }

        return id;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
