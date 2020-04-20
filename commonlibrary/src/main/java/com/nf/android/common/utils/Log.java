package com.nf.android.common.utils;



/**
 * 日志输出
 */
public class Log {

    public static final boolean isDebug = true;

    /**
     * Debug输出Log日志
     **/
    public static void d(String tag, String msg) {
        if (isDebug) {
            if (msg.length() > 4000) {
                for (int i = 0; i < msg.length(); i += 4000) {
                    if (i + 4000 < msg.length())
                        android.util.Log.d(tag + i, msg.substring(i, i + 4000));
                    else
                        android.util.Log.d(tag + i, msg.substring(i, msg.length()));
                }
            } else android.util.Log.d(tag, msg);
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
