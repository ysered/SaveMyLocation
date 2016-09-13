package com.ysered.savemylocation;

import android.content.Context;

import com.ysered.savemylocation.database.DataSource;
import com.ysered.savemylocation.database.SqliteDataSource;
import com.ysered.savemylocation.facebook.LoginValidator;
import com.ysered.savemylocation.facebook.SimpleFacebookLoginValidator;

public final class Injector {

    public static DataSource provideDataSource(Context context) {
        return new SqliteDataSource(context);
    }

    public static LoginValidator provideLoginValidator() {
        return new SimpleFacebookLoginValidator();
    }

}
