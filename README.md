#### 自定义文本控件目录介绍
- **1.关于控件类型，共三种**
- **2.关于集成方法**
- **3.关于超文本控件使用方法**
- **4.关于密码控件使用方法**
- **5.关于截图说明**

## 1.关于控件类型，共三种
- 1.超文本，支持文字，图片混排模式
- 2.浮点标记编辑
- 3.密码控件，支持显示与隐藏


## 2.关于集成方法
- 首先在项目build.gradlew中添加
```
compile 'cn.yc:YCCustomTextLib:2.1'
```
## 3.关于超文本控件使用方法
- 3.1在布局中
```
<com.ns.yc.yccustomtextlib.t.HyperTextEditor
        android:id="@+id/et_new_content"
        android:layout_marginTop="10dp"
        android:layout_width="match_parent"
        android:layout_height="match_parent"
        android:background="#f5f5f7"
        android:textSize="16sp"
        android:textColor="@android:color/black"/>
```
- 3.2代码使用方法
```
//插入图片
et_new_content.insertImage(imagePath, et_new_content.getMeasuredWidth());
```
- 3.3具体的使用可以参考此案例
- 支持从相册选中图片并且插入，完整案例链接地址：https://github.com/yangchong211/LifeHelper









