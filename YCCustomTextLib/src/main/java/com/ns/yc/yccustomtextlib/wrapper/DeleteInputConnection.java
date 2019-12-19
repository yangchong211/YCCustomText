package com.ns.yc.yccustomtextlib.wrapper;

import android.view.KeyEvent;
import android.view.inputmethod.InputConnection;
import android.view.inputmethod.InputConnectionWrapper;

import com.ns.yc.yccustomtextlib.utils.HyperLogUtils;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/07/18
 *     desc  : 自定义InputConnectionWrapper
 *     revise:
 * </pre>
 */
public class DeleteInputConnection extends InputConnectionWrapper {

    public DeleteInputConnection(InputConnection target, boolean mutable) {
        super(target, mutable);
    }

    @Override
    public boolean sendKeyEvent(KeyEvent event) {
        HyperLogUtils.d("DeletableEditText---sendKeyEvent--");
        return super.sendKeyEvent(event);
    }

    @Override
    public boolean deleteSurroundingText(int beforeLength, int afterLength) {
        HyperLogUtils.d("DeletableEditText---deleteSurroundingText--"+beforeLength+"----"+afterLength);
        if (beforeLength == 1 && afterLength == 0) {
            return sendKeyEvent(new KeyEvent(KeyEvent.ACTION_DOWN, KeyEvent.KEYCODE_DEL))
                    && sendKeyEvent(new KeyEvent(KeyEvent.ACTION_UP, KeyEvent.KEYCODE_DEL));
        }
        return super.deleteSurroundingText(beforeLength, afterLength);
    }

}
