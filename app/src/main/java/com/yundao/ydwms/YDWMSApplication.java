package com.yundao.ydwms;

import android.app.Application;

public class YDWMSApplication extends Application {

    private static YDWMSApplication instance;

    public synchronized static YDWMSApplication getInstance() {
        if (null == instance) {
            instance = new YDWMSApplication();
        }
        return instance;
    }
}
