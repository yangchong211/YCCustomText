package com.ns.yc.yccustomtextlib.edit.span;

import android.text.Editable;
import android.widget.EditText;

import com.ns.yc.yccustomtextlib.edit.style.BoldItalicStyle;
import com.ns.yc.yccustomtextlib.edit.style.BoldStyle;
import com.ns.yc.yccustomtextlib.edit.style.ItalicStyle;
import com.ns.yc.yccustomtextlib.edit.style.StrikeThroughStyle;
import com.ns.yc.yccustomtextlib.edit.style.UnderlineStyle;
import com.ns.yc.yccustomtextlib.utils.HyperLogUtils;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2019/12/24
 *     desc  : span帮助工具类
 *     revise:
 * </pre>
 */
public class SpanTextHelper {

    private static volatile SpanTextHelper helper;

    public static SpanTextHelper getInstance(){
        if (helper == null){
            synchronized (SpanTextHelper.class){
                if (helper == null){
                    helper = new SpanTextHelper();
                }
            }
        }
        return helper;
    }

    /**
     * 修改加粗样式
     */
    public void bold(EditText lastFocusEdit) {
        //获取editable对象
        Editable editable = lastFocusEdit.getEditableText();
        //获取当前选中的起始位置
        int start = lastFocusEdit.getSelectionStart();
        //获取当前选中的末尾位置
        int end = lastFocusEdit.getSelectionEnd();
        HyperLogUtils.i("bold select  Start:" + start + "   end:  " + end);
        if (checkNormalStyle(start, end)) {
            return;
        }
        new BoldStyle().applyStyle(editable, start, end);
    }

    /**
     * 修改斜体样式
     */
    public void italic(EditText lastFocusEdit) {
        Editable editable = lastFocusEdit.getEditableText();
        int start = lastFocusEdit.getSelectionStart();
        int end = lastFocusEdit.getSelectionEnd();
        HyperLogUtils.i("italic select  Start:" + start + "   end:  " + end);
        if (checkNormalStyle(start, end)) {
            return;
        }
        new ItalicStyle().applyStyle(editable, start, end);
    }


    /**
     * 修改加粗斜体样式
     */
    public void boldItalic(EditText lastFocusEdit) {
        Editable editable = lastFocusEdit.getEditableText();
        int start = lastFocusEdit.getSelectionStart();
        int end = lastFocusEdit.getSelectionEnd();
        HyperLogUtils.i("boldItalic select  Start:" + start + "   end:  " + end);
        if (checkNormalStyle(start, end)) {
            return;
        }
        new BoldItalicStyle().applyStyle(editable, start, end);
    }


    /**
     * 修改删除线样式
     */
    public void strikeThrough(EditText lastFocusEdit) {
        Editable editable = lastFocusEdit.getEditableText();
        int start = lastFocusEdit.getSelectionStart();
        int end = lastFocusEdit.getSelectionEnd();
        HyperLogUtils.i("strikeThrough select  Start:" + start + "   end:  " + end);
        if (checkNormalStyle(start, end)) {
            return;
        }
        new StrikeThroughStyle().applyStyle(editable, start, end);
    }

    /**
     * 修改下划线样式
     */
    public void underline(EditText lastFocusEdit) {
        Editable editable = lastFocusEdit.getEditableText();
        int start = lastFocusEdit.getSelectionStart();
        int end = lastFocusEdit.getSelectionEnd();
        HyperLogUtils.i("underline select  Start:" + start + "   end:  " + end);
        if (checkNormalStyle(start, end)) {
            return;
        }
        new UnderlineStyle().applyStyle(editable, start, end);
    }

    /**
     * 判断是否是正常的样式
     * @param start                             start
     * @param end                               end
     * @return
     */
    private boolean checkNormalStyle(int start, int end) {
        if (start > end) {
            return true;
        }
        return false;
    }

}
