package com.self.service;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.PixelFormat;
import android.os.Handler;
import android.os.IBinder;
import android.os.SystemClock;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;
import android.view.Gravity;
import android.view.View;
import android.view.WindowManager;
import android.widget.TextView;

import com.self.activity.R;
import com.self.constant.Constant;
import com.self.engine.PhoneHomeEngine;
import com.self.util.SpUtils;

public class IncomingPhoneService extends Service {

    private OutCallReceiver outCallReceiver;
    private TelephonyManager manager;
    private PhoneStateListener listener;
    private WindowManager.LayoutParams params;
    private WindowManager wm;
    private View view;
    private boolean isOutCall = false;

    private int bgStyles[] = new int[]{R.drawable.call_locate_blue, R.drawable.call_locate_gray,
            R.drawable.call_locate_green, R.drawable.call_locate_orange, R.drawable.call_locate_white};

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    private class OutCallReceiver extends BroadcastReceiver {
        @Override
        public void onReceive(Context context, Intent intent) {
            isOutCall = true;
            String phoneNumber = getResultData();
            showLocationToast(phoneNumber);
        }
    }

    @Override
    public void onCreate() {
        super.onCreate();
        initToastParams();
        wm = (WindowManager) getSystemService(WINDOW_SERVICE);
        outCallReceiver = new OutCallReceiver();
        IntentFilter intentFilter = new IntentFilter(Intent.ACTION_NEW_OUTGOING_CALL);
        registerReceiver(outCallReceiver, intentFilter);
        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, String incomingNumber) {
                super.onCallStateChanged(state, incomingNumber);
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:
                        closeLocationToast();
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK://通话状态
                        closeLocationToast();
                        break;
                    case TelephonyManager.CALL_STATE_RINGING:
                        showLocationToast(incomingNumber);
                        break;
                }
            }
        };
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        unregisterReceiver(outCallReceiver);
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

    private Handler handler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (view != null) {
                wm.removeView(view);
                view = null;
            }
        }
    };

    private void showLocationToast(String phoneNumber) {
        if (!isOutCall)
            closeLocationToast();
        view = View.inflate(getApplicationContext(), R.layout.sys_toast, null);
        int index = SpUtils.getInt(getApplicationContext(), Constant.STYLEBGINDEX);
        view.setBackgroundResource(bgStyles[index]);
        TextView tv_location = (TextView) view
                .findViewById(R.id.tv_toast_location);
        tv_location.setText(PhoneHomeEngine.queryPhoneHome(phoneNumber,
                getApplicationContext()));
        wm.addView(view, params);
    }

    protected void closeLocationToast() {
        if (isOutCall) {//外拨电话延迟关闭
            new Thread() {
                @Override
                public void run() {
                    SystemClock.sleep(6000);
                    handler.obtainMessage().sendToTarget();
                }
            }.start();
            isOutCall = false;
        } else {//接听电话直接关闭
            if (view != null) {
                wm.removeView(view);
                view = null;
            }
        }
    }

    private void initToastParams() {
        params = new WindowManager.LayoutParams();
        params.height = WindowManager.LayoutParams.WRAP_CONTENT;
        params.width = WindowManager.LayoutParams.WRAP_CONTENT;
        params.gravity = Gravity.CENTER;
        params.format = PixelFormat.TRANSLUCENT;
        params.type = WindowManager.LayoutParams.TYPE_PRIORITY_PHONE;
        params.setTitle("Toast");
        params.flags = WindowManager.LayoutParams.FLAG_KEEP_SCREEN_ON
                | WindowManager.LayoutParams.FLAG_NOT_FOCUSABLE
                | WindowManager.LayoutParams.FLAG_NOT_TOUCHABLE;
    }
}
