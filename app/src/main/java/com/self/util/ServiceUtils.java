package com.self.util;

import android.app.ActivityManager;
import android.app.ActivityManager.RunningServiceInfo;
import android.content.Context;

import java.util.List;

/**
 * Created by tanlang on 2016/5/14.
 */
public class ServiceUtils {

    /**
     * 判断服务是否在运行
     *
     * @param context
     * @param serviceName 完整包名+类名，例如：com.self.service.LostFindService
     * @return
     */
    public static boolean isServiceRunning(Context context, String serviceName) {
        boolean isRunning = false;
        ActivityManager manager = (ActivityManager) context.getSystemService(Context.ACTIVITY_SERVICE);
        List<RunningServiceInfo> runningServices = manager.getRunningServices(100);
        for (RunningServiceInfo serviceInfo : runningServices) {
            String className = serviceInfo.service.getClassName();
            if (serviceName.equals(className)) {
                isRunning = true;
                break;
            }
        }

        return isRunning;
    }
}
