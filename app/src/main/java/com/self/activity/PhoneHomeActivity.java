package com.self.activity;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;

import com.self.engine.PhoneHomeEngine;

/**
 * Created by tanlang on 2016/5/24.
 */
public class PhoneHomeActivity extends AppCompatActivity {
    
    private EditText et_phone;
    private Button btn_query;
    private TextView tv_phone_home;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_phonehome);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);

        initView();
        initEvent();
    }

    private void initView() {
        et_phone = (EditText) findViewById(R.id.et_phone_number);
        btn_query = (Button) findViewById(R.id.btn_query);
        tv_phone_home = (TextView) findViewById(R.id.tv_phone_home);
    }

    private void initEvent() {
        et_phone.addTextChangedListener(new TextWatcher() {

            @Override
            public void onTextChanged(CharSequence s, int start, int before, int count) {

            }

            @Override
            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

            }

            @Override
            public void afterTextChanged(Editable s) {
                queryPhomeHome();
            }
        });

        btn_query.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View v) {
                queryPhomeHome();
            }
        });
    }

    /**
     * 归属地查询
     */
    private void queryPhomeHome() {
        String phone = et_phone.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Animation shake = AnimationUtils.loadAnimation(this, R.anim.shake);
            et_phone.startAnimation(shake);
            tv_phone_home.setText("归属地：");
            return;
        }

        String phomeHome = PhoneHomeEngine.queryPhomeHome(phone, getApplicationContext());
        tv_phone_home.setText("归属地：" + phomeHome);
    }

}
