package com.ns.yc.yccustomtextlib.hyper;

import android.content.Context;
import android.graphics.Bitmap;
import android.util.AttributeSet;
import android.widget.ImageView;


/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2016/4/5
 * 描    述：
 * 修订历史：
 * ================================================
 */
public class HyperImageView extends ImageView {

    private String absolutePath;
    private Bitmap bitmap;

    public HyperImageView(Context context) {
        this(context, null);
    }

    public HyperImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HyperImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

}
