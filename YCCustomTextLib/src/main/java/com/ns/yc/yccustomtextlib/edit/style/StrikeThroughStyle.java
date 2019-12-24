package com.ns.yc.yccustomtextlib.edit.style;

import com.ns.yc.yccustomtextlib.edit.span.StrikeThroughSpan;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 删除线
 *     revise:
 * </pre>
 */
public class StrikeThroughStyle extends NormalStyle<StrikeThroughSpan> {
    @Override
    protected StrikeThroughSpan newSpan() {
        return new StrikeThroughSpan();
    }
}
