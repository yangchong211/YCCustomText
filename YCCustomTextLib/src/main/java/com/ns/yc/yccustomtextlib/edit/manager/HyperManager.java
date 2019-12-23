/*
Copyright 2017 yangchong211（github.com/yangchong211）

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
*/
package com.ns.yc.yccustomtextlib.edit.manager;

import android.widget.ImageView;
import com.ns.yc.yccustomtextlib.edit.inter.ImageLoader;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/3/31
 *     desc  : 图片loader管理者，使用单利
 *     revise: 暴露给开发者调用，将请求图片的过程暴露给开发者，便于维护
 * </pre>
 */
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
