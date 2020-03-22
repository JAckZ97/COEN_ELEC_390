package com.example.coen_elec_390_project.Database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;


import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.User;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
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

        /**Query updating the statistic table*/
        String CREATE_TABLE_STATISTIC = "CREATE TABLE " + Config.STATISTIC_TABLE_NAME
                + " (" + Config.COLUMN_STATISTIC_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, "
                + Config.COLUMN_STATISTIC_USER_ID + " INTEGER,"
                + Config.COLUMN_STATISTIC_DATE + " TEXT NOT NULL,"
                + Config.COLUMN_STATISTIC_PERF_INDEX + " INTEGER,"
                + Config.COLUMN_STATISTIC_SPEED + " REAL)";

        Log.d(TAG, CREATE_TABLE_STATISTIC);

        /**Execute the SQL query*/
       db.execSQL(CREATE_TABLE_STATISTIC);

        Log.d(TAG, "Statistic database created");
    }

    /**Function that adds a user into the user database*/
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

    /**Function that returns a user*/
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

    /**Function that adds a statistic into the statistic database*/
    public long insertStatistic(Statistic statistic) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /**We put the value from the user into the database*/
        contentValues.put(Config.COLUMN_STATISTIC_USER_ID, statistic.getUser_id());
        contentValues.put(Config.COLUMN_STATISTIC_DATE, statistic.getDate());
        contentValues.put(Config.COLUMN_STATISTIC_PERF_INDEX, statistic.getPerformance_index());
        contentValues.put(Config.COLUMN_STATISTIC_SPEED, statistic.getSpeed());

        /**We try to insert it*/
        try {
            id = db.insertOrThrow(Config.STATISTIC_TABLE_NAME, null, contentValues);
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

    public List<Statistic> getStatisticsByUser(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + Config.STATISTIC_TABLE_NAME + " WHERE " + Config.COLUMN_STATISTIC_USER_ID + " = " + userId;

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    List<Statistic> statistics = new ArrayList<>();

                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_ID));
                        int user_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_USER_ID));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));
                        int perf_index = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_PERF_INDEX));
                        double speed = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_SPEED));

                        statistics.add(new Statistic(id, user_id, date, perf_index, speed));
                    } while(cursor.moveToNext());

                    return statistics;
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

    public List<Statistic> getStatisticsAfterStart(int userId, String startdate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;
        SimpleDateFormat sdformat = new SimpleDateFormat("yyyy-MM-dd");
        Date startDate;

        String query = "SELECT * FROM " + Config.STATISTIC_TABLE_NAME + " WHERE "/* + Config.COLUMN_STATISTIC_USER_ID + " = " + userId
                + " AND "*/ + Config.COLUMN_STATISTIC_DATE + " > " + startdate;

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    List<Statistic> statistics = new ArrayList<>();

                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_ID));
                        int user_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_USER_ID));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));
                        int perf_index = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_PERF_INDEX));
                        double speed = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_SPEED));


                        statistics.add(new Statistic(id, user_id, date, perf_index, speed));
                    } while(cursor.moveToNext());

                    return statistics;
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



    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

    }
}
