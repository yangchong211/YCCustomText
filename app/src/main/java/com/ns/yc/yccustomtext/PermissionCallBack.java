package com.ns.yc.yccustomtext;


import android.content.Context;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/9/10
 *     desc  : 权限申请回调callback
 *     revise:
 * </pre>
 */
public interface PermissionCallBack {

    /**
     * 拒绝权限
     */
    int REFUSE = 1;
    /**
     * 权限申请失败
     */
    int DEFEATED = 2;

    /**
     * 申请权限成功
     * @param context                       上下文
     */
    void onPermissionGranted(Context context);

    /**
     * 申请权限失败
     * @param context                       上下文
     * @param type                          类型，1是拒绝权限，2是申请失败
     */
    void onPermissionDenied(Context context, int type);


}
