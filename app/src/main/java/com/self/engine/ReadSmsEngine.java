package com.self.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.self.constant.Constant;
import com.self.domain.ContactBean;
import com.self.util.EncryptUtils;
import com.self.util.SpUtils;

import java.util.Set;
import java.util.TreeSet;

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
        ContactBean contact = new ContactBean();
        String phone = EncryptUtils.decrypt(SpUtils.getString(context, Constant.PHONE));
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address", "body"}, "address = ?", new String[]{phone}, "date desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String body = cursor.getString(1);
                contact.setPhone(cursor.getString(0));
                contact.setContent(body);
                break;
            }
            cursor.close();
        }

        return contact;
    }

    /**
     * 获取所有短信的号码
     *
     * @param context
     * @return
     */
    public static Set<ContactBean> getAllSms(Context context) {
        Set<ContactBean> sets = new TreeSet<>();
        ContactBean contact = new ContactBean();
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address"}, null, null, null);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contact.setPhone(cursor.getString(0));
                sets.add(contact);
            }
            cursor.close();
        }

        return sets;
    }
}
