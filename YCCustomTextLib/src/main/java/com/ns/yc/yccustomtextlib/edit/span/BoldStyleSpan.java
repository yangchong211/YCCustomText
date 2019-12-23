package com.ns.yc.yccustomtextlib.edit.span;

import android.graphics.Typeface;
import android.text.style.StyleSpan;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 加粗
 *     revise:
 * </pre>
 */
public class BoldStyleSpan extends StyleSpan implements IInlineSpan {

    private String type;

    public BoldStyleSpan() {
        super(Typeface.BOLD);
        type = RichTypeEnum.BOLD;
    }

    @Override
    public String getType() {
        return type;
    }

}
