package com.ns.yc.yccustomtextlib.manager;

import android.widget.ImageView;

import com.ns.yc.yccustomtextlib.inter.ImageLoader;

public class HyperManager {

    private static HyperManager instance;
    private ImageLoader imageLoader;

    public static HyperManager getInstance(){
        if (instance == null){
            synchronized (HyperManager.class){
                if (instance == null){
                    instance = new HyperManager();
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
