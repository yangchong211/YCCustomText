package com.ns.yc.yccustomtext;

import android.Manifest;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.Intent;
import android.net.Uri;
import android.support.v4.app.FragmentActivity;

import com.tbruyelle.rxpermissions2.Permission;
import com.tbruyelle.rxpermissions2.RxPermissions;

import java.util.concurrent.TimeUnit;

import io.reactivex.Observable;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.functions.Consumer;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/9/10
 *     desc  : 权限申请工具类
 *     revise:
 * </pre>
 */
public class PermissionUtils {

    /**
     * 注意要点：
     * 第一个：this 参数可以是 FragmentActivity 或 Fragment。
     *        如果你在 fragment 中使用 RxPermissions，你应该传递 fragment 实例，而不是fragment.getActivity()
     * 第二个：请求权限，必须在初始化阶段比如 onCreate 中调用
     *        应用程序可能在权限请求期间重新启动，因此请求必须在初始化阶段完成。
     * 第三个：统一规范权限，避免混乱
     * 第四个：该库可以和rx结合使用
     *
     * 参考案例：https://github.com/tbruyelle/RxPermissions
     */

    /**
     * 定位权限
     */
    private static String[] LOCATION = new String[]{Manifest.permission.ACCESS_COARSE_LOCATION,
            Manifest.permission.ACCESS_FINE_LOCATION};

    /**
     * 电话
     */
    private static String[] PHONE = new String[]{Manifest.permission.CALL_PHONE,};

    /**
     * 读写存储权限
     */
    private static String[] WRITE = new String[]{Manifest.permission.READ_EXTERNAL_STORAGE,
            Manifest.permission.WRITE_EXTERNAL_STORAGE};

    /**
     * 短信权限
     */
    private static String[] SMS = new String[]{Manifest.permission.SEND_SMS,};

    /**
     * 相机权限，相机权限包括读写文件权限
     */
    private static String[] CAMERA = new String[]{Manifest.permission.CAMERA,
            Manifest.permission.WRITE_EXTERNAL_STORAGE,
            Manifest.permission.READ_EXTERNAL_STORAGE};


    /**
     * 定位权限
     * @param activity                          activity
     * @param callBack                          回调callBack
     */
    @SuppressLint("CheckResult")
    public static void checkLocationPermission(final FragmentActivity activity,
                                               final PermissionCallBack callBack) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        //request    不支持返回权限名，返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
                .request(LOCATION)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //已经获得权限
                            callBack.onPermissionGranted(activity);
                        } else {
                            //用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                            callBack.onPermissionDenied(activity, PermissionCallBack.REFUSE);
                        }
                    }
                });
    }

    /**
     * 读写权限
     * @param activity                          activity
     * @param callBack                          回调callBack
     */
    @SuppressLint("CheckResult")
    public static void checkWritePermissionsTime(final FragmentActivity activity,
                                                 final PermissionCallBack callBack) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        //ensure
        //必须配合rxJava,回调结果与request一样，不过这个可以延迟操作
        Observable.timer(10, TimeUnit.MILLISECONDS)
                .compose(rxPermissions.ensure(WRITE))
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //已经获得权限
                            callBack.onPermissionGranted(activity);
                        } else {
                            //用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                            callBack.onPermissionDenied(activity, PermissionCallBack.REFUSE);
                        }
                    }
                });
    }

    /**
     * 读写权限
     * @param activity                          activity
     * @param callBack                          回调callBack
     */
    @SuppressLint("CheckResult")
    public static void checkWritePermissionsRequest(final FragmentActivity activity,
                                                    final PermissionCallBack callBack) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        //request    不支持返回权限名，返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
                .request(WRITE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //已经获得权限
                            callBack.onPermissionGranted(activity);
                        } else {
                            //用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                            callBack.onPermissionDenied(activity, PermissionCallBack.REFUSE);
                        }
                    }
                });
    }


    /**
     * 相机权限
     * @param activity                          activity
     * @param callBack                          回调callBack
     */
    @SuppressLint("CheckResult")
    public static void checkCameraPermissions(final FragmentActivity activity,
                                              final PermissionCallBack callBack) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        //requestEachCombined
        //返回的权限名称:将多个权限名合并成一个
        //返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
                .requestEachCombined(CAMERA)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        boolean shouldShowRequestPermissionRationale =
                                permission.shouldShowRequestPermissionRationale;
                        if (permission.granted) {
                            // 用户已经同意该权限
                            callBack.onPermissionGranted(activity);
                        } else if (shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                            // 那么下次再次启动时。还会提示请求权限的对话框
                            callBack.onPermissionDenied(activity, PermissionCallBack.DEFEATED);
                        } else {
                            // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                            callBack.onPermissionDenied(activity, PermissionCallBack.REFUSE);
                        }
                    }
                });
    }

    /**
     * 检测短信息权限
     * @param activity                          activity
     * @param callBack                          回调callBack
     */
    @SuppressLint("CheckResult")
    public static void checkSmsPermissions(final FragmentActivity activity,
                                           final PermissionCallBack callBack) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        //requestEach   把每一个权限的名称和申请结果都列出来
        rxPermissions
                .requestEach(SMS)
                .subscribe(new Consumer<Permission>() {
                    @Override
                    public void accept(Permission permission) throws Exception {
                        boolean shouldShowRequestPermissionRationale =
                                permission.shouldShowRequestPermissionRationale;
                        if (permission.granted) {
                            // 用户已经同意该权限
                            callBack.onPermissionGranted(activity);
                        } else if (shouldShowRequestPermissionRationale) {
                            // 用户拒绝了该权限，没有选中『不再询问』（Never ask again）
                            // 那么下次再次启动时。还会提示请求权限的对话框
                            callBack.onPermissionDenied(activity, PermissionCallBack.DEFEATED);
                        } else {
                            // 用户拒绝了该权限，而且选中『不再询问』那么下次启动时，就不会提示出来了，
                            callBack.onPermissionDenied(activity, PermissionCallBack.REFUSE);
                        }
                    }
                });
    }

    /**
     * 检测电话权限
     * @param activity                          activity
     * @param callBack                          回调callBack
     */
    @SuppressLint("CheckResult")
    public static void checkPhonePermissions(final FragmentActivity activity,
                                             final PermissionCallBack callBack) {
        RxPermissions rxPermissions = new RxPermissions(activity);
        //request    不支持返回权限名，返回的权限结果:全部同意时值true,否则值为false
        rxPermissions
                .request(PHONE)
                .subscribe(new Consumer<Boolean>() {
                    @Override
                    public void accept(Boolean aBoolean) throws Exception {
                        if (aBoolean) {
                            //已经获得权限
                            callBack.onPermissionGranted(activity);
                        } else {
                            //用户拒绝开启权限，且选了不再提示时，才会走这里，否则会一直请求开启
                            callBack.onPermissionDenied(activity, PermissionCallBack.REFUSE);
                        }
                    }
                });
    }


    /**
     * 拨打电话
     * @param activity                              上下文
     * @param telephoneNumber                       电话
     */
    public static void callPhone(final FragmentActivity activity, final String telephoneNumber) {
        if (activity == null || telephoneNumber == null || telephoneNumber.length() == 0) {
            return;
        }
        checkPhonePermissions(activity, new PermissionCallBack() {
            @Override
            public void onPermissionGranted(Context context) {
                Intent intent = new Intent(Intent.ACTION_CALL, Uri.parse("tel:" + telephoneNumber));
                intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK);
                activity.startActivity(intent);
            }

            @Override
            public void onPermissionDenied(Context context, int type) {

            }
        });
    }

}
