package com.self.domain;

import android.graphics.drawable.Drawable;

/**
 * Created by tanlang on 2016/5/29.
 */
public class AppBean {
    private Drawable appIcon;
    private String appName;
    private long size;
    private boolean isSdCard;
    private boolean isSystem;
    private String packageName;

    public Drawable getAppIcon() {
        return appIcon;
    }

    public void setAppIcon(Drawable appIcon) {
        this.appIcon = appIcon;
    }

    public String getAppName() {
        return appName;
    }

    public void setAppName(String appName) {
        this.appName = appName;
    }

    public long getSize() {
        return size;
    }

    public void setSize(long size) {
        this.size = size;
    }

    public boolean isSdCard() {
        return isSdCard;
    }

    public void setSdCard(boolean sdCard) {
        isSdCard = sdCard;
    }

    public boolean isSystem() {
        return isSystem;
    }

    public void setSystem(boolean system) {
        isSystem = system;
    }

    public String getPackageName() {
        return packageName;
    }

    public void setPackageName(String packageName) {
        this.packageName = packageName;
    }
}
