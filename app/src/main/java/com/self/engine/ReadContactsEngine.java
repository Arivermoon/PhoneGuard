package com.self.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.self.domain.ContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanlang on 2016/5/10.
 */
public class ReadContactsEngine {

    /**
     * 获取联系人
     *
     * @param context
     * @return
     */
    public static List<ContactBean> getContacts(Context context) {
        List<ContactBean> lists = new ArrayList<>();
        Cursor cursor = context.getContentResolver().query(Phone.CONTENT_URI, new String[]{Phone.DISPLAY_NAME, Phone.NUMBER}, null, null, Phone.SORT_KEY_PRIMARY);
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactBean contact = new ContactBean();
                contact.setName(cursor.getString(0));
                contact.setPhone(cursor.getString(1));
                lists.add(contact);
            }
            cursor.close();
        }

        return lists;
    }

    /**
     * 获取通话记录
     *
     * @param context
     * @return
     */
    public static List<ContactBean> getCallLogs(Context context) {
        Uri uri = Uri.parse("content://call_log/calls");
        Cursor cursor = context.getContentResolver().query(uri, new String[]{"number", "name"}, null, null, null);
        List<ContactBean> lists = new ArrayList<>();
        if (cursor != null) {
            while (cursor.moveToNext()) {
                ContactBean bean = new ContactBean();
                String phone = cursor.getString(0);//获取号码
                String name = cursor.getString(1);//获取名字
                bean.setName(name);
                bean.setPhone(phone);
                lists.add(bean);
            }
        }

        return lists;

    }
}
