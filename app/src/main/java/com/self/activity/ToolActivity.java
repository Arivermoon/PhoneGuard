package com.self.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;

public class ToolActivity extends AppCompatActivity implements View.OnClickListener {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        TextView tv_phone_home = (TextView) findViewById(R.id.tv_phone_home);
        tv_phone_home.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_phone_home:
                Intent intent = new Intent(this, PhoneHomeActivity.class);
                startActivity(intent);
                break;
        }
    }
}
