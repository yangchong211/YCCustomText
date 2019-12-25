package com.ns.yc.yccustomtextlib.edit.style;

import com.ns.yc.yccustomtextlib.edit.span.BoldItalicSpan;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 加粗斜体
 *     revise:
 * </pre>
 */
public class BoldItalicStyle extends NormalStyle<BoldItalicSpan> {

    @Override
    protected BoldItalicSpan newSpan() {
        return new BoldItalicSpan();
    }
}
