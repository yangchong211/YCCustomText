package com.ns.yc.yccustomtextlib.model;

import java.io.Serializable;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/3/31
 *     desc  : 富文本实体类
 *     revise:
 * </pre>
 */
public class HyperEditData implements Serializable {

    private String inputStr;
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
