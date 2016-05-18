package com.self.activity;

import android.Manifest;

import com.self.domain.ContactBean;
import com.self.engine.ReadSmsEngine;

import java.util.List;

/**
 * 短信记录
 */
public class SmsLogActivity extends BaseListViewActivity {

    @Override
    public List<ContactBean> getDatas() {
        return ReadSmsEngine.getAllSms(this);
    }

    @Override
    public String getPermission() {
        return Manifest.permission.READ_SMS;
    }
}
