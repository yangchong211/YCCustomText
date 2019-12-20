package com.ns.yc.yccustomtextlib.inter;

import android.view.View;

/**
 * <pre>
 *     @author yangchong
 *     blog  : https://github.com/yangchong211/YCStatusBar
 *     time  : 2017/5/18
 *     desc  : 自定义点击和删除事件接口
 *     revise:
 * </pre>
 */
public interface OnHyperTextListener {
    /**
     * 图片点击事件
     * @param view                  view
     * @param imagePath             图片地址
     */
    void onImageClick(View view, String imagePath);
}
