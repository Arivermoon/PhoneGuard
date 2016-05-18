package com.self.activity;

import android.content.Intent;
import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.self.service.LostFindService;
import com.self.util.ServiceUtils;

/**
 * Created by tanlang on 2016/5/9.
 */
public class Setup4Activity extends BaseSetupActivity {

    private CheckBox checkBox;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);
        checkBox = (CheckBox) findViewById(R.id.cb_setup4_isprotected);
    }

    @Override
    protected void initData() {
        boolean serviceRunning = ServiceUtils.isServiceRunning(Setup4Activity.this, "com.self.service.LostFindService");
        checkBox.setChecked(serviceRunning);
    }

    @Override
    protected void initEvent() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {
                Intent service = new Intent(Setup4Activity.this, LostFindService.class);
                if (isChecked) {
                    //打勾开启服务，否则停止服务
                    startService(service);
                } else {
                    stopService(service);
                }
            }
        });
    }

    @Override
    public void next(View v) {
        if (!checkBox.isChecked()) {
            Toast.makeText(this, "未开启防盗保护", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(v);
    }

    @Override
    protected void nextActivity() {
        startActivity(LostFindActivity.class);
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup3Activity.class);
    }
}
