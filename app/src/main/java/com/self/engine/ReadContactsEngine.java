package com.self.engine;

import android.content.ContentResolver;
import android.content.Context;
import android.database.Cursor;
import android.net.Uri;

import com.self.domain.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanlang on 2016/5/10.
 */
public class ReadContactsEngine {

    public static List<Contact> getContacts(Context context) {
        List<Contact> lists = new ArrayList<>();
        Uri uri = Uri.parse("content://com.android.contacts/contacts");
        ContentResolver resolver = context.getContentResolver();
        Cursor cursor = resolver.query(uri, new String[]{"_id"}, null, null, null);
        if (cursor != null && cursor.getCount() > 0){
            while (cursor.moveToNext()) {
                Contact contact = new Contact();
                int contactId = cursor.getInt(0);
                uri = Uri.parse("content://com.android.contacts/contacts/" + contactId + "/data");
                Cursor cursor1 = resolver.query(uri, new String[]{"mimetype", "data1"}, null, null, null);
                if (cursor1 != null && cursor1.getCount() > 0){
                    while (cursor1.moveToNext()) {
                        String mimeType = cursor1.getString(0);
                        String data1 = cursor1.getString(1);
                        if ("vnd.android.cursor.item/name".equals(mimeType)) { //是姓名
                            contact.setName(data1);
                        } else if ("vnd.android.cursor.item/phone_v2".equals(mimeType)) { //手机
                            contact.setPhone(data1);
                        }
                    }
                    cursor1.close();
                    lists.add(contact);
                }
            }
            cursor.close();
        }

        return lists;
    }
}