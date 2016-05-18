package com.self.util;

import android.content.Context;

import com.self.constant.Constant;

/**
 * Created by tanlang on 2016-05-09.
 */
public class SpUtils {

    public static String getString(Context context, String name) {
        return context.getSharedPreferences(Constant.FILENAME, Context.MODE_PRIVATE).getString(name, "");
    }

    public static void putString(Context context, String name, String value) {
        context.getSharedPreferences(Constant.FILENAME, Context.MODE_PRIVATE).edit().putString(name, value).commit();
    }

    public static Boolean getBoolean(Context context, String name) {
        return context.getSharedPreferences(Constant.FILENAME, Context.MODE_PRIVATE).getBoolean(name, false);
    }

    public static void putBoolean(Context context, String name, Boolean value) {
        context.getSharedPreferences(Constant.FILENAME, Context.MODE_PRIVATE).edit().putBoolean(name, value).commit();
    }

    public static int getInt(Context context, String name) {
        return context.getSharedPreferences(Constant.FILENAME, Context.MODE_PRIVATE).getInt(name, 0);
    }

    public static void putInt(Context context, String name, int value) {
        context.getSharedPreferences(Constant.FILENAME, Context.MODE_PRIVATE).edit().putInt(name, value).commit();
    }
}
