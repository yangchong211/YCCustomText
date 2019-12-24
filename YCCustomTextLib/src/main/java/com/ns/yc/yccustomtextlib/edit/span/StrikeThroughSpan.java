package com.ns.yc.yccustomtextlib.edit.span;

import android.text.style.StrikethroughSpan;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 删除线
 *     revise:
 * </pre>
 */
public class StrikeThroughSpan extends StrikethroughSpan implements InterInlineSpan {

    private String type;

    public StrikeThroughSpan() {
        type = RichTypeEnum.STRIKE_THROUGH;
    }

    @Override
    public String getType() {
        return type;
    }
}
