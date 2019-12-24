package com.ns.yc.yccustomtextlib.edit.style;


import com.ns.yc.yccustomtextlib.edit.span.UnderLineSpan;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 下划线
 *     revise:
 * </pre>
 */
public class UnderlineStyle extends NormalStyle<UnderLineSpan> {
    @Override
    protected UnderLineSpan newSpan() {
        return new UnderLineSpan();
    }
}
