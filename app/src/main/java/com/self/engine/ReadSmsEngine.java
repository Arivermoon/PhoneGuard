package com.self.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.self.constant.Constant;
import com.self.domain.ContactBean;
import com.self.util.EncryptUtils;
import com.self.util.SpUtils;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.TreeMap;

/**
 * Created by tanlang on 2016/5/10.
 */
public class ReadSmsEngine {

    public static final Uri SMS_URI = Uri.parse("content://sms/inbox");

    /**
     * 根据手机号获取收件箱的短信
     * 用于监听短信
     *
     * @param context
     * @return
     */
    public static ContactBean getSms(Context context) {
        ContactBean bean = new ContactBean();
        String phone = EncryptUtils.decrypt(SpUtils.getString(context, Constant.PHONE));
        String selection = "read = 0 and address = " + phone + " date > " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address", "body"}, selection, null, "date desc limit 1");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                bean.setPhone(cursor.getString(0));
                bean.setContent(cursor.getString(1));
            }
            cursor.close();
        }

        return bean;
    }

    /**
     * 获取所有短信的号码
     *
     * @param context
     * @return
     */
    public static List<ContactBean> getAllSms(Context context) {
        Map<String, ContactBean> map = new TreeMap<>();
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address", "person"}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactBean bean = new ContactBean();
                String phone = cursor.getString(0);
                bean.setPhone(phone);
                bean.setName(cursor.getString(1));
                map.put(phone, bean);
            }
            cursor.close();
        }

        return new ArrayList<>(map.values());
    }
}
