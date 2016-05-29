package com.self.activity;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.MotionEvent;
import android.view.View;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.self.constant.Constant;
import com.self.service.BlackListService;
import com.self.service.IncomingPhoneService;
import com.self.util.ServiceUtils;
import com.self.util.SpUtils;
import com.self.view.SettingCenterItemView;

public class SettingCenterActivity extends AppCompatActivity {

    private String[] styleNames = new String[]{"卫士蓝", "金属灰", "苹果绿", "活力橙", "半透明"};
    private SettingCenterItemView item_autoupdate;
    private SettingCenterItemView item_blacklist;
    private SettingCenterItemView item_phone_show;
    private RelativeLayout item_phone_home;
    private ImageView iv_phone_home;
    private TextView tv_phone_home_content;
    private AlertDialog dialog;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_settingcenter);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        item_autoupdate = (SettingCenterItemView) findViewById(R.id.item_autoupdate);
        item_blacklist = (SettingCenterItemView) findViewById(R.id.item_blacklist);
        item_phone_show = (SettingCenterItemView) findViewById(R.id.item_phone_show);
        item_phone_home = (RelativeLayout) findViewById(R.id.item_phone_home);
        iv_phone_home = (ImageView) findViewById(R.id.iv_phone_home);
        tv_phone_home_content = (TextView) findViewById(R.id.tv_phone_home_content);

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

        item_phone_show.setItemOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent service = new Intent(SettingCenterActivity.this, IncomingPhoneService.class);
                if (ServiceUtils.isServiceRunning(getApplicationContext(), "com.self.service.IncomingPhoneService")) {
                    item_phone_show.setChecked(false);
                    stopService(service);
                } else {
                    item_phone_show.setChecked(true);
                    startService(service);
                }
            }
        });

        item_phone_home.setOnTouchListener(new View.OnTouchListener() {
            @Override
            public boolean onTouch(View v, MotionEvent event) {
                switch (event.getAction()) {
                    case MotionEvent.ACTION_DOWN:
                        iv_phone_home.setImageResource(R.drawable.arrow_pressed);
                        break;
                    case MotionEvent.ACTION_UP:
                        iv_phone_home.setImageResource(R.drawable.arrow_disable);
                        showStyleDialog();
                        break;
                }
                return false;
            }
        });
    }

    private void showStyleDialog() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("选择归属地样式");
        builder.setSingleChoiceItems(styleNames, SpUtils.getInt(this, Constant.STYLEBGINDEX), new DialogInterface.OnClickListener() {
            @Override
            public void onClick(DialogInterface dialog, int which) {
                SpUtils.putInt(getApplicationContext(), Constant.STYLEBGINDEX, which);
                tv_phone_home_content.setText(styleNames[which]);
                dialog.dismiss();
            }
        });
        dialog = builder.create();
        dialog.show();
    }

    private void initData() {
        item_autoupdate.setChecked(SpUtils.getBoolean(this, Constant.AUTO_UPDATE));
        item_blacklist.setChecked(ServiceUtils.isServiceRunning(getApplicationContext(), "com.self.service.BlackListService"));

        item_phone_show.setChecked(SpUtils.getBoolean(this, Constant.TEL_SAFE));
    }
}
