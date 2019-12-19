package com.ns.yc.yccustomtextlib;

import android.widget.ImageView;

import com.ns.yc.yccustomtextlib.inter.ImageLoader;

public class XRichText {

    private static XRichText instance;
    private ImageLoader imageLoader;

    public static XRichText getInstance(){
        if (instance == null){
            synchronized (XRichText.class){
                if (instance == null){
                    instance = new XRichText();
                }
            }
        }
        return instance;
    }

    public void setImageLoader(ImageLoader imageLoader){
        this.imageLoader = imageLoader;
    }

    public void loadImage(String imagePath, ImageView imageView, int imageHeight){
        if (imageLoader != null){
            imageLoader.loadImage(imagePath, imageView, imageHeight);
        }
    }
}
