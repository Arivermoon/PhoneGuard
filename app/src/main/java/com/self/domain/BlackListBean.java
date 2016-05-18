package com.self.domain;

/**
 * Created by tanlang on 2016/5/15.
 */
public class BlackListBean {

    private String phone;

    private int mode;

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public int getMode() {
        return mode;
    }

    public void setMode(int mode) {
        this.mode = mode;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof BlackListBean) {
            BlackListBean bean = (BlackListBean) o;
            return phone.equals(bean.getPhone());
        }
        return false;
    }

    @Override
    public int hashCode() {
        return phone.hashCode();
    }
}
