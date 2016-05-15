package com.self.service;

import android.app.Service;
import android.app.admin.DevicePolicyManager;
import android.content.Intent;
import android.database.ContentObserver;
import android.media.MediaPlayer;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;

import com.self.activity.R;
import com.self.domain.ContactBean;
import com.self.engine.ReadSmsEngine;

public class LostFindService extends Service {

    private SmsObserver smsObserver;
    
    private boolean isPlaying = false;

    private Handler handler = new Handler() {
        @Override
        public void handleMessage(Message msg) {
            String content = (String) msg.obj;
            if (content.equals("#*gps*#")) {

            } else if (content.equals("#*lockscreen*#")) {
                //一键锁屏
                DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                dpm.resetPassword("123456", 0);
                dpm.lockNow();
            } else if (content.equals("#*wipedata*#")) {
                //清除SD卡数据
                DevicePolicyManager dpm = (DevicePolicyManager) getSystemService(DEVICE_POLICY_SERVICE);
                dpm.wipeData(DevicePolicyManager.WIPE_EXTERNAL_STORAGE);
            } else if (content.equals("#*music*#")) {
                if (!isPlaying){
                    isPlaying = true;
                    MediaPlayer mp = MediaPlayer.create(getApplication(), R.raw.qqqg);
                    mp.start();
                    mp.setOnCompletionListener(new MediaPlayer.OnCompletionListener() {
                        @Override
                        public void onCompletion(MediaPlayer mp) {
                            isPlaying = false;
                        }
                    });
                }
                
            }
        }
    };

    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        smsObserver = new SmsObserver(handler);
        //注册内容观察者
        getContentResolver().registerContentObserver(ReadSmsEngine.SMS_URI, true, smsObserver);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        //取消注册内容观察者
        getContentResolver().unregisterContentObserver(smsObserver);
    }

    /**
     * 内容观察者
     */
    private class SmsObserver extends ContentObserver {

        public SmsObserver(Handler handler) {
            super(handler);
        }

        @Override
        public void onChange(boolean selfChange) {
            super.onChange(selfChange);
            //每当有新短信到来时，获取短消息
            ContactBean sms = ReadSmsEngine.getSms(getApplicationContext());
            Message msg = Message.obtain();
            msg.obj = sms.getContent();
            handler.sendMessage(msg);
        }
    }

}
