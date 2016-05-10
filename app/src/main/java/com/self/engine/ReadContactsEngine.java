package com.self.engine;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.provider.ContactsContract;

import com.self.domain.Contact;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanlang on 2016/5/10.
 */
public class ReadContactsEngine {

    public static List<Contact> getContacts(Context context) {
        Uri uri4Contacts = Uri.parse(ContactsContract.AUTHORITY_URI + "/contacts");
        Uri uri4Data = Uri.parse(ContactsContract.AUTHORITY_URI + "/data");
        List<Contact> lists = new ArrayList<>();
        Cursor cursor1 = context.getContentResolver().query(uri4Contacts, new String[]{"_id"}, null, null, null);
        while (cursor1.moveToNext()) {
            Contact contact = new Contact();
            String id = cursor1.getString(0);
            Cursor cursor2 = context.getContentResolver().query(uri4Data, new String[]{"data1", "mimetype"}, "raw_contact_id = ?", new String[]{id}, null);
            while (cursor2.moveToNext()) {
                String data = cursor2.getString(0);
                String mimetype = cursor2.getString(1);
                if (mimetype.equals("vnd.android.cursor.item/name")) {
                    contact.setName(data);
                } else if (mimetype.equals("vnd.android.cursor.item/phone_v2")) {
                    contact.setPhone(data);
                }
            }
            cursor2.close();
            lists.add(contact);
        }
        //关闭游标，释放资源
        cursor1.close();
        return lists;
    }
}
