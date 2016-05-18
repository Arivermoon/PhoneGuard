package com.self.domain;

/**
 * Created by tanlang on 2016/5/10.
 */
public class ContactBean {

    private String name;
    private String phone;
    private String content;

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getPhone() {
        return phone;
    }

    public void setPhone(String phone) {
        this.phone = phone;
    }

    public String getContent() {
        return content;
    }

    public void setContent(String content) {
        this.content = content;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o instanceof ContactBean) {
            ContactBean that = (ContactBean) o;
            return phone != null ? phone.equals(that.phone) : that.phone == null;
        }
        return false;
    }

    @Override
    public int hashCode() {
        return phone != null ? phone.hashCode() : 0;
    }
}
