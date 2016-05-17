package com.self.activity;

import com.self.domain.ContactBean;
import com.self.engine.ReadContactsEngine;

import java.util.List;

public class CallActivity extends BaseListViewActivity {

    @Override
    public List<ContactBean> getDatas() {
        return ReadContactsEngine.getCalllog(this);
    }
}
