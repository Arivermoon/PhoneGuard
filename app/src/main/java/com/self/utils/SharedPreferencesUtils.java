package com.self.utils;

import android.content.Context;

/**
 * Created by tanlang on 2016-05-09.
 */
public class SharedPreferencesUtils {

    public static String getString(Context context, String filename, String key) {
        return context.getSharedPreferences(filename, Context.MODE_PRIVATE).getString(key, "");
    }

    public static void putString(Context context, String filename, String key, String value) {
        context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit().putString(key, value).commit();
    }

    public static Boolean getBoolean(Context context, String filename, String key) {
        return context.getSharedPreferences(filename, Context.MODE_PRIVATE).getBoolean(key, false);
    }

    public static void putBoolean(Context context, String filename, String key, Boolean value) {
        context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit().putBoolean(key, value).commit();
    }

    public static int getInt(Context context, String filename, String key) {
        return context.getSharedPreferences(filename, Context.MODE_PRIVATE).getInt(key, 0);
    }

    public static void putInt(Context context, String filename, String key, int value) {
        context.getSharedPreferences(filename, Context.MODE_PRIVATE).edit().putInt(key, value).commit();
    }
}
