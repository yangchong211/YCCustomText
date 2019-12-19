package com.ns.yc.yccustomtextlib.utils;

import android.util.Log;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 日志工具类
 *     revise:
 * </pre>
 */
public final class HyperLogUtils {


    private static final String TAG = "HyperLogUtils";
    private static boolean isLog = true;

    /**
     * 设置是否开启日志
     * @param isLog                 是否开启日志
     */
    public static void setIsLog(boolean isLog) {
        HyperLogUtils.isLog = isLog;
    }

    public static void d(String message) {
        if(isLog){
            Log.d(TAG, message);
        }
    }

    public static void i(String message) {
        if(isLog){
            Log.i(TAG, message);
        }

    }

    public static void e(String message, Throwable throwable) {
        if(isLog){
            Log.e(TAG, message, throwable);
        }
    }

}
