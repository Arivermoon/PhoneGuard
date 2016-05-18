package com.self.activity;

import android.content.Intent;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.TextView;

import com.self.constant.Constant;
import com.self.util.SpUtils;

public class LostFindActivity extends AppCompatActivity {

    private TextView textView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        if (SpUtils.getBoolean(getApplicationContext(), Constant.SETTED)) {
            setContentView(R.layout.activity_lostfind);
            textView = (TextView) findViewById(R.id.tv_lostfind);
            initEvent();
        } else {
            SpUtils.putBoolean(getApplicationContext(), Constant.SETTED, true);
            Intent intent = new Intent(this, Setup1Activity.class);
            startActivity(intent);
        }
    }

    private void initEvent() {
        textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(LostFindActivity.this, Setup1Activity.class);
                startActivity(intent);
                finish();
            }
        });
    }

}
