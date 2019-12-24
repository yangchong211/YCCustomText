package com.ns.yc.yccustomtextlib.edit.span;

import android.graphics.Typeface;
import android.text.style.UnderlineSpan;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 下划线
 *     revise:
 * </pre>
 */
public class UnderLineSpan extends UnderlineSpan implements InterInlineSpan {

    private String type;

    public UnderLineSpan() {
        type = RichTypeEnum.UNDERLINE;
    }

    @Override
    public String getType() {
        return type;
    }

}
