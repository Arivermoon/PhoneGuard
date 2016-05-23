package com.self.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;

import com.self.constant.Constant;
import com.self.service.BlackListService;
import com.self.util.ServiceUtils;
import com.self.util.SpUtils;
import com.self.view.SettingCenterItemView;

public class SettingCenterActivity extends AppCompatActivity {

    private SettingCenterItemView item_autoupdate;
    private SettingCenterItemView item_blacklist;
    private SettingCenterItemView item_telsafe;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingcenter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        item_autoupdate = (SettingCenterItemView) findViewById(R.id.item_autoupdate);
        item_blacklist = (SettingCenterItemView) findViewById(R.id.item_blacklist);
        item_telsafe = (SettingCenterItemView) findViewById(R.id.item_telsafe);

        initData();
        initEvent();
    }

    private void initEvent() {
        item_autoupdate.setItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item_autoupdate.setChecked(!item_autoupdate.isChecked());
                SpUtils.putBoolean(getApplicationContext(), Constant.AUTO_UPDATE, item_autoupdate.isChecked());
            }
        });

        item_blacklist.setItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(SettingCenterActivity.this, BlackListService.class);
                if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.self.service.BlackListService")) {
                    stopService(service);
                    item_blacklist.setChecked(false);
                } else {
                    startService(service);
                    item_blacklist.setChecked(true);
                }
            }
        });

        item_telsafe.setItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                item_telsafe.setChecked(!item_telsafe.isChecked());
                SpUtils.putBoolean(getApplicationContext(), Constant.TEL_SAFE, item_telsafe.isChecked());
            }
        });
    }

    private void initData() {
        item_autoupdate.setChecked(SpUtils.getBoolean(this, Constant.AUTO_UPDATE));
        item_blacklist.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.self.service.BlackListService"));

        item_telsafe.setChecked(SpUtils.getBoolean(this, Constant.TEL_SAFE));
    }
}
