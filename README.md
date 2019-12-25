#### 目录介绍
- 01.富文本控件介绍
- 02.业务需求简单介绍
- 03.富文本支持功能
- 04.富文本实现方案
- 05.富文本如何使用
- 06.富文本开发步骤
- 07.富文本遗留问题
- 08.其他说明介绍




### 01.富文本控件介绍
#### 1.1 富文本介绍
- 自定义文本控件，支持富文本，包含两种状态：编辑状态和预览状态。编辑状态中，可以对插入本地或者网络图片，可以同时插入多张有序图片和删除图片，支持图文混排，并且可以对文字内容简单操作加粗字体，设置字体下划线，支持设置文字超链接(超链接支持跳转)，支持字数和图片数量统计，功能正在开发中和完善中……


#### 1.2 富文本截图
![image](https://github.com/yangchong211/YCCustomText/blob/master/image/image1.jpeg)
![image](https://github.com/yangchong211/YCCustomText/blob/master/image/image2.jpeg)
![image](https://github.com/yangchong211/YCCustomText/blob/master/image/image3.jpeg)
![image](https://github.com/yangchong211/YCCustomText/blob/master/image/image4.jpeg)
![image](https://github.com/yangchong211/YCCustomText/blob/master/image/image5.jpeg)



### 02.业务需求简单介绍
- 富文本控件支持动态插入文字，图片等图文混排内容。图片可以支持本地图片，也支持插入网络链接图片；
- 富文本又两种状态：编辑状态 + 预览状态 。两种状态可以相互进行切换；
- 富文本在编辑状态，可以同时选择插入超过一张以上的多张图片，并且可以动态设置图片之间的top间距；
- 在编辑状态，支持利用光标删除文字内容，同时也支持用光标删除图片；
- 在编辑状态，插入图片后，图片的宽度填充满手机屏幕的宽度，然后高度可以动态设置，图片是剧中裁剪显示；
- 在编辑状态，插入图片后，如果本地图片过大，要求对图片进行质量压缩，大小压缩；
- 在编辑状态，插入多张图片时，添加插入过渡动画，避免显示图片生硬。结束后，光标移到插入图片中的最后一行显示；
- 编辑状态中，图片点击暴露点击事件接口，可以在4个边角位置动态设置一个删除图片的功能，点击删除按钮则删除图片；
- 连续插入多张图片时，比如顺序1，2，3，注意避免出现图片插入顺序混乱的问题(异步插入多张图片可能出现顺序错乱问题)；
- 在编辑富文本状态的时候，连续多张图片之间插入输入框，方便在图片间输入文本内容；
- 在编辑状态中，可以设置文字大小和颜色，同时做好拓展需求，后期可能添加文本加粗，下划线，插入超链接，对齐方式等功能；
- 编辑状态，连续插入多张图片，如果想在图片中间插入文字内容，则需要靠谱在图片之间预留编辑文本控件，方便操作；
- 支持对文字选中的内容进行设置加粗，添加下划线，改变颜色，设置对齐方式等等；



### 03.富文本支持功能
- 支持加粗、斜体、删除线、下划线行内样式，一行代码即可设置文本span属性，十分方便
- 支持添加单张或者多张图片，并且插入过渡动画友好，同时可以保证插入图片顺序
- 支持富文本编辑状态和预览状态的切换，支持富文本内容转化为json内容输出，转化为html内容输出
- 支持设置富文本的文字大小，行间距，图片和文本间距，以及插入图片的宽和高的属性
- 图片支持点击预览，支持点击叉号控件去除图片，暴露给外部开发者调用。同时加载图片的逻辑也是暴露给外部开发者，充分解耦
- 关于富文本字数统计，由于富文本中包括文字和图片，因此图片和文字数量统计分开。参考易车是：共n个文字，共n个图片显示



### 04.富文本实现方案
#### 4.0 页面构成分析
- 整个界面的要求
    - 整体界面可滚动，可以编辑，也可以预览
    - 内容可编辑可以插入文字、图片等。图片提供按钮操作
    - 软键盘删除键可删除图片，也可以删除文字内容
    - 文字可以修改属性，比如加粗，对齐，下划线
- 根据富文本作出以下分析
    - 使用原生控件，可插入图片、文字界面不能用一个EditText来做，需要使用LinearLayout添加不同的控件，图片部分用ImageView，界面可滑动最外层使用ScrollView。
    - 使用WebView+js+css方式，富文本格式用html方式展现，比较复杂，对标签要非常熟悉才可以尝试使用
- 使用原生控件多焦点问题分析
    - 界面是由多个输入区域拼接而成，暂且把输入区域称为EditText，图片区域称为ImageView，外层是LinearLayout。
    - 如果一个富文本是：文字1+图片1+文字2+文字3+图片3+图片4；那么使用LinearLayout包含多个EditText实现的难点：
        - 如何处理记录当前的焦点区域
        - 如何处理在文字区域的中间位置插入ImageView样式的拆分和合并
        - 如何处理输入区域的删除键处理 



#### 4.1 第一种方案
- 使用ScrollView作为最外层，布局包含LineaLayout，图文混排内容，则是用TextView/EditText和ImageView去填充。
- 富文本编辑状态：ScrollView + LineaLayout + n个EditText+Span + n个ImageView
- 富文本预览状态：ScrollView + LineaLayout + n个TextView+Span + n个ImageView
- 删除的时候，根据光标的位置，如果光标遇到是图片，则可以用光标删除图片；如果光标遇到是文字，则可以用光标删除文字
- 当插入或者删除图片的时候，可以添加一个过渡动画效果，避免直接生硬的显示。如何在ViewGroup中添加view，删除view时给相应view和受影响的其他view添加动画，不太容易做。如果只是对受到影响的view添加动画，可以通过设置view的高度使之显示和隐藏，还可以利用ScrollView通过滚动隐藏和显示动画，但其他受影响的view则比较难处理，最终选择布局动画LayoutTransition 就可以很好地完成这个功能。




#### 4.2 第二种方法
- 使用WebView实现编辑器，支持n多格式，例如常见的html或者markdown格式。利用html标签对富文本处理，这种方式就需要专门处理标签的样式。
- 注意这种方法的实现，需要深入研究js，css等，必须非常熟悉才可以用到实际开发中，可以当作学习一下。这种方式对于图片的显示和上传，相比原生要麻烦一些。



### 05.富文本如何使用
- 如何引用
```

```
- 在布局中引用，HyperTextEditor是编辑富文本，HyperTextView是预览富文本
    ```
    <?xml version="1.0" encoding="utf-8"?>
    <LinearLayout
        xmlns:android="http://schemas.android.com/apk/res/android"
        xmlns:tools="http://schemas.android.com/tools"
        android:orientation="vertical"
        xmlns:app="http://schemas.android.com/apk/res-auto"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        tools:ignore="MissingDefaultResource">
    
        <com.ns.yc.yccustomtextlib.edit.view.HyperTextEditor
            android:id="@+id/hte_content"
            android:visibility="visible"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            android:background="@android:color/white"
            android:textSize="16sp"
            app:editor_text_line_space="6dp"
            app:editor_image_height="500"
            app:editor_image_bottom="10"
            app:editor_text_init_hint="在这里输入内容"
            app:editor_text_size="16sp"
            app:editor_text_color="@android:color/black"/>
    
    
        <com.ns.yc.yccustomtextlib.edit.view.HyperTextView
            android:id="@+id/htv_content"
            android:visibility="gone"
            android:layout_width="match_parent"
            android:layout_height="match_parent"
            app:ht_view_text_line_space="6dp"
            app:ht_view_image_height="0"
            app:ht_view_image_bottom="10"
            app:ht_view_text_size="16sp"
            app:ht_view_text_color="@android:color/black"/>
    
    </LinearLayout>
    ```
- 在编辑富文本状态时，HyperTextEditor控件
    ```
    //插入图片
    hte_content.insertImage(imagePath);
    //对外提供的接口, 生成编辑数据上传
    List<HyperEditData> editList = hte_content.buildEditData();
    
    hte_content.setOnHyperListener(new OnHyperEditListener() {
        @Override
        public void onImageClick(View view, String imagePath) {
            //图片点击事件
        }

        @Override
        public void onRtImageDelete(String imagePath) {
            //图片删除事件
        }
    });
    hte_content.setOnHyperChangeListener(new OnHyperChangeListener() {
        @Override
        public void onImageClick(int contentLength, int imageLength) {
            //富文本的文字数量，图片数量统计
            tv_length.setText("文字共"+contentLength+"个字，图片共"+imageLength+"张");
        }
    });
    ```
- 在预览富文本状态时，HyperTextView控件
    ```
    //清除所有文本
    htv_content.clearAllLayout();
    //将html数据转化成集合
    List<String> textList = HyperLibUtils.cutStringByImgTag(html);
    //省略部分代码，具体看demo
    if (text.contains("<img") && text.contains("src=")) {
        //imagePath可能是本地路径，也可能是网络地址
        String imagePath = HyperLibUtils.getImgSrc(text);
        //在特定位置添加ImageView
        htv_content.addImageViewAtIndex(htv_content.getLastIndex(), imagePath);
    } else {
        //在特定位置插入TextView
        htv_content.addTextViewAtIndex(htv_content.getLastIndex(), text);
    }
    
    htv_content.setOnHyperTextListener(new OnHyperTextListener() {
        @Override
        public void onImageClick(View view, String imagePath) {
            //图片点击事件
        }
    });
    ```
- 关于HyperTextEditor的属性介绍说明
    ```
    <declare-styleable name="HyperTextEditor" >
        <!--父控件的左和右padding-->
        <attr name="editor_layout_right_left" format="integer" />
        <!--父控件的上和下padding-->
        <attr name="editor_layout_top_bottom" format="integer" />
        <!--插入的图片显示高度-->
        <attr name="editor_image_height" format="integer" />
        <!--两张相邻图片间距-->
        <attr name="editor_image_bottom" format="integer" />
        <!--文字相关属性，初始提示信息-->
        <attr name="editor_text_init_hint" format="string" />
        <!--文字大小-->
        <attr name="editor_text_size" format="dimension" />
        <!--文字颜色-->
        <attr name="editor_text_color" format="color" />
        <!--文字行间距-->
        <attr name="editor_text_line_space" format="dimension" />
    </declare-styleable>
    ```



### [06.富文本开发步骤](https://github.com/yangchong211/YCCustomText/blob/master/read/wiki1.md)
- 01.业务需求简单介绍
- 02.实现的方案介绍
- 03.异常状态下保存状态信息
- 04.处理软键盘回删按钮逻辑
- 05.在指定位置插入图片
- 06.在指定位置插入输入文字
- 07.如果对选中文字加粗
- 08.利用Span对文字属性处理
- 09.如何设置插入多张图片
- 10.如何设置插入网络图片
- 11.如何避免插入图片OOM
- 12.如何删除图片或者文字
- 13.删除和插入图片添加动画
- 14.点击图片可以查看大图
- 15.如何暴露设置文字属性方法
- 16.文字中间添加图片注意事项
- 17.键盘弹出和收缩优化
- 18.前后台切换编辑富文本优化
- 19.合理运用面向对象编程思想
- 20.用的设计模式介绍
- 21.生成html片段上传服务器
- 22.生成json片段上传服务器
- 23.图片上传策略问题思考
- [更多信息](https://github.com/yangchong211/YCCustomText/blob/master/read/wiki1.md)


### 07.富文本遗留问题




### 08.其他说明介绍
#### 关于其他内容介绍
![image](https://upload-images.jianshu.io/upload_images/4432347-7100c8e5a455c3ee.jpg?imageMogr2/auto-orient/strip%7CimageView2/2/w/1240)


#### 关于博客汇总链接
- 1.[技术博客汇总](https://www.jianshu.com/p/614cb839182c)
- 2.[开源项目汇总](https://blog.csdn.net/m0_37700275/article/details/80863574)
- 3.[生活博客汇总](https://blog.csdn.net/m0_37700275/article/details/79832978)
- 4.[喜马拉雅音频汇总](https://www.jianshu.com/p/f665de16d1eb)
- 5.[其他汇总](https://www.jianshu.com/p/53017c3fc75d)



#### 其他推荐
- 博客笔记大汇总【15年10月到至今】，包括Java基础及深入知识点，Android技术博客，Python学习笔记等等，还包括平时开发中遇到的bug汇总，当然也在工作之余收集了大量的面试题，长期更新维护并且修正，持续完善……开源的文件是markdown格式的！同时也开源了生活博客，从12年起，积累共计47篇[近100万字]，转载请注明出处，谢谢！
- 链接地址：https://github.com/yangchong211/YCBlogs
- 如果觉得好，可以star一下，谢谢！当然也欢迎提出建议，万事起于忽微，量变引起质变！



#### 关于LICENSE
```
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
```



