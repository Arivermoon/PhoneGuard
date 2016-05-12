package com.self.activity;

import android.content.Intent;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v7.app.AppCompatActivity;
import android.view.GestureDetector;
import android.view.MotionEvent;
import android.view.View;

/**
 * Created by tanlang on 2016/5/9.
 */
public abstract class BaseSetupActivity extends AppCompatActivity {

    private GestureDetector gd;

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        initView();
        initData();
        initEvent();
        initGesture();//初始化手势操作
    }

    protected abstract void initData();

    protected abstract void initEvent();

    private void initGesture() {
        gd = new GestureDetector(this, new GestureDetector.OnGestureListener() {

            @Override
            public boolean onFling(MotionEvent e1, MotionEvent e2, float velocityX, float velocityY) {
                float x = e2.getX() - e1.getX();
                if (Math.abs(x) < 200) {
                    return false;
                }
                if (x > 0) {//往右滑
                    prev(null);
                } else {//往左滑
                    next(null);
                }
                return true;
            }

            @Override
            public boolean onDown(MotionEvent e) {
                return false;
            }

            @Override
            public void onShowPress(MotionEvent e) {

            }

            @Override
            public boolean onSingleTapUp(MotionEvent e) {
                return false;
            }

            @Override
            public boolean onScroll(MotionEvent e1, MotionEvent e2, float distanceX, float distanceY) {
                return false;
            }

            @Override
            public void onLongPress(MotionEvent e) {

            }
        });
    }

    @Override
    public boolean onTouchEvent(MotionEvent event) {
        gd.onTouchEvent(event);
        return super.onTouchEvent(event);
    }

    public abstract void initView();

    //点击事件
    public void next(View v) {
        nextActivity();
        nextAnimation();
    }

    protected abstract void nextActivity();

    private void nextAnimation() {
        overridePendingTransition(R.anim.next_in, R.anim.next_out);
    }

    public void startActivity(Class type) {
        Intent intent = new Intent(this, type);
        startActivity(intent);
        finish();
    }

    //点击事件
    public void prev(View v) {
        prevActivity();
        prevAnimation();
    }

    private void prevAnimation() {
        overridePendingTransition(R.anim.prev_in, R.anim.prev_out);
    }

    protected abstract void prevActivity();

}
