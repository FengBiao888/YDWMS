package com.yundao.ydwms;

import android.app.Application;

import com.yundao.ydwms.protocal.respone.User;

public class YDWMSApplication extends Application {

    private static YDWMSApplication instance;

    private String authorization ;
    private User user;

    public synchronized static YDWMSApplication getInstance() {
        if (null == instance) {
            instance = new YDWMSApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();

        instance = this;
    }

    public void setAuthorization(String authorization) {
        this.authorization = authorization;
    }

    public String getAuthorization() {
        return authorization;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public User getUser() {
        return user;
    }
}
