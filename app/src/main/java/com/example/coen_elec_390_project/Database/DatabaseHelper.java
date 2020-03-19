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
//                + Config.COLUMN_USER_AGE + " TEXT NOT NULL,"
//                + Config.COLUMN_USER_WEIGHT + " TEXT NOT NULL,"
//                + Config.COLUMN_USER_HEIGHT + " TEXT NOT NULL)";

        Log.d(TAG, CREATE_TABLE_USER);

        /**Execute the SQL query*/
        db.execSQL(CREATE_TABLE_USER);

        Log.d(TAG, "User database created");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed, all data will be gone
        db.execSQL("DROP TABLE IF EXISTS "+ Config.USER_TABLE_NAME);

        //recreate the table
        onCreate(db);
    }


    public long insertUser(User user) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /**We put the value from the user into the database*/
        contentValues.put(Config.COLUMN_USER_FULLNAME, user.getFullname());
        contentValues.put(Config.COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(Config.COLUMN_USER_PASSWORD, user.getPassword());
//        contentValues.put(Config.COLUMN_USER_AGE, user.getAge());
//        contentValues.put(Config.COLUMN_USER_HEIGHT, user.getHeight());
//        contentValues.put(Config.COLUMN_USER_WEIGHT, user.getWeight());

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

    /**Fucntion that returns a user*/
    public User getUser(String email) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        User user = new User();

        String query = "SELECT * FROM " + Config.USER_TABLE_NAME + " WHERE " + Config.COLUMN_USER_EMAIL + " = '" + email + "'";

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        String database_email = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL));

                        if(database_email.equals(email)) {
                            user.setId(cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_ID)));
                            user.setFullname(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_FULLNAME)));
                            user.setEmail(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL)));
                            user.setPassword(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_PASSWORD)));
//                            user.setAge(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_AGE)));
//                            user.setHeight(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_HEIGHT)));
//                            user.setWeight(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT)));

                            return user;
                        }
                    } while(cursor.moveToNext());
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

        return user;
    }

    /**Function that returns a list of all the user in the database*/
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
//                        String age = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_AGE));
//                        String height = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_HEIGHT));
//                        String weight = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT));

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

        String query = "SELECT * FROM " + Config.USER_TABLE_NAME + " WHERE " + Config.COLUMN_USER_EMAIL + " = '" + email + "'";

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        String database_email = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL));

                        if(database_email.equals(email)) {
                            return true;
                        }
                    } while(cursor.moveToNext());
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

        return false;
    }

    /**Function that validate the password*/
    public boolean checkPassword(String email, String password) {
        SQLiteDatabase db = this.getWritableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + Config.USER_TABLE_NAME + " WHERE " + Config.COLUMN_USER_EMAIL + " = '" + email + "'";

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        String database_email = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL));
                        String database_password = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_PASSWORD));

                        if(database_email.equals(email) && database_password.equals(password)) {
                            return true;
                        }
                    } while(cursor.moveToNext());
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

        return false;
    }

}
