package com.example.coen_elec_390_project.Database;

public class Config {
    public static final String DATABASE_NAME = "local-user-db";
    public static final int DATABASE_VERSION = 1;

    /**User table*/
    public static final String USER_TABLE_NAME = "user";
    public static final String COLUMN_USER_ID = "id";
    public static final String COLUMN_USER_FULLNAME = "fullname";
    public static final String COLUMN_USER_EMAIL = "email";
    public static final String COLUMN_USER_PASSWORD = "password";

    /**Statistic table*/
    public static final String STATISTIC_TABLE_NAME = "statistic";
    public static final String COLUMN_STATISTIC_ID = "id";
    public static final String COLUMN_STATISTIC_USER_ID = "userId";
    public static final String COLUMN_STATISTIC_DATE = "date";
    public static final String COLUMN_STATISTIC_PERF_INDEX= "perfIndex";
    public static final String COLUMN_STATISTIC_SPEED = "speed";
}
