package com.ns.yc.yccustomtextlib.web;

import java.util.List;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 日志工具类
 *     revise:
 * </pre>
 */
public interface OnDecorationListener {

    void onStateChangeListener(String text, List<WebRichType> types);

}