package com.ns.yc.yccustomtextlib.utils;

import android.content.Context;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 功能工具类
 *     revise:
 * </pre>
 */
public final class HyperLibUtils {


    public static int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }


}
