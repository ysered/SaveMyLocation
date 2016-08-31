package com.ysered.savemylocation.database;

public interface PersistenceContract {

    final class UserEntry {
        public static final String TABLE_NAME = "user";
        public static final String COL_EMAIL = "email";
    }

    final class LocationEntry {
        public static final String TABLE_NAME = "location";
        public static final String COL_USER = "user";
        public static final String COL_LAT = "lat";
        public static final String COL_LON = "lon";
    }

}
