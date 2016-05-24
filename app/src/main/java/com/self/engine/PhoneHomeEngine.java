package com.self.engine;

import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * Created by tanlang on 2016/5/24.
 */
public class PhoneHomeEngine {

    public static final String PATH = "/data/data/com.self.activity/files/address.db";

    public static String queryPhomeHome(String phoneNumber, Context context) {
        String phomeHome;
        Pattern pattern = Pattern.compile("1[3578][0-9]{9}");
        Matcher matcher = pattern.matcher(phoneNumber);
        if (matcher.matches()) {
            // 是手机号
            phomeHome = mobileQuery(phoneNumber, context);
        } else if (phoneNumber.length() >= 11) {
            // 固定号码
            phomeHome = phoneQuery(phoneNumber, context);
        } else {
            // 如果是服务号码
            phomeHome = serviceNumberQuery(phoneNumber);
        }

        return phomeHome;
    }

    /**
     * 查询服务号码 如：110匪警
     *
     * @param phoneNumber
     * @return
     */
    public static String serviceNumberQuery(String phoneNumber) {
        String result = "";
        if (phoneNumber.equals("110")) {
            result = "匪警";
        } else if (phoneNumber.equals("10086")) {
            result = "中国移动";
        }
        return result;
    }

    /**
     * @param phoneNumber 电话号码全称
     * @param context
     * @return 固定电话号码归属地
     */
    public static String phoneQuery(String phoneNumber, Context context) {
        String result = phoneNumber;
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        String code;
        if (phoneNumber.charAt(1) == '1' || phoneNumber.charAt(1) == '2') {
            // 2位区号
            code = phoneNumber.substring(1, 3);
        } else {
            // 3位区号
            code = phoneNumber.substring(1, 4);
        }

        Cursor cursor = database.rawQuery("select location from data2 where area = ?", new String[]{code});
        if (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        return result;
    }

    /**
     * @param phoneNumber 电话号码全称
     * @param context
     * @return 手机号码归属地
     */
    public static String mobileQuery(String phoneNumber, Context context) {
        String result = phoneNumber;
        SQLiteDatabase database = SQLiteDatabase.openDatabase(PATH, null, SQLiteDatabase.OPEN_READONLY);
        String sql = "select location from data2 where id = (select outKey from data1 where id = ?)";
        Cursor cursor = database.rawQuery(sql, new String[]{phoneNumber.substring(0, 7)});
        if (cursor.moveToNext()) {
            result = cursor.getString(0);
        }
        return result;
    }
}
