package com.self.engine;

import android.content.Context;
import android.database.Cursor;
import android.provider.ContactsContract.CommonDataKinds.Phone;

import com.self.domain.ContactBean;

import java.util.ArrayList;
import java.util.List;

/**
 * Created by tanlang on 2016/5/10.
 */
public class ReadContactsEngine {

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
}
