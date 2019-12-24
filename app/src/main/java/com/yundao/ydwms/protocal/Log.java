package com.yundao.ydwms.protocal;


import com.yundao.ydwms.BuildConfig;

/**
 * 日志输出
 */
public class Log {

    public static final boolean isDebug = BuildConfig.DEBUG;

    /**
     * Debug输出Log日志
     **/
    public static void d(String tag, String msg) {
        if (isDebug) {
            if (tag == null || tag.length() == 0
                    || msg == null || msg.length() == 0)
                return;

            int segmentSize = 3 * 1024;
            long length = msg.length();
            if (length <= segmentSize ) {// 长度小于等于限制直接打印
                android.util.Log.d(tag, msg);
            }else {
                while (msg.length() > segmentSize ) {// 循环分段打印日志
                    String logContent = msg.substring(0, segmentSize );
                    msg = msg.replace(logContent, "");
                    android.util.Log.d(tag, logContent);
                }
                android.util.Log.d(tag, msg);// 打印剩余日志
            }
        }
    }

    /**
     * Error输出Log日志
     **/
    public static void e(String tag, String msg) {
        if (isDebug) {
//            android.util.Log.e(tag, msg);
            if (msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    if (i + 4000 < msg.length())
                        android.util.Log.e(tag + i, msg.substring(i, i + 4000));
                    else
                        android.util.Log.e(tag + i, msg.substring(i, msg.length()));
                }
            } else android.util.Log.e(tag, msg);
        }
    }

    /**
     * Error输出Log日志
     **/
    public static void e(String tag, String msg, Throwable e) {
        if (isDebug) {
//            android.util.Log.e(tag, msg + " " + e.getMessage());
            msg = msg + " " + e.getMessage();
            if (msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    if (i + 4000 < msg.length())
                        android.util.Log.e(tag + i, msg.substring(i, i + 4000));
                    else
                        android.util.Log.e(tag + i, msg.substring(i, msg.length()));
                }
            } else android.util.Log.e(tag, msg);
        }
    }

    /**
     * warning输出Log日志
     **/
    public static void w(String tag, String msg) {
        if (isDebug) {
//            android.util.Log.w(tag, msg);
            if (msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    if (i + 4000 < msg.length())
                        android.util.Log.w(tag + i, msg.substring(i, i + 4000));
                    else
                        android.util.Log.w(tag + i, msg.substring(i, msg.length()));
                }
            } else android.util.Log.w(tag, msg);
        }
    }

    /**
     * warning输出Log日志
     **/
    public static void w(String tag, String msg, Exception e) {
        if (isDebug) {
//            android.util.Log.w(tag, msg + " " + e.getMessage());
            msg = msg + " " + e.getMessage();
            if (msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    if (i + 4000 < msg.length())
                        android.util.Log.w(tag + i, msg.substring(i, i + 4000));
                    else
                        android.util.Log.w(tag + i, msg.substring(i, msg.length()));
                }
            } else android.util.Log.w(tag, msg);
        }
    }

    /**
     * Info输出Log日志
     **/
    public static void i(String tag, String msg) {
        if (isDebug) {
//            android.util.Log.i(tag, msg);
            if (msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    if (i + 4000 < msg.length())
                        android.util.Log.i(tag + i, msg.substring(i, i + 4000));
                    else
                        android.util.Log.i(tag + i, msg.substring(i, msg.length()));
                }
            } else android.util.Log.i(tag, msg);
        }
    }

    /**
     * Send a VERBOSE log message
     */
    public static void v(String tag, String msg) {
        if (isDebug) {
            if (msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    if (i + 4000 < msg.length())
                        android.util.Log.v(tag + i, msg.substring(i, i + 4000));
                    else
                        android.util.Log.v(tag + i, msg.substring(i, msg.length()));
                }
            } else android.util.Log.v(tag, msg);
        }
    }
}
