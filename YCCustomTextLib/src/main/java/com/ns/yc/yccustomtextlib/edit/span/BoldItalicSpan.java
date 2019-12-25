package com.ns.yc.yccustomtextlib.edit.span;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 加粗斜体
 *     revise:
 * </pre>
 */
public class BoldItalicSpan extends StyleSpan implements InterInlineSpan {

    private String type;

    public BoldItalicSpan() {
        super(Typeface.BOLD_ITALIC);
        type = RichTypeEnum.BOLD_ITALIC;
    }

    @Override
    public String getType() {
        return type;
    }

}
