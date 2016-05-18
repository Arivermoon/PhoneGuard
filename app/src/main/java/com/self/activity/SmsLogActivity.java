package com.self.activity;

import android.Manifest;

import com.self.domain.ContactBean;
import com.self.engine.ReadSmsEngine;

import java.util.ArrayList;
import java.util.List;
import java.util.Set;

/**
 * 短信记录
 */
public class SmsLogActivity extends BaseListViewActivity {

    @Override
    public List<ContactBean> getDatas() {
        List<ContactBean> lists = new ArrayList<>();
        Set<ContactBean> sets = ReadSmsEngine.getAllSms(this);
        lists.addAll(sets);
        return lists;
    }

    @Override
    public String getPermission() {
        return Manifest.permission.READ_SMS;
    }
}
