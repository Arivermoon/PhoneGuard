package com.self.activity;

import android.Manifest;

import com.self.domain.ContactBean;
import com.self.engine.ReadContactsEngine;

import java.util.List;

public class ContactActivity extends BaseListViewActivity {

    @Override
    public List<ContactBean> getDatas() {
        return ReadContactsEngine.getContacts(this);
    }

    @Override
    public String getPermission() {
        return Manifest.permission.READ_CONTACTS;
    }
}
