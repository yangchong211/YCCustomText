package com.ns.yc.yccustomtextlib.edit.style;


import com.ns.yc.yccustomtextlib.edit.span.ItalicStyleSpan;
/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 斜体
 *     revise:
 * </pre>
 */
public class ItalicStyle extends NormalStyle<ItalicStyleSpan> {
    @Override
    protected ItalicStyleSpan newSpan() {
        return new ItalicStyleSpan();
    }
}
