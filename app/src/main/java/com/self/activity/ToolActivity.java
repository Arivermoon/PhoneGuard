package com.self.activity;

import android.app.ProgressDialog;
import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import com.self.engine.ReadSmsEngine;

public class ToolActivity extends AppCompatActivity implements View.OnClickListener {

    private ProgressDialog pd;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tool);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        pd = new ProgressDialog(this);
        pd.setProgressStyle(ProgressDialog.STYLE_HORIZONTAL);

        TextView tv_phone_home = (TextView) findViewById(R.id.tv_phone_home);
        TextView tv_backup_sms = (TextView) findViewById(R.id.tv_backup_sms);
        TextView tv_restore_sms = (TextView) findViewById(R.id.tv_restore_sms);
        tv_phone_home.setOnClickListener(this);
        tv_backup_sms.setOnClickListener(this);
        tv_restore_sms.setOnClickListener(this);
    }

    @Override
    public void onClick(View v) {
        switch (v.getId()) {
            case R.id.tv_phone_home:
                Intent intent = new Intent(this, PhoneHomeActivity.class);
                startActivity(intent);
                break;
            case R.id.tv_backup_sms:
                ReadSmsEngine.smsBackUpJson(this, new ReadSmsEngine.BackUpProgress() {
                    @Override
                    public void show() {
                        pd.show();
                    }

                    @Override
                    public void setMax(int max) {
                        pd.setMax(max);
                    }

                    @Override
                    public void setProgress(int progress) {
                        pd.setProgress(progress);
                    }

                    @Override
                    public void end() {
                        pd.dismiss();
                    }
                });
                Toast.makeText(this, "短信备份成功", Toast.LENGTH_SHORT).show();
                break;
            case R.id.tv_restore_sms:
                ReadSmsEngine.smsRestoreJson(this, new ReadSmsEngine.BackUpProgress() {
                    @Override
                    public void show() {
                        pd.show();
                    }

                    @Override
                    public void setMax(int max) {
                        pd.setMax(max);
                    }

                    @Override
                    public void setProgress(int progress) {
                        pd.setProgress(progress);
                    }

                    @Override
                    public void end() {
                        pd.dismiss();
                    }
                });
                Toast.makeText(this, "短信还原成功", Toast.LENGTH_SHORT).show();
                break;
            default:
                break;
        }
    }
}
