package com.ysered.savemylocation.database;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

import com.google.android.gms.maps.model.LatLng;
import com.ysered.savemylocation.database.PersistenceContract.LocationEntry;
import com.ysered.savemylocation.database.PersistenceContract.UserEntry;

import java.util.ArrayList;
import java.util.List;

public class SqliteDataSource implements DataSource {

    private final SQLiteOpenHelper mHelper;

    public SqliteDataSource(Context context) {
        mHelper = new DatabaseHelper(context);
    }

    @Override
    public void saveUser(String email) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        final ContentValues values = new ContentValues();
        values.put(UserEntry.COL_EMAIL, email);
        db.insertWithOnConflict(UserEntry.TABLE_NAME, null, values, SQLiteDatabase.CONFLICT_IGNORE);
    }

    @Override
    public List<LatLng> getLocations(String user) {
        final String[] columns = {LocationEntry.COL_LAT, LocationEntry.COL_LON};
        final String where = LocationEntry.COL_USER + " = ?";
        final String[] whereArgs = {user};

        final Cursor cursor = mHelper.getReadableDatabase().query(LocationEntry.TABLE_NAME,
                columns, where, whereArgs, null, null, null);

        List<LatLng> locations = new ArrayList<>(cursor.getCount());
        if (cursor.moveToFirst()) {
            double longitude;
            double latitude;
            while (!cursor.isAfterLast()) {
                latitude = cursor.getDouble(cursor.getColumnIndex(LocationEntry.COL_LAT));
                longitude = cursor.getDouble(cursor.getColumnIndex(LocationEntry.COL_LON));
                locations.add(new LatLng(latitude, longitude));
                cursor.moveToNext();
            }
            cursor.close();
        }
        return locations;
    }

    @Override
    public void overrideLocations(String user, List<LatLng> locations) {
        final SQLiteDatabase db = mHelper.getWritableDatabase();
        try {
            db.beginTransaction();
            db.delete(LocationEntry.TABLE_NAME, LocationEntry.COL_USER + " = ?",
                    new String[] {user});
            for (LatLng latLng : locations) {
                final ContentValues values = new ContentValues();
                values.put(LocationEntry.COL_USER, user);
                values.put(LocationEntry.COL_LAT, latLng.latitude);
                values.put(LocationEntry.COL_LON, latLng.longitude);
                db.insert(LocationEntry.TABLE_NAME, null, values);
            }
            db.setTransactionSuccessful();
        } finally {
            db.endTransaction();
        }
    }

}
