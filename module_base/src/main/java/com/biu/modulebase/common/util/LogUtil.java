package com.biu.modulebase.common.util;

import android.util.Log;

import com.biu.modulebase.BuildConfig;
import com.biu.modulebase.Constant;


public class LogUtil {
    private LogUtil() {
        /* cannot be instantiated */
        throw new UnsupportedOperationException("cannot be instantiated");
    }

    private static final String TAG = Constant.TAG;

    // 下面四个是默认tag的函数
    public static void LogI(String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.i(TAG, msg);
    }

    public static void LogD(String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.d(TAG, msg);
    }

    public static void LogE(String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.e(TAG, msg);
    }

    public static void LogV(String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.v(TAG, msg);
    }

    // 下面是传入自定义tag的函数
    public static void LogI(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.i(tag, msg);
    }

    public static void LogD(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.d(tag, msg);
    }

    public static void LogE(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.e(tag, msg);
    }

    public static void v(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.v(tag, msg);
    }

    public static void LogW(String tag, String msg) {
        if (BuildConfig.LOG_DEBUG)
            Log.w(tag, msg);
    }
}