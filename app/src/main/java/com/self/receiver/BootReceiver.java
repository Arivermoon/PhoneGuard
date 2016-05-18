package com.self.receiver;

import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.telephony.SmsManager;
import android.telephony.TelephonyManager;

import com.self.constant.Constant;
import com.self.util.EncryptUtils;
import com.self.util.SpUtils;

public class BootReceiver extends BroadcastReceiver {

    @Override
    public void onReceive(Context context, Intent intent) {
        String oldSim = SpUtils.getString(context, Constant.SIM);
        TelephonyManager manager = (TelephonyManager) context.getSystemService(Context.TELEPHONY_SERVICE);
        String newSim = manager.getSimSerialNumber();
        if (!newSim.equals(oldSim)) {
            //发送报警信息
            String phone = EncryptUtils.decrypt(SpUtils.getString(context, Constant.PHONE));
            SmsManager smsManager = SmsManager.getDefault();
            smsManager.sendTextMessage(phone, null, "我手机丢了，请不要上当受骗", null, null);
        }
    }
}
