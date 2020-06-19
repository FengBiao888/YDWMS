package com.yundao.ydwms;

import android.app.Application;
import android.os.Debug;

import com.yundao.ydwms.util.SharedPreferenceUtil;
import com.yundao.ydwms.protocal.respone.User;

public class YDWMSApplication extends Application {

    private static YDWMSApplication instance;

    private String authorization ;
    private User user;
    private boolean isPhoneTest = true;
    private boolean useLocalData = true ;

    public synchronized static YDWMSApplication getInstance() {
        if (null == instance) {
            instance = new YDWMSApplication();
        }
        return instance;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        SharedPreferenceUtil.initPreference(this);
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

    public boolean isPhoneTest() {
        return isPhoneTest;
    }

    public boolean isUseLocalData() {
        return useLocalData;
    }
}
