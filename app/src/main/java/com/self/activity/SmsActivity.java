package com.self.activity;

import com.self.domain.ContactBean;
import com.self.engine.ReadSmsEngine;

import java.util.List;

public class SmsActivity extends BaseListViewActivity {

    @Override
    public List<ContactBean> getDatas() {
        return ReadSmsEngine.getAllSms(this);
    }
}
