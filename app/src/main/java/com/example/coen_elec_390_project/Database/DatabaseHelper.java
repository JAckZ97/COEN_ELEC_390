package com.example.coen_elec_390_project.Database;

import android.content.ContentValues;
import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteException;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;
import android.widget.Toast;
import android.content.Intent;
import androidx.constraintlayout.solver.widgets.ConstraintWidgetGroup;

import com.example.coen_elec_390_project.Model.Statistic;
import com.example.coen_elec_390_project.Model.User;

import java.text.ParseException;
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
                + Config.COLUMN_USER_PASSWORD + " TEXT NOT NULL,"
                + Config.COLUMN_USER_GENDER + " TEXT,"
                + Config.COLUMN_USER_AGE + " TEXT,"
                + Config.COLUMN_USER_WEIGHT + " TEXT,"
                + Config.COLUMN_USER_HEIGHT + " TEXT,"
                + Config.COLUMN_USER_HEIGHT_UNIT + " INTEGER,"
                + Config.COLUMN_USER_WEIGHT_UNIT + " INTEGER,"
                + Config.COLUMN_USER_STAT_COUNTER + " INTEGER,"
                + Config.COLUMN_USER_FBUID + " TEXT )";

        Log.d(TAG, CREATE_TABLE_USER);

        /**Execute the SQL query*/
        db.execSQL(CREATE_TABLE_USER);

        Log.d(TAG, "User database created");

        /**Query updating the statistic table*/
        String CREATE_TABLE_STATISTIC = "CREATE TABLE " + Config.STATISTIC_TABLE_NAME
                + " (" + Config.COLUMN_STATISTIC_ID + " INTEGER PRIMARY KEY, "
                + Config.COLUMN_STATISTIC_USER_ID + " INTEGER,"
                + Config.COLUMN_STATISTIC_DATE + " TEXT NOT NULL,"
                + Config.COLUMN_STATISTIC_PERF_INDEX + " REAL,"
                + Config.COLUMN_STATISTIC_SPEED + " REAL,"
                + Config.COLUMN_STATISTIC_CALORIES + " REAL,"
                + Config.COLUMN_STATISTIC_STEP_COUNTER + " INTEGER)";

        Log.d(TAG, CREATE_TABLE_STATISTIC);

        /**Execute the SQL query*/
        db.execSQL(CREATE_TABLE_STATISTIC);

        Log.d(TAG, "<DB> Statistic database created");
    }
  
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {

        // Drop older table if existed, all data will be gone
        db.execSQL("DROP TABLE IF EXISTS "+ Config.USER_TABLE_NAME);
        db.execSQL("DROP TABLE IF EXISTS "+ Config.STATISTIC_TABLE_NAME);
        //recreate the table
        onCreate(db);
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
        contentValues.put(Config.COLUMN_USER_GENDER,"Other");
        contentValues.putNull(Config.COLUMN_USER_AGE);
        contentValues.putNull(Config.COLUMN_USER_WEIGHT);
        contentValues.putNull(Config.COLUMN_USER_HEIGHT);
        contentValues.put(Config.COLUMN_USER_HEIGHT_UNIT, 1);
        contentValues.put(Config.COLUMN_USER_WEIGHT_UNIT, 1);
        contentValues.put(Config.COLUMN_USER_STAT_COUNTER, 0);
        contentValues.putNull(Config.COLUMN_USER_FBUID);

        /**We try to insert it*/
        try {
            id = db.insertOrThrow(Config.USER_TABLE_NAME, null, contentValues);
        }

        catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
            Log.e("Tag","<LOGIN> exception here");
        }

        /**We close the database*/
        finally {
            db.close();
        }
        Log.e("Tag","<LOGIN> finished here");
        return id;
    }

    public long insertOldUser(User user) {
        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /**We put the value from the user into the database*/

        Log.e("Tag","<LOGIN> gender->"+user.getGender()+" age->"+user.getAge()+" weight->"+user.getWeight()+" height->"+user.getHeight());

        contentValues.put(Config.COLUMN_USER_FULLNAME, user.getFullname());
        contentValues.put(Config.COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(Config.COLUMN_USER_PASSWORD, user.getPassword());
        contentValues.put(Config.COLUMN_USER_GENDER,user.getGender());
        contentValues.put(Config.COLUMN_USER_AGE,user.getAge());
        contentValues.put(Config.COLUMN_USER_WEIGHT,user.getWeight());
        contentValues.put(Config.COLUMN_USER_HEIGHT,user.getHeight());
        contentValues.put(Config.COLUMN_USER_HEIGHT_UNIT, user.getHeightUnit());
        contentValues.put(Config.COLUMN_USER_WEIGHT_UNIT, user.getWeightUnit());
        contentValues.put(Config.COLUMN_USER_STAT_COUNTER, user.getStat_counter());
        contentValues.put(Config.COLUMN_USER_FBUID,user.getFbuid());

        /**We try to insert it*/
        try {
            id = db.insertOrThrow(Config.USER_TABLE_NAME, null, contentValues);
        }

        catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
            Log.e("Tag","<LOGIN> exception here");
        }

        /**We close the database*/
        finally {
            db.close();
        }
        return id;
    }

    public long updateProfile(User user) {
        long id = -1;
//        long id2 = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /**We put the value from the user into the database*/


        contentValues.put(Config.COLUMN_USER_FULLNAME, user.getFullname());
        contentValues.put(Config.COLUMN_USER_EMAIL, user.getEmail());
        contentValues.put(Config.COLUMN_USER_PASSWORD, user.getPassword());
        contentValues.put(Config.COLUMN_USER_GENDER, user.getGender());
        contentValues.put(Config.COLUMN_USER_AGE, user.getAge());
        contentValues.put(Config.COLUMN_USER_WEIGHT, user.getWeight());
        contentValues.put(Config.COLUMN_USER_HEIGHT, user.getHeight());
        contentValues.put(Config.COLUMN_USER_HEIGHT_UNIT, user.getHeightUnit());
        contentValues.put(Config.COLUMN_USER_WEIGHT_UNIT, user.getWeightUnit());
        contentValues.put(Config.COLUMN_USER_STAT_COUNTER, user.getStat_counter());
        contentValues.put(Config.COLUMN_USER_FBUID,user.getFbuid());

        // TODO: FIX UPDATE METHOD:
        //  https://developer.android.com/reference/android/database/sqlite/SQLiteDatabase#update
        //  (java.lang.String,%20android.content.ContentValues,%20java.lang.String,%20java.lang.String%5B%5D)

        String[] args = {String.valueOf(user.getId())};
        /**We try to update it*/
        try {
//            not works, don't know why
//            id = db.update(Config.USER_TABLE_NAME, contentValues, "email=?",args);
//            it will update the all user in database when "whereClause" set to null
            db.beginTransaction();
            id = db.update(Config.USER_TABLE_NAME, contentValues, Config.COLUMN_USER_ID+" LIKE ?",args);
            db.setTransactionSuccessful();
            db.endTransaction();
        }

        catch (SQLiteException e) {
            Log.d(TAG, "<DB> Exception: " + e);
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
        User user = null;

        String query = "SELECT * FROM " + Config.USER_TABLE_NAME + " WHERE " + Config.COLUMN_USER_EMAIL + " = '" + email + "'";

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);
            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    do {
                        user = new User();
                        String database_email = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL));
                        Log.e("Tag","<DB> email "+database_email);
                        if(database_email.equals(email)) {
                            user.setId(cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_ID)));
                            user.setFullname(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_FULLNAME)));
                            user.setEmail(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_EMAIL)));
                            user.setPassword(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_PASSWORD)));
                            user.setGender(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_GENDER)));
                            user.setAge(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_AGE)));
                            user.setWeight(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT)));
                            user.setHeight(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_HEIGHT)));
                            user.setHeightUnit(cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_HEIGHT_UNIT)));
                            user.setWeightUnit(cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT_UNIT)));
                            user.setStat_counter(cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_STAT_COUNTER)));
                            user.setFbuid(cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_FBUID)));
                            return user;
                        }
                    } while(cursor.moveToNext());
                }
            }
        }

        catch (SQLiteException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
            if(cursor != null) {
                cursor.close();
            }

            db.close();
            return null;
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
                        String gender = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_GENDER));
                        String age = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_AGE));
                        String weight = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT));
                        String height = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_HEIGHT));
                        int heightUnit = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_HEIGHT_UNIT));
                        int weightUnit = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_WEIGHT_UNIT));
                        int stat_counter = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_USER_STAT_COUNTER));
                        String fbuid = cursor.getString(cursor.getColumnIndex(Config.COLUMN_USER_FBUID));
                        //public User(int id, String fullname, String email, String password, String age, String weight, String height, String gender, int heightUnit, int weightUnit,int stat_counter,String fbuid) {
                        users.add(new User(id, fullname, email, password, age, weight, height, gender, heightUnit, weightUnit,stat_counter,fbuid));
//                        users.add(new User(id, fullname, email, password, gender, age, height, weight));
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

    /**Function that returns a list of all statistic in the local database*/
    public List<Statistic> getAllStats() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        try {
            cursor = db.query(Config.STATISTIC_TABLE_NAME, null, null, null, null, null, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    List<Statistic> statistics = new ArrayList<>();

                    do {
                        /*
                            public Statistic(Integer user_id, String date, Double performance_index, Double speed,Double calories,Integer step_counter) {}
                            public Statistic(Integer id, Integer user_id, String date, Double performance_index, Double speed,Double calories, Integer step_counter)
                         */
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_ID));
                        int user_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_USER_ID));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));
                        double perf_index = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_PERF_INDEX));
                        double speed = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_SPEED));
                        double calories = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_CALORIES));
                        int step_counter = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_STEP_COUNTER));

                        statistics.add(new Statistic(id, user_id, date, perf_index, speed,calories,step_counter));
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
    public long insertStatistic(Statistic statistic,User user) {

        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues = new ContentValues();

        /**We put the value from the user into the database*/
        contentValues.put(Config.COLUMN_STATISTIC_ID, user.getStat_counter());
        user.setStat_counter(user.getStat_counter()+1);
        contentValues.put(Config.COLUMN_STATISTIC_USER_ID, statistic.getUser_id());
        contentValues.put(Config.COLUMN_STATISTIC_DATE, statistic.getDate());
        contentValues.put(Config.COLUMN_STATISTIC_PERF_INDEX, statistic.getPerformance_index());
        contentValues.put(Config.COLUMN_STATISTIC_SPEED, statistic.getSpeed());
        contentValues.put(Config.COLUMN_STATISTIC_CALORIES,statistic.getCalories());

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

        updateProfile(user);
        return id;
    }

    public long UpdateStatistic(List<Statistic> mylist) {

        long id = -1;
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues contentValues;
        for(int i = 0;i<mylist.size();i++){
            contentValues= new ContentValues();
            /**We put the value from the user into the database*/
            contentValues.put(Config.COLUMN_STATISTIC_ID, mylist.get(i).getId());
            contentValues.put(Config.COLUMN_STATISTIC_USER_ID, mylist.get(i).getUser_id());
            contentValues.put(Config.COLUMN_STATISTIC_DATE, mylist.get(i).getDate());
            contentValues.put(Config.COLUMN_STATISTIC_PERF_INDEX, mylist.get(i).getPerformance_index());
            contentValues.put(Config.COLUMN_STATISTIC_SPEED, mylist.get(i).getSpeed());
            contentValues.put(Config.COLUMN_STATISTIC_CALORIES,mylist.get(i).getCalories());
            contentValues.put(Config.COLUMN_STATISTIC_STEP_COUNTER,mylist.get(i).getStep_counter());

            /**We try to insert it*/
            try {
                id = db.insertOrThrow(Config.STATISTIC_TABLE_NAME, null, contentValues);
            }

            catch (SQLiteException e) {
                Log.d(TAG, "Exception: " + e);
                Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
            }
        }
        db.close();
        return id;
    }

    /**Function that returns all the statistics of a given user*/
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

                        /*
                        + " (" + Config.COLUMN_STATISTIC_ID + " INTEGER PRIMARY KEY, "
                + Config.COLUMN_STATISTIC_USER_ID + " INTEGER,"
                + Config.COLUMN_STATISTIC_DATE + " TEXT NOT NULL,"
                + Config.COLUMN_STATISTIC_PERF_INDEX + " REAL,"
                + Config.COLUMN_STATISTIC_SPEED + " REAL,"
                + Config.COLUMN_STATISTIC_CALORIES + " REAL,"
                + Config.COLUMN_STATISTIC_STEP_COUNTER + " INTEGER)";
                         */
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_ID));
                        int user_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_USER_ID));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));
                        double perf_index = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_PERF_INDEX));
                        double speed = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_SPEED));
                        double calories = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_CALORIES));
                        int step_counter = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_STEP_COUNTER));

                        //Statistic(Integer id, Integer user_id, String date, Double performance_index, Double speed,Double calories, Integer step_counter) {
                        statistics.add(new Statistic(id, user_id, date, perf_index, speed,calories,step_counter));
                    } while(cursor.moveToNext());

                    Log.e("Tag","<DB> get stats");
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

    /**Function that compares two dates*/
    public int compareDate(String d1, String d2) throws ParseException {
        SimpleDateFormat format = new SimpleDateFormat("yyyy/MM/dd");

        Date date1 = format.parse(d1);
        Date date2 = format.parse(d2);

        if(date1.after(date2)) {
            return -1;
        }

        else if(date1.before(date2)) {
            return 1;
        }

        else {
            return 0;
        }
    }

    /**Function that finds the max date in the statistic database*/
    public String getMaxDate(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + Config.STATISTIC_TABLE_NAME + " WHERE " + Config.COLUMN_STATISTIC_USER_ID + " = " + userId;

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    String maxdate = "2020/01/01";

                    do {
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));

                        int after = compareDate(date, maxdate);
                        Log.d(TAG, "maxdate:" + maxdate);
                        Log.d(TAG, "date: " + date);
                        Log.d(TAG, "after: " + after);
                        if(after == -1) {
                            maxdate = date;
                        }

                    } while(cursor.moveToNext());
                    Log.d(TAG, "final maxdate:" + maxdate);
                    return maxdate;
                }
            }
        }

        catch (SQLiteException | ParseException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }

        finally {
            if(cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return "";
    }

    /**Function that finds the max date in the statistic database*/
    public String getMinDate(int userId) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + Config.STATISTIC_TABLE_NAME + " WHERE " + Config.COLUMN_STATISTIC_USER_ID + " = " + userId;

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    String mindate = "2020/12/31";

                    do {
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));

                        int before = compareDate(date, mindate);
                        Log.d(TAG, "mindate:" + mindate);
                        Log.d(TAG, "date: " + date);
                        Log.d(TAG, "after: " + before);
                        if(before == 1) {
                            mindate = date;
                        }

                    } while(cursor.moveToNext());
                    Log.d(TAG, "final mindate:" + mindate);
                    return mindate;
                }
            }
        }

        catch (SQLiteException | ParseException e) {
            Log.d(TAG, "Exception: " + e);
            Toast.makeText(context, "Operation failed: " + e, Toast.LENGTH_LONG).show();
        }

        finally {
            if(cursor != null) {
                cursor.close();
            }

            db.close();
        }

        return "";
    }

    /**Function that returns all the statistics of a given user after a given date*/
    public List<Statistic> getStatisticsAfterStartDate(int userId, String startdate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + Config.STATISTIC_TABLE_NAME + " WHERE " + Config.COLUMN_STATISTIC_USER_ID + " = " + userId;

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    List<Statistic> statistics = new ArrayList<>();

                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_ID));
                        int user_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_USER_ID));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));
                        Log.d(TAG, "date: " + date);
                        int after = compareDate(date, startdate);
                        Log.d(TAG, "after: " + after);
                        double perf_index = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_PERF_INDEX));
                        double speed = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_SPEED));
                        double calories = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_CALORIES));
                        int step_counter = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_STEP_COUNTER));
                        if(after == -1 || after == 0) {
                            statistics.add(new Statistic(id, user_id, date, perf_index, speed,calories,step_counter));
                        }
                    } while(cursor.moveToNext());

                    return statistics;
                }
            }
        }

        catch (SQLiteException | ParseException e) {
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

    /**Function that returns all the statistics of a given user before a given date*/
    public List<Statistic> getStatisticsBeforeEndDate(int userId, String enddate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + Config.STATISTIC_TABLE_NAME + " WHERE " + Config.COLUMN_STATISTIC_USER_ID + " = " + userId;

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    List<Statistic> statistics = new ArrayList<>();

                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_ID));
                        int user_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_USER_ID));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));
                        Log.d(TAG, "date: " + date);
                        int before = compareDate(date, enddate);
                        Log.d(TAG, "before: " + before);
                        double perf_index = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_PERF_INDEX));
                        double speed = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_SPEED));
                        double calories = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_CALORIES));
                        int step_counter = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_STEP_COUNTER));
                        if(before == 1 || before == 0) {
                            statistics.add(new Statistic(id, user_id, date, perf_index, speed,calories,step_counter));
                        }
                    } while(cursor.moveToNext());

                    return statistics;
                }
            }
        }

        catch (SQLiteException | ParseException e) {
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

    /**Function that returns all the statistics of a given user between a given start date and end date*/
    public List<Statistic> getStatisticsBetweenStartAndEndDates(int userId, String startdate, String enddate) {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor cursor = null;

        String query = "SELECT * FROM " + Config.STATISTIC_TABLE_NAME + " WHERE " + Config.COLUMN_STATISTIC_USER_ID + " = " + userId;

        Log.d(TAG, query);

        try {
            cursor = db.rawQuery(query, null);

            if(cursor != null) {
                if(cursor.moveToFirst()) {
                    List<Statistic> statistics = new ArrayList<>();

                    do {
                        int id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_ID));
                        int user_id = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_USER_ID));
                        String date = cursor.getString(cursor.getColumnIndex(Config.COLUMN_STATISTIC_DATE));
                        Log.d(TAG, "date: " + date);
                        int before = compareDate(date, enddate);
                        int after = compareDate(date, startdate);
                        Log.d(TAG, "before: " + before);
                        Log.d(TAG, "after: " + after);
                        double perf_index = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_PERF_INDEX));
                        double speed = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_SPEED));
                        double calories = cursor.getDouble(cursor.getColumnIndex(Config.COLUMN_STATISTIC_CALORIES));
                        int step_counter = cursor.getInt(cursor.getColumnIndex(Config.COLUMN_STATISTIC_STEP_COUNTER));
                        if((after == -1 || after == 0) && (before == 1 || before == 0)) {
                            statistics.add(new Statistic(id, user_id, date, perf_index, speed,calories,step_counter));
                        }
                    } while(cursor.moveToNext());

                    return statistics;
                }
            }
        }

        catch (SQLiteException | ParseException e) {
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

    public void clearDatabase() {
        SQLiteDatabase db = this.getReadableDatabase();
        String clearUser = "DELETE FROM " + Config.USER_TABLE_NAME;
        String clearStats = "DELETE FROM " + Config.STATISTIC_TABLE_NAME;
        db.execSQL(clearUser);
        db.execSQL(clearStats);
    }

}
