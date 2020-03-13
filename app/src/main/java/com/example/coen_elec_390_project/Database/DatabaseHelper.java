package com.example.coen_elec_390_project.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.os.strictmode.SqliteObjectLeakedViolation;
import android.util.Log;
import android.widget.Toast;

import androidx.constraintlayout.solver.widgets.ConstraintWidgetGroup;

import com.example.coen_elec_390_project.Model.User;

import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

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

    /**Function that returns all the user in the database*/
    public List<User> getAllUsers() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Config.USER_TABLE_NAME, null, null, null, null, null, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    List<User> users = new ArrayList<>();

                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_ID));
                        String fullname = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_FULLNAME));
                        String email = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL));
                        String password = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_PASSWORD));

                        users.add(new User(id, fullname, email, password));
                    } while(cursor.moveToNext());

                    return users;
                }
            }
        }

        catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }

        finally {
            if(cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return Collections.emptyList();
    }

    /**Function that checks if a user is already register in the database*/
    public boolean checkIfExisting(String email) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;
        boolean output = false;
        String query = "SELECT * FROM " + Config.USER_TABLE_NAME + " WHERE " + Config.COLUMN_USER_EMAIL + " = '" + email + "'";

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);
            Log.d(TAG, "try");
            if(cursor != null) {
                Log.d(TAG, "if");
                do {
                    if(cursor.moveToFirst()) {
                    }
                    Log.d(TAG, "do");
                    String database_email = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL));
                    Log.d(TAG, "database " + database_email);
                    Log.d(TAG, "email " + email);

                    if(database_email.equals(email)) {
                        output = true;
                    }
                } while(cursor.moveToNext());
            }
        }

        catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }

        finally {
            if(cursor != null) {
                cursor.close();
            }

            Log.d(TAG, String.valueOf(output));
            db.close();
        }


        return output;
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
