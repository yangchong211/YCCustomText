package com.ns.yc.yccustomtextlib.model;

import java.io.Serializable;

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
