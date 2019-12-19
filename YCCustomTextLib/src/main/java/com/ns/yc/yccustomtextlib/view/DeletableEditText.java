package com.ns.yc.yccustomtextlib.view;

import android.content.Context;
import android.support.v7.widget.AppCompatEditText;
import android.util.AttributeSet;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputConnection;

import com.ns.yc.yccustomtextlib.wrapper.DeleteInputConnection;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/3/31
 *     desc  : 自定义EditText
 *     revise: 主要用途是处理软键盘回删按钮backSpace时回调OnKeyListener
 * </pre>
 */
public class DeletableEditText extends AppCompatEditText {

    public DeletableEditText(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
    }

    public DeletableEditText(Context context, AttributeSet attrs) {
        super(context, attrs);
    }

    public DeletableEditText(Context context) {
        super(context);
    }

    @Override
    public InputConnection onCreateInputConnection(EditorInfo outAttrs) {
        return new DeleteInputConnection(super.onCreateInputConnection(outAttrs), true);
    }

}