package com.nf.android.common.utils;

/**
 * 设置常量工具类
 * @author yangdl
 * @time 2019/11/6
 */
public class ConstantUtil {

    private String actionLogout; //退出登录时的广播动作

    private transient static ConstantUtil instance = null;

    public static ConstantUtil getInstance() {
        if (instance == null) {
            instance = new ConstantUtil();
        }
        return instance;
    }

    public String getActionLogout() {
        return actionLogout;
    }

    public void setActionLogout(String actionLogout) {
        this.actionLogout = actionLogout;
    }
}
