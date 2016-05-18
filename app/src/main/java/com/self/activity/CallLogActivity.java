package com.self.activity;

import android.Manifest;

import com.self.domain.ContactBean;
import com.self.engine.ReadContactsEngine;

import java.util.List;

/**
 * 通话记录
 */
public class CallLogActivity extends BaseListViewActivity {

    @Override
    public List<ContactBean> getDatas() {
        return ReadContactsEngine.getCalllog(this);
    }

    @Override
    public String getPermission() {
        return Manifest.permission.READ_CALL_LOG;
    }
}
