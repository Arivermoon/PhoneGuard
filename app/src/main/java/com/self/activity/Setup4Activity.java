package com.self.activity;

import android.view.View;
import android.widget.CheckBox;
import android.widget.CompoundButton;
import android.widget.Toast;

import com.self.utils.Constant;
import com.self.utils.SpUtils;

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
        checkBox.setChecked(SpUtils.getBoolean(this, Constant.CHECK));
    }

    @Override
    protected void initEvent() {
        checkBox.setOnCheckedChangeListener(new CompoundButton.OnCheckedChangeListener() {
            @Override
            public void onCheckedChanged(CompoundButton buttonView, boolean isChecked) {

            }
        });
    }

    @Override
    public void next(View v) {
        boolean checked = checkBox.isChecked();
        if (!checked) {
            Toast.makeText(this, "未开启防盗保护", Toast.LENGTH_SHORT).show();
            return;
        }
        SpUtils.putBoolean(this, Constant.CHECK, checked);
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
