package com.ns.yc.yccustomtextlib.edit.span;

import android.graphics.Typeface;
import android.text.style.StyleSpan;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 斜体
 *     revise:
 * </pre>
 */
public class ItalicStyleSpan extends StyleSpan implements InterInlineSpan {

    private String type;

    public ItalicStyleSpan() {
        super(Typeface.ITALIC);
        type = RichTypeEnum.ITALIC;
    }

    @Override
    public String getType() {
        return type;
    }
}
