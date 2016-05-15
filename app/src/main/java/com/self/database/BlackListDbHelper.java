package com.self.database;

import android.content.Context;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;

/**
 * Created by tanlang on 2016/5/15.
 */
public class BlackListDbHelper extends SQLiteOpenHelper {

    public BlackListDbHelper(Context context) {
        super(context, "blacklist.db", null, 1);
    }

    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL("create table blacklist(_id integer primary key autoincrement,phone text,mode integer)");
    }

    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        //版本号发生变化，执行该方法
        db.execSQL("drop table if exists blacklist");
        onCreate(db);
    }
}
