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
import com.ns.yc.yccustomtextlib.edit.view.HyperImageView;

/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/3/31
 *     desc  : 图片loader管理者，使用单利
 *     revise: 暴露给开发者调用，将请求图片的过程暴露给开发者，便于维护
 * </pre>
 */
public final class HyperManager {

    private static volatile HyperManager instance;
    private ImageLoader imageLoader;

    /**
     * 使用dcl双重校验方式获取单利对象
     * 优点：在并发量不多，安全性不高的情况下或许能很完美运行单例模式
     * 缺点：不同平台编译过程中可能会存在严重安全隐患。
     *      在JDK1.5之后，官方给出了volatile关键字可以解决，多线程下执行顺序
     * @return                          返回对象
     */
    public static HyperManager getInstance(){
        //第一层判断是为了避免不必要的同步
        if (instance == null){
            synchronized (HyperManager.class){
                //第二层的判断是为了在null的情况下才创建实例
                if (instance == null){
                    instance = new HyperManager();
                }
            }
        }
        return instance;
    }

    /*
     * 利用面向对象思想，将图片的设置和加载都使用manager完成，让代码逻辑和业务逻辑分离
     */


    /**
     * 设置加载loader
     * @param imageLoader                   loader
     */
    public void setImageLoader(ImageLoader imageLoader){
        this.imageLoader = imageLoader;
    }

    /**
     * 设置图片
     * @param imagePath                     地址
     * @param imageView                     图片view
     * @param imageHeight                   图片高度
     */
    public void loadImage(String imagePath, HyperImageView imageView, int imageHeight){
        if (imageLoader != null){
            imageLoader.loadImage(imagePath, imageView, imageHeight);
        }
    }

}
