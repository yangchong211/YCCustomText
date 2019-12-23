package com.ns.yc.yccustomtextlib.web;

import java.util.List;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 状态变化监听
 *     revise:
 * </pre>
 */
public interface OnDecorationListener {

    /**
     * 状态变化监听事件
     * @param text                      文字内容
     * @param types                     类型
     */
    void onStateChangeListener(String text, List<WebRichType> types);

}