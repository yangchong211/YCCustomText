package com.ns.yc.yccustomtextlib.edit.style;

import com.ns.yc.yccustomtextlib.edit.span.BoldStyleSpan;
/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 加粗
 *     revise:
 * </pre>
 */
public class BoldStyle extends NormalStyle<BoldStyleSpan> {

    @Override
    protected BoldStyleSpan newSpan() {
        return new BoldStyleSpan();
    }
}
