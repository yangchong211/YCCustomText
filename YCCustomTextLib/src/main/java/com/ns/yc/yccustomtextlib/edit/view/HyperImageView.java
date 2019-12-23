/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.ns.yc.yccustomtextlib.edit.view;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Rect;
import android.support.v7.widget.AppCompatImageView;
import android.util.AttributeSet;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/3/31
 *     desc  : 自定义ImageView
 *     revise: 可以存放Bitmap和Path等信息
 * </pre>
 */
public class HyperImageView extends AppCompatImageView {

    /**
     * 是否显示边框
     */
    private boolean showBorder = false;
    /**
     * 边框颜色
     */
    private int borderColor = Color.GRAY;
    /**
     * 边框大小
     */
    private int borderWidth = 5;
    /**
     * 文件绝对路径
     */
    private String absolutePath;
    /**
     * bitmap图片
     */
    private Bitmap bitmap;
    /**
     * 画笔
     */
    private Paint paint;

    public HyperImageView(Context context) {
        this(context, null);
    }

    public HyperImageView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HyperImageView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        initData();
    }

    private void initData() {
        //画笔
        paint = new Paint();
        //设置颜色
        paint.setColor(borderColor);
        //设置画笔的宽度
        paint.setStrokeWidth(borderWidth);
        //设置画笔的风格-不能设成填充FILL否则看不到图片
        paint.setStyle(Paint.Style.STROKE);
    }

    public String getAbsolutePath() {
        return absolutePath;
    }

    public void setAbsolutePath(String absolutePath) {
        this.absolutePath = absolutePath;
    }

    public boolean isShowBorder() {
        return showBorder;
    }

    public void setShowBorder(boolean showBorder) {
        this.showBorder = showBorder;
    }

    public Bitmap getBitmap() {
        return bitmap;
    }

    public void setBitmap(Bitmap bitmap) {
        this.bitmap = bitmap;
    }

    public int getBorderColor() {
        return borderColor;
    }

    public void setBorderColor(int borderColor) {
        this.borderColor = borderColor;
    }

    public int getBorderWidth() {
        return borderWidth;
    }

    public void setBorderWidth(int borderWidth) {
        this.borderWidth = borderWidth;
    }

    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        if (showBorder) {
            //画边框
            Rect rec = canvas.getClipBounds();
            // 这两句可以使底部和右侧边框更大
            //rec.bottom -= 2;
            //rec.right -= 2;
            canvas.drawRect(rec, paint);
        }
    }
}
