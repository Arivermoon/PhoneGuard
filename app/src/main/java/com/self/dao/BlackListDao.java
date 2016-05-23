package com.self.dao;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;

import com.self.database.BlackListDbHelper;
import com.self.domain.BlackListBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanlang on 2016/5/15.
 */
public class BlackListDao {

    private BlackListDbHelper dbHelper;

    public BlackListDao(Context context) {
        this.dbHelper = new BlackListDbHelper(context);
    }

    public void save(BlackListBean bean) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("phone", bean.getPhone());
        cv.put("mode", bean.getMode());
        db.insert("blacklist", null, cv);
        db.close();
    }

    public void update(BlackListBean bean) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        ContentValues cv = new ContentValues();
        cv.put("mode", bean.getMode());
        db.update("blacklist", cv, "phone = ?", new String[]{bean.getPhone()});
        db.close();
    }

    public void delete(BlackListBean bean) {
        SQLiteDatabase db = dbHelper.getWritableDatabase();
        db.delete("blacklist", "phone = ?", new String[]{bean.getPhone()});
        db.close();
    }

    public List<BlackListBean> findAll() {
        List<BlackListBean> lists = new ArrayList<>();
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("blacklist", new String[]{"phone", "mode"}, null, null, null, null, "phone");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                BlackListBean bean = new BlackListBean();
                bean.setPhone(cursor.getString(0));
                bean.setMode(cursor.getInt(1));
                lists.add(bean);
            }
            cursor.close();
        }
        db.close();
        return lists;
    }

    public BlackListBean findByPhone(String phone) {
        SQLiteDatabase db = dbHelper.getReadableDatabase();
        Cursor cursor = db.query("blacklist", new String[]{"phone", "mode"}, "phone = ?", new String[]{phone}, null, null, null);
        BlackListBean bean = null;
        if (cursor != null) {
            while (cursor.moveToNext()) {
                bean = new BlackListBean();
                bean.setPhone(cursor.getString(0));
                bean.setMode(cursor.getInt(1));
            }
            cursor.close();
        }
        db.close();
        return bean;
    }
}
