package com.example.coen_elec_390_project.Database;

public class Config {
    public static final String DATABASE_NAME = "local-user-db";
    public static final int DATABASE_VERSION = 1;

    /**User table*/
    public static final String USER_TABLE_NAME = "user";
    public static final String COLUMN_USER_ID = "_id";
    public static final String COLUMN_USER_FULLNAME = "fullname";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";
  
    public static final String COLUMN_USER_GENDER = "gender";
    public static final String COLUMN_USER_AGE = "age";
    public static final String COLUMN_USER_HEIGHT = "height";
    public static final String COLUMN_USER_WEIGHT = "weight";
    public static final String COLUMN_USER_HEIGHT_UNIT = "heightUnit";
    public static final String COLUMN_USER_WEIGHT_UNIT= "weightUnit";
    public static final String COLUMN_USER_STAT_COUNTER="stat_counter";
    public static final String COLUMN_USER_FBUID = "fbuid";


    /**Statistic table*/
    public static final String STATISTIC_TABLE_NAME = "statistic";
    public static final String COLUMN_STATISTIC_ID = "id";
    public static final String COLUMN_STATISTIC_USER_ID = "userId";
    public static final String COLUMN_STATISTIC_DATE = "date";
    public static final String COLUMN_STATISTIC_PERF_INDEX= "perfIndex";
    public static final String COLUMN_STATISTIC_SPEED = "speed";
    public static final String COLUMN_STATISTIC_CALORIES = "calories";
    public static final String COLUMN_STATISTIC_STEP_COUNTER = "step_counter";
}
