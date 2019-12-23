package com.ns.yc.yccustomtextlib.web;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 加载结束监听
 *     revise:
 * </pre>
 */
public interface AfterInitialLoadListener {

    /**
     * 加载结束监听
     * @param isReady                   是否结束
     */
    void onAfterInitialLoad(boolean isReady);

}