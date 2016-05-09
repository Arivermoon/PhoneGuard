package com.self.activity;

/**
 * Created by tanlang on 2016/5/9.
 */
public class Setup1Activity extends BaseSetupActivity {

    @Override
    public void initView() {
        setContentView(R.layout.activity_setup1);
    }

    @Override
    protected void initEvent() {

    }

    @Override
    protected void nextActivity() {
        startActivity(Setup2Activity.class);
    }

    @Override
    protected void prevActivity() {

    }
}
