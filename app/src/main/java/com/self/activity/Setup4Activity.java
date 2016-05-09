package com.self.activity;

/**
 * Created by tanlang on 2016/5/9.
 */
public class Setup4Activity extends BaseSetupActivity {

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup4);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void nextActivity() {

    }

    @Override
    protected void prevActivity() {
        startActivity(Setup3Activity.class);
    }
}
