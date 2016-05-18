package com.self.activity;

import android.content.Context;
import android.telephony.TelephonyManager;
import android.text.TextUtils;
import android.view.View;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.Toast;

import com.self.constant.Constant;
import com.self.util.SpUtils;

/**
 * Created by tanlang on 2016/5/9.
 */
public class Setup2Activity extends BaseSetupActivity {

    private Button button;
    private ImageView imageView;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup2);
        button = (Button) findViewById(R.id.bt_setup2_bindsim);
        imageView = (ImageView) findViewById(R.id.iv_setup2_isbind);
    }

    @Override
    protected void initData() {
        String sim = SpUtils.getString(getApplicationContext(), Constant.SIM);
        if (TextUtils.isEmpty(sim)) {
            imageView.setImageResource(R.drawable.unlock);
        } else {
            imageView.setImageResource(R.drawable.lock);
        }
    }

    @Override
    protected void initEvent() {
        button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String sim = SpUtils.getString(getApplicationContext(), Constant.SIM);
                if (TextUtils.isEmpty(sim)) {
                    TelephonyManager manager = (TelephonyManager) getSystemService(Context.TELEPHONY_SERVICE);
                    String number = manager.getSimSerialNumber();
                    SpUtils.putString(getApplicationContext(), Constant.SIM, number);
                    imageView.setImageResource(R.drawable.lock);
                    Toast.makeText(getApplicationContext(), "绑定SIM卡成功", Toast.LENGTH_SHORT).show();
                } else {
                    SpUtils.putString(getApplicationContext(), Constant.SIM, "");
                    imageView.setImageResource(R.drawable.unlock);
                    Toast.makeText(getApplicationContext(), "解绑SIM卡成功", Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public void next(View v) {
        if (TextUtils.isEmpty(SpUtils.getString(getApplicationContext(), Constant.SIM))) {
            Toast.makeText(getApplicationContext(), "未绑定SIM卡", Toast.LENGTH_SHORT).show();
            return;
        }
        super.next(v);
    }

    @Override
    protected void nextActivity() {
        startActivity(Setup3Activity.class);
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup1Activity.class);
    }
}
