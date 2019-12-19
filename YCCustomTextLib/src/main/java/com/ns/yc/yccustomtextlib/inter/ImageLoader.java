package com.ns.yc.yccustomtextlib.inter;

import android.widget.ImageView;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2018/3/31
 *     desc  : 接口
 *     revise: 暴露给开发者自定义设置图片
 * </pre>
 */
public interface ImageLoader {
    void loadImage(String imagePath, ImageView imageView, int imageHeight);
}
