package com.ysered.savemylocation;

import android.content.Context;

import com.ysered.savemylocation.database.DataSource;
import com.ysered.savemylocation.database.SqliteDataSource;
import com.ysered.savemylocation.facebook.LoginValidator;
import com.ysered.savemylocation.facebook.SimpleFacebookLoginValidator;

public final class Injector {

    public DataSource provideDataSource(Context context) {
        return new SqliteDataSource(context);
    }

    public LoginValidator provideFacebookLoginValidator() {
        return new SimpleFacebookLoginValidator();
    }

}
