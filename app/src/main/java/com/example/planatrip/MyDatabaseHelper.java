package com.example.planatrip;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

public class MyDatabaseHelper extends SQLiteOpenHelper {
    private static final String DATABASE_NAME = "mydatabase.db";
    private static final int DATABASE_VERSION = 1;

    public static final String TABLE_NAME = "coordinates";
    public static final String TABLE_NAME2= "CURRENT_TRIP";
    public static final String TABLE_NAME3= "BUDGET";
    public static final String COLUMN_ID = "_id";
    public static final String COLUMN_LATITUDE_FROM = "latitude_from";
    public static final String COLUMN_LONGITUDE_FROM = "longitude_from";
    public static final String COLUMN_LATITUDE_TO = "latitude_to";
    public static final String COLUMN_LONGITUDE_TO = "longitude_to";
    public static final String COLUMN_SEARCH_STRING = "search_string";
    public static final String COLUMN_NAMEOFTRIP_STRING = "nameoftrip_string";
    public static final String COLUMN_CURRENTTRIP_ID = "currenttrip_id";
    public static final String COLUMN_EXPENSE_STRING = "expense_string";
    public static final String COLUMN_EXPENSE_VALUE = "expense_value";
    public static final String COLUMN_TRIP_ID = "trip_id";

    public static final String COLUMN_CURRENTTRIP_STRING = "currenttrip_string";

    private static final String CREATE_TABLE =
            "CREATE TABLE " + TABLE_NAME + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_LATITUDE_FROM + " REAL NOT NULL, " +
                    COLUMN_LONGITUDE_FROM + " REAL NOT NULL," +
                    COLUMN_LATITUDE_TO + " REAL NOT NULL, " +
                    COLUMN_SEARCH_STRING + " TEXT NOT NULL, " +
                    COLUMN_LONGITUDE_TO + " REAL NOT NULL," +
                    COLUMN_NAMEOFTRIP_STRING + " TEXT NOT NULL)";


    private static final String CREATE_TABLE2 =
            "CREATE TABLE " + TABLE_NAME2 + " (" +
            COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_CURRENTTRIP_ID + " INTEGER)";

    private static final String CREATE_TABLE3 =
            "CREATE TABLE " + TABLE_NAME3 + " (" +
                    COLUMN_ID + " INTEGER PRIMARY KEY AUTOINCREMENT, " +
                    COLUMN_EXPENSE_STRING + " TEXT," +
                    COLUMN_EXPENSE_VALUE + " REAL," +
                    COLUMN_TRIP_ID + " INTEGER)";


    public MyDatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE);
        db.execSQL(CREATE_TABLE2);
        db.execSQL(CREATE_TABLE3);
        db.execSQL("INSERT INTO CURRENT_TRIP VALUES (0,null)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        // Implement database upgrade logic here, if needed
    }
}