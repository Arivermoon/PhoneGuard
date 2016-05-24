package com.self.service;

import android.app.Service;
import android.content.Context;
import android.content.Intent;
import android.database.ContentObserver;
import android.net.Uri;
import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.RemoteException;
import android.support.annotation.Nullable;
import android.telephony.PhoneStateListener;
import android.telephony.TelephonyManager;

import com.android.internal.telephony.ITelephony;
import com.self.constant.Constant;
import com.self.dao.BlackListDao;
import com.self.domain.BlackListBean;
import com.self.domain.ContactBean;
import com.self.engine.ReadContactsEngine;
import com.self.engine.ReadSmsEngine;

import java.lang.ref.WeakReference;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;

/**
 * Created by tanlang on 2016/5/23.
 */
public class BlackListService extends Service {

    private ContentObserver contentObserver;

    private MyHandler handler;
    private BlackListDao blackListDao;
    private BlackListBean bean;
    private TelephonyManager manager;
    private PhoneStateListener listener;

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

    static class MyHandler extends Handler {

        WeakReference<BlackListService> wf;

        public MyHandler(BlackListService service) {
            wf = new WeakReference<>(service);
        }

        @Override
        public void handleMessage(Message msg) {
            BlackListService service = wf.get();
            BlackListBean b = (BlackListBean) msg.obj;
            if (b != null) {
                int mode = b.getMode();
                if ((mode & Constant.SMS) != 0) {
                    ReadSmsEngine.deleteSms(service, b.getPhone());
                }
            }
        }
    }


    @Override
    public void onCreate() {
        super.onCreate();
        handler = new MyHandler(this);
        blackListDao = new BlackListDao(this);
        contentObserver = new ContentObserver(handler) {

            @Override
            public void onChange(boolean selfChange) {
                super.onChange(selfChange);
                ContactBean latestSms = ReadSmsEngine.getLatestSms(getApplicationContext());
                bean = blackListDao.findByPhone(latestSms.getPhone());
                Message msg = Message.obtain();
                msg.obj = bean;
                handler.sendMessage(msg);
            }
        };

        getContentResolver().registerContentObserver(ReadSmsEngine.SMS_URI, true, contentObserver);

        manager = (TelephonyManager) getSystemService(TELEPHONY_SERVICE);
        listener = new PhoneStateListener() {
            @Override
            public void onCallStateChanged(int state, final String incomingNumber) {
                switch (state) {
                    case TelephonyManager.CALL_STATE_IDLE:// 挂断的状态，空闲的状态
                        break;
                    case TelephonyManager.CALL_STATE_RINGING://响铃状态
                        int mode = blackListDao.findByPhone(incomingNumber).getMode();
                        if ((mode & Constant.TEL) != 0) {
                            getContentResolver().registerContentObserver(Uri.parse("content://call_log/calls"), true, new ContentObserver(new Handler()) {
                                @Override
                                public void onChange(boolean selfChange) {
                                    super.onChange(selfChange);
                                    deleteCalllog(incomingNumber);//删除电话日志
                                    getContentResolver().unregisterContentObserver(this);
                                }
                            });
                            endCall();
                        }
                        break;
                    case TelephonyManager.CALL_STATE_OFFHOOK:// 通话的状态
                        break;
                    default:
                        break;
                }
            }
        };
        manager.listen(listener, PhoneStateListener.LISTEN_CALL_STATE);
    }

    private void endCall() {
        try {
            Class clazz = Class.forName("android.os.ServiceManager");
            Method method = clazz.getDeclaredMethod("getService", String.class);
            IBinder binder = (IBinder) method.invoke(null, Context.TELEPHONY_SERVICE);
            ITelephony iTelephony = ITelephony.Stub.asInterface(binder);
            iTelephony.endCall();//挂断电话
        } catch (ClassNotFoundException e) {
            e.printStackTrace();
        } catch (NoSuchMethodException e) {
            e.printStackTrace();
        } catch (IllegalAccessException e) {
            e.printStackTrace();
        } catch (IllegalArgumentException e) {
            e.printStackTrace();
        } catch (InvocationTargetException e) {
            e.printStackTrace();
        } catch (RemoteException e) {
            e.printStackTrace();
        }

    }

    private void deleteCalllog(String incomingNumber) {
        ReadContactsEngine.deleteCalllog(this, incomingNumber);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
        getContentResolver().unregisterContentObserver(contentObserver);
        manager.listen(listener, PhoneStateListener.LISTEN_NONE);
    }

}
