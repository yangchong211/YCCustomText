package com.ns.yc.yccustomtextlib;

import android.widget.ImageView;

import com.ns.yc.yccustomtextlib.inter.ImageLoader;

public class HyperRichText {

    private static HyperRichText instance;
    private ImageLoader imageLoader;

    public static HyperRichText getInstance(){
        if (instance == null){
            synchronized (HyperRichText.class){
                if (instance == null){
                    instance = new HyperRichText();
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
