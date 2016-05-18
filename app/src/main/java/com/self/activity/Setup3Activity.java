package com.self.activity;

import android.content.Intent;
import android.text.TextUtils;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.self.constant.Constant;
import com.self.util.EncryptUtils;
import com.self.util.SpUtils;

/**
 * Created by tanlang on 2016/5/9.
 */
public class Setup3Activity extends BaseSetupActivity {

    private EditText editText;

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup3);
        editText = (EditText) findViewById(R.id.et_setup3_safenumber);
    }

    @Override
    protected void initData() {
        editText.setText(EncryptUtils.decrypt(SpUtils.getString(getApplicationContext(), Constant.PHONE)));
    }

    @Override
    protected void initEvent() {

    }

    public void selectSafeNumber(View v) {
        Intent intent = new Intent(this, ContactActivity.class);
        startActivityForResult(intent, 0);
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (data != null) {
            String phone = data.getStringExtra(Constant.PHONE);
            editText.setText(phone);
        }
        super.onActivityResult(requestCode, resultCode, data);
    }

    @Override
    public void next(View v) {
        String phone = editText.getText().toString().trim();
        if (TextUtils.isEmpty(phone)) {
            Toast.makeText(getApplicationContext(), "未设置安全号码", Toast.LENGTH_SHORT).show();
            return;
        }
        SpUtils.putString(getApplicationContext(), Constant.PHONE, EncryptUtils.encrypt(phone));
        super.next(v);
    }

    @Override
    protected void nextActivity() {
        startActivity(Setup4Activity.class);
    }

    @Override
    protected void prevActivity() {
        startActivity(Setup2Activity.class);
    }
}
