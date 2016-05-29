package com.self.engine;

import android.content.pm.ApplicationInfo;
import android.content.pm.PackageInfo;
import android.content.pm.PackageManager;
import android.os.Environment;

import com.self.domain.AppBean;

import java.io.File;
import java.util.ArrayList;
import java.util.List;

/**
 * @author Administrator
 */
public class AppManagerEngine {

    public static long getSDCardFreeSpace() {
        return Environment.getExternalStorageDirectory().getFreeSpace();
    }

    public static long getRomFreeSpace() {
        return Environment.getDataDirectory().getFreeSpace();
    }

    public static List<AppBean> getAllApks(PackageManager pm) {
        List<AppBean> appBeens = new ArrayList<>();
        List<PackageInfo> installedPackages = pm.getInstalledPackages(0);
        for (PackageInfo packageInfo : installedPackages) {
            AppBean bean = new AppBean();
            bean.setAppName(packageInfo.applicationInfo.loadLabel(pm).toString());
            bean.setPackageName(packageInfo.packageName);
            bean.setAppIcon(packageInfo.applicationInfo.loadIcon(pm));
            File file = new File(packageInfo.applicationInfo.sourceDir);
            bean.setSize(file.length());
            int flag = packageInfo.applicationInfo.flags;
            if ((flag & ApplicationInfo.FLAG_SYSTEM) != 0) {
                bean.setSystem(true);
            } else {
                bean.setSystem(false);
            }
            if ((flag & ApplicationInfo.FLAG_EXTERNAL_STORAGE) != 0) {
                bean.setSdCard(true);
            } else {
                bean.setSdCard(false);
            }

            appBeens.add(bean);
        }

        return appBeens;
    }


}
