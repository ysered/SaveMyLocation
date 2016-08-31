package com.ysered.savemylocation.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.ysered.savemylocation.database.PersistenceContract.LocationEntry;
import com.ysered.savemylocation.database.PersistenceContract.UserEntry;

public class DatabaseHelper extends SQLiteOpenHelper {

    private static final String DATABASE_NAME = "save_my_location.db";
    private static final int DATABASE_VERSION = 1;

    public DatabaseHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("CREATE TABLE " + UserEntry.TABLE_NAME + "(" +
                UserEntry.COL_EMAIL + " TEXT PRIMARY KEY)");

        db.execSQL("CREATE TABLE " + LocationEntry.TABLE_NAME + "(" +
                LocationEntry.COL_USER + " TEXT, " +
                LocationEntry.COL_LAT + " TEXT, " +
                LocationEntry.COL_LON + " TEXT, " +
                "PRIMARY KEY(" + LocationEntry.COL_LAT + ", " + LocationEntry.COL_LON + "), " +
                "FOREIGN KEY(" + LocationEntry.COL_USER + ") " +
                "REFERENCES " + UserEntry.TABLE_NAME + "(" + UserEntry.COL_EMAIL + ")" +
                ")");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        if (oldVersion != newVersion) {
            db.execSQL("DROP TABLE IF EXISTS location");
            db.execSQL("DROP TABLE IF EXISTS user");
            onCreate(db);
        }
    }

}
