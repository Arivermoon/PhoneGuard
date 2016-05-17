package com.self.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.self.domain.ContactBean;
import com.self.utils.Constant;
import com.self.utils.EncryptUtils;
import com.self.utils.SpUtils;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanlang on 2016/5/10.
 */
public class ReadSmsEngine {

    public static final Uri SMS_URI = Uri.parse("content://sms/inbox");

    /**
     * 获取收件箱的短信
     *
     * @param context
     * @return
     */
    public static ContactBean getSms(Context context) {
        ContactBean contact = new ContactBean();
        //一般条件加上address = "手机号"
        String phone = EncryptUtils.decrypt(SpUtils.getString(context, Constant.PHONE));
        String selection = "address = " + phone + " and date > " + (System.currentTimeMillis() - 10 * 60 * 1000);
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address", "body"}, selection, null, "date desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                String body = cursor.getString(1);
                contact.setPhone(cursor.getString(0));
                contact.setContent(body);
                //获取验证码
//                Pattern pattern = Pattern.compile("[0-9]{4}");
//                Matcher matcher = pattern.matcher(body);
//                if (matcher.find()) {
//                    contact.setCode(matcher.group());
//                }
            }
            cursor.close();
        }

        return contact;
    }

    public static List<ContactBean> getAllSms(Context context) {
        List<ContactBean> lists = new ArrayList<>();
        ContactBean contact = new ContactBean();
        Cursor cursor = context.getContentResolver().query(SMS_URI, new String[]{"address"}, null, null, "date desc");
        if (cursor != null) {
            while (cursor.moveToNext()) {
                contact.setPhone(cursor.getString(0));
                lists.add(contact);
            }
            cursor.close();
        }

        return lists;
    }
}
