package com.ysered.savemylocation.utils;

import android.content.Context;
import android.content.SharedPreferences;
import android.preference.PreferenceManager;

import com.ysered.savemylocation.Constants;

public class PreferenceUtils {

    public static void saveCurrentUser(Context context, String user) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREF_CURRENT_USER, user);
        editor.apply();
    }

    public static String getCurrentUser(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        return prefs.getString(Constants.PREF_CURRENT_USER, null);
    }

    public static void clearCurrentUser(Context context) {
        final SharedPreferences prefs = PreferenceManager.getDefaultSharedPreferences(context);
        final SharedPreferences.Editor editor = prefs.edit();
        editor.putString(Constants.PREF_CURRENT_USER, null);
        editor.apply();
    }

}
