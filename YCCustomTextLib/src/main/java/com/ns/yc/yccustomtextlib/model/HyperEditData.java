package com.ns.yc.yccustomtextlib.model;

import java.io.Serializable;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/3/31
 *     desc  : 富文本实体类
 *     revise: 目前就支持简单的富文本，文字+图片
 * </pre>
 */
public class HyperEditData implements Serializable {

    /**
     * 富文本输入文字内容
     */
    private String inputStr;
    /**
     * 富文本输入图片地址
     */
    private String imagePath;

    public String getInputStr() {
        return inputStr;
    }

    public void setInputStr(String inputStr) {
        this.inputStr = inputStr;
    }

    public String getImagePath() {
        return imagePath;
    }

    public void setImagePath(String imagePath) {
        this.imagePath = imagePath;
    }
}
