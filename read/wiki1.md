#### 基础概念目录介绍
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




### 00.该控件介绍
- 自定义文本控件，支持富文本，包含两种状态：编辑状态和预览状态。编辑状态中，可以对插入本地或者网络图片，可以同时插入多张有序图片和删除图片，支持图文混排，并且可以对文字内容简单操作加粗字体，设置字体下划线，支持设置文字超链接(超链接支持跳转)，功能正在开发中和完善中……




### 01.业务需求简单介绍
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



### 02.实现的方案介绍
#### 2.0 页面构成分析
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





#### 2.2 第一种方案
- 使用ScrollView作为最外层，布局包含LineaLayout，图文混排内容，则是用TextView/EditText和ImageView去填充。
- 富文本编辑状态：ScrollView + LineaLayout + n个EditText + n个ImageView
- 富文本预览状态：ScrollView + LineaLayout + n个TextView + n个ImageView
- 删除的时候，根据光标的位置，如果光标遇到是图片，则可以用光标删除图片；如果光标遇到是文字，则可以用光标删除文字
- 当插入或者删除图片的时候，可以添加一个过渡动画效果，避免直接生硬的显示。如何在ViewGroup中添加view，删除view时给相应view和受影响的其他view添加动画，不太容易做。如果只是对受到影响的view添加动画，可以通过设置view的高度使之显示和隐藏，还可以利用ScrollView通过滚动隐藏和显示动画，但其他受影响的view则比较难处理，最终选择布局动画LayoutTransition 就可以很好地完成这个功能。




#### 2.3 第二种方法
- 使用WebView实现编辑器，支持n多格式，例如常见的html或者markdown格式。利用html标签对富文本处理，这种方式就需要专门处理标签的样式。
- 注意这种方法的实现，需要深入研究js，css等，必须非常熟悉才可以用到实际开发中，可以当作学习一下。这种方式对于图片的显示和上传，相比原生要麻烦一些。




### 03.异常状态下保存状态信息





### 04.处理软键盘回删按钮逻辑
- 想了一下，当富文本处于编辑的状态，利用光标可以进行删除插入点之前的字符。删除的时候，根据光标的位置，如果光标遇到是图片，则可以用光标删除图片；如果光标遇到是文字，则可以用光标删除文字。
- 更详细的来说，监听删除键的点击的逻辑需要注意，当光标在EditText 输入中间，点击删除不进行处理正常删除；当光标在EditText首端，判断前一个控件，如果是图片控件，删除图片控件，如果是输入控件，删除当前控件并将输入区域合并成一个输入区域。
- 创建一个键盘退格监听事件，代码如下所示：
    ```
    // 初始化键盘退格监听，主要用来处理点击回删按钮时，view的一些列合并操作
    keyListener = new OnKeyListener() {
        @Override
        public boolean onKey(View v, int keyCode, KeyEvent event) {
            //KeyEvent.KEYCODE_DEL    删除插入点之前的字符
            if (event.getAction() == KeyEvent.ACTION_DOWN && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                EditText edit = (EditText) v;
                //处于退格删除的逻辑
                onBackspacePress(edit);
            }
            return false;
        }
    };
    ```
- 然后针对退格删除，分为两种情况，第一种是删除图片，第二种是删除文字内容。具体代码如下所示：
    ```
    /**
     * 处理软键盘backSpace回退事件
     * @param editTxt 					光标所在的文本输入框
     */
    private void onBackspacePress(EditText editTxt) {
        try {
            int startSelection = editTxt.getSelectionStart();
            // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
            if (startSelection == 0) {
                int editIndex = layout.indexOfChild(editTxt);
                // 如果editIndex-1<0,
                View preView = layout.getChildAt(editIndex - 1);
                if (null != preView) {
                    if (preView instanceof RelativeLayout) {
                        // 光标EditText的上一个view对应的是图片，删除图片操作
                        onImageCloseClick(preView);
                    } else if (preView instanceof EditText) {
                        // 光标EditText的上一个view对应的还是文本框EditText
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ```



### 05.在指定位置插入图片
- 当点击插入图片的时候，需要思考两个问题。第一个是在那个位置插入图片，所以需要定位到这个位置；第二个是插入图片后，什么时候折行操作。
- 对于上面两个问题，这个位置可以取光标所在的位置，但是对于一个EditText输入文本，插入图片这个位置可以分多种情况：
    - 如果光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
    - 如果光标已经顶在了editText的最末端，则需要添加新的imageView
    - 如果光标已经顶在了editText的最中间，则需要分割字符串，分割成两个EditText，并在两个EditText中间插入图片
    - 如果当前获取焦点的EditText为空，直接在EditText下方插入图片，并且插入空的EditText
- 代码思路如下所示
    ```
    /**
     * 插入一张图片
     * @param imagePath							图片路径地址
     */
    public void insertImage(String imagePath) {
        if (TextUtils.isEmpty(imagePath)){
            return;
        }
        try {
            //lastFocusEdit获取焦点的EditText
            String lastEditStr = lastFocusEdit.getText().toString();
            //获取光标所在位置
            int cursorIndex = lastFocusEdit.getSelectionStart();
            //获取光标前面的字符串
            String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
            //获取光标后的字符串
            String editStr2 = lastEditStr.substring(cursorIndex).trim();
            //获取焦点的EditText所在位置
            int lastEditIndex = layout.indexOfChild(lastFocusEdit);
            if (lastEditStr.length() == 0) {
                //如果当前获取焦点的EditText为空，直接在EditText下方插入图片，并且插入空的EditText
            } else if (editStr1.length() == 0) {
                //如果光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
            } else if (editStr2.length() == 0) {
                // 如果光标已经顶在了editText的最末端，则需要添加新的imageView和EditText
            } else {
                //如果光标已经顶在了editText的最中间，则需要分割字符串，分割成两个EditText，并在两个EditText中间插入图片
            }
            hideKeyBoard();
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ```



### 06.在指定位置插入输入文字
- 前面已经提到了，如果一个富文本是：文字1+图片1+文字2+文字3+图片3+图片4，那么点击文字1控件则在此输入文字，点击文字3控件则在此输入文字。
- 所以，这样操作，确定处理记录当前的焦点区域位置十分重要。当前的编辑器已经添加了多个输入文本EditText，现在的问题在于需要记录当前编辑的EditText，在应用样式的时候定位到输入的控件，在编辑器中添加一个变量lastFocusEdit。具体可以看代码……
- 既然可以记录最后焦点输入文本，那么如何监听当前的输入控件呢，这就用到了OnFocusChangeListener，这个又是在哪里用到，具体如下面所示。要先setOnFocusChangeListener(focusListener) 再 requestFocus。
    ```
    /**
     * 所有EditText的焦点监听listener
     */
    private OnFocusChangeListener focusListener;
    
    
    focusListener = new OnFocusChangeListener() {
        @Override
        public void onFocusChange(View v, boolean hasFocus) {
            if (hasFocus) {
                lastFocusEdit = (EditText) v;
                HyperLogUtils.d("HyperTextEditor---onFocusChange--"+lastFocusEdit);
            }
        }
    };
    
	/**
	 * 在特定位置插入EditText
	 * @param index							位置
	 * @param editStr						EditText显示的文字
	 */
	public void addEditTextAtIndex(final int index, CharSequence editStr) {
	    //省略部分代码
		try {
			EditText editText = createEditText("插入文字", EDIT_PADDING);
			editText.setOnFocusChangeListener(focusListener);
			layout.addView(editText, index);
			//插入新的EditText之后，修改lastFocusEdit的指向
			lastFocusEdit = editText;
			//获取焦点
			lastFocusEdit.requestFocus();
			//将光标移至文字指定索引处
			lastFocusEdit.setSelection(editStr.length(), editStr.length());
		} catch (Exception e) {
			e.printStackTrace();
		}
	}
    ```



### 07.如果对选中文字加粗
- Span 的分类介绍
    - 字符外观，这种类型修改字符的外形但是不影响字符的测量，会触发文本重新绘制但是不触发重新布局。
        - ForegroundColorSpan，BackgroundColorSpan，UnderlineSpan，StrikethrougnSpan
    - 字符大小布局，这种类型Span会更改文本的大小和布局，会触发文本的重新测量绘制
        - StyleSpan，RelativeSizeSpan，AbsoluteSizeSpan
    - 影响段落级别，这种类型Span 在段落级别起作用，更改文本块在段落级别的外观，修改对齐方式，边距等。
        - AlignmentSpan，BulletSpan，QuoteSpan
- 实现基础样式 粗体、 斜体、 下划线 、中划线 的设置和取消。举个例子，对文本加粗，文字设置span样式注意要点，这里需要区分几种情况
- 当前选中区域不存在 bold 样式 这里我们选中BB。两种情况
    - 当前区域紧靠左侧或者右侧不存在粗体样式: AABBCC 这时候直接设置 span即可
    - 当前区域紧靠左侧或者右侧存在粗体样式如： AABBCC AABBCC AABBCC。这时候需要合并左右两侧的span，只剩下一个 span
- 当前选中区域存在了Bold 样式 选中 ABBC。四种情况：
    - 选中样式两侧不存在连续的bold样式 AABBCC
    - 选中内部两端存在连续的bold 样式 AABBCC
    - 选中左侧存在连续的bold 样式 AABBCC
    - 选中右侧存在连续的bold 样式 AABBCC
    - 这时候需要合并左右两侧已经存在的span，只剩下一个 span
- 接下来逐步分解，然后处理span的逻辑顺序如下所示
    - 首先对选中文字内容样式情况判断
    - 边界判断与设置
    - 取消Span




### 08.利用Span对文字属性处理



### 09.如何设置插入多张图片



### 10.如何设置插入网络图片



### 11.如何避免插入图片OOM
- 加载一个本地的大图片或者网络图片，从加载到设置到View上，如何减下内存，避免加载图片OOM。
    - 在展示高分辨率图片的时候，最好先将图片进行压缩。压缩后的图片大小应该和用来展示它的控件大小相近，在一个很小的ImageView上显示一张超大的图片不会带来任何视觉上的好处，但却会占用相当多宝贵的内存，而且在性能上还可能会带来负面影响。
- 加载图片的内存都去哪里呢？
    - 其实我们的内存就是去bitmap里了，BitmapFactory的每个decode函数都会生成一个bitmap对象，用于存放解码后的图像，然后返回该引用。如果图像数据较大就会造成bitmap对象申请的内存较多，如果图像过多就会造成内存不够用自然就会出现out of memory的现象。
- 为何容易OOM？
    - 通过BitmapFactory的decode的这些方法会尝试为已经构建的bitmap分配内存，这时就会很容易导致OOM出现。为此每一种解析方法都提供了一个可选的BitmapFactory.Options参数，将这个参数的inJustDecodeBounds属性设置为true就可以让解析方法禁止为bitmap分配内存，返回值也不再是一个Bitmap对象，而是null。
- 如何对图片进行压缩？
    - 1.解析图片，获取图片资源的属性
    - 2.计算图片的缩放值
    - 3.最后对图片进行质量压缩
- 具体设置图片压缩的代码如下所示
    ```
    public static Bitmap getSmallBitmap(String filePath, int newWidth, int newHeight) {
        final BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        // Calculate inSampleSize
        // 计算图片的缩放值
        options.inSampleSize = calculateInSampleSize(options, newWidth, newHeight);
        // Decode bitmap with inSampleSize set
        options.inJustDecodeBounds = false;
        Bitmap bitmap = BitmapFactory.decodeFile(filePath, options);
        // 质量压缩
        Bitmap newBitmap = compressImage(bitmap, 500);
        if (bitmap != null){
            //手动释放资源
            bitmap.recycle();
        }
        return newBitmap;
    }
    ```
- 思考：inJustDecodeBounds这个参数是干什么的？
    - 如果设置为true则表示decode函数不会生成bitmap对象，仅是将图像相关的参数填充到option对象里，这样我们就可以在不生成bitmap而获取到图像的相关参数了。
- 为何设置两次inJustDecodeBounds属性？
    - 第一次：设置为true则表示decode函数不会生成bitmap对象，仅是将图像相关的参数填充到option对象里，这样我们就可以在不生成bitmap而获取到图像的相关参数。
    - 第二次：将inJustDecodeBounds设置为false再次调用decode函数时就能生成bitmap了。而此时的bitmap已经压缩减小很多了，所以加载到内存中并不会导致OOM。




### 12.如何删除图片或者文字
- 当富文本处于编辑状态时，点击删除图片是可以删除图片的，对于删除的逻辑，封装的lib可以给开发者暴露一个删除的监听事件。注意删除图片有两种操作：第一种是利用光标删除，第二种是点击触发删除。删除图片后，不仅仅是要删除图片数据，而且还要删除图片ImageView控件。
    ```
    /**
     * 处理图片上删除的点击事件
     * 删除类型 0代表backspace删除 1代表按红叉按钮删除
     * @param view 							整个image对应的relativeLayout view
     */
    private void onImageCloseClick(View view) {
        try {
            //判断过渡动画是否结束，只能等到结束才可以操作
            if (!mTransition.isRunning()) {
                disappearingImageIndex = layout.indexOfChild(view);
                //删除文件夹里的图片
                List<HyperEditData> dataList = buildEditData();
                HyperEditData editData = dataList.get(disappearingImageIndex);
                if (editData.getImagePath() != null){
                    if (onHyperListener != null){
                        onHyperListener.onRtImageDelete(editData.getImagePath());
                    }
                    //SDCardUtil.deleteFile(editData.imagePath);
                    //从图片集合中移除图片链接
                    imagePaths.remove(editData.getImagePath());
                }
                //然后移除当前view
                layout.removeView(view);
                //合并上下EditText内容
                mergeEditText();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }
    ```


### 13.删除和插入图片添加动画
- 为什么要添加插入图片的过渡动画
    - 当向一个ViewGroup添加控件或者移除控件；这种场景虽然能够实现效果，并没有一点过度效果，直来直去的添加或者移除，显得有点生硬。有没有办法添加一定的过度效果，让实现的效果显得圆滑呢？
- LayoutTransition简单介绍
    - LayoutTransition类实际上Android系统中的一个实用工具类。使用LayoutTransition类在一个ViewGroup中对布局更改进行动画处理。
- 如何运用到插入或者删除图片场景中
    - 向一个ViewGroup添加控件或者移除控件，这两种效果的过程是应对应于控件的显示、控件添加时其他控件的位置移动、控件的消失、控件移除时其他控件的位置移动等四种动画效果。这些动画效果在LayoutTransition中，由以下四个关键字做出了相关声明：
        - APPEARING：元素在容器中显现时需要动画显示。
        - CHANGE_APPEARING：由于容器中要显现一个新的元素，其它元素的变化需要动画显示。
        - DISAPPEARING：元素在容器中消失时需要动画显示。
        - CHANGE_DISAPPEARING：由于容器中某个元素要消失，其它元素的变化需要动画显示。
    - 也就是说,ViewGroup中有多个ImageView对象，如果需要删除其中一个ImageView对象的话，该ImageView对象可以设置动画(即DISAPPEARING 动画形式)，ViewGroup中的其它ImageView对象此时移动到新的位置的过程中也可以设置相关的动画(即CHANGE_DISAPPEARING 动画形式)；
    - 若向ViewGroup中添加一个ImageView，ImageView对象可以设置动画(即APPEARING 动画形式)，ViewGroup中的其它ImageView对象此时移动到新的位置的过程中也可以设置相关的动画(即CHANGE_APPEARING 动画形式)。
    - 给ViewGroup设置动画很简单，只需要生成一个LayoutTransition实例，然后调用ViewGroup的setLayoutTransition（LayoutTransition）函数就可以了。当设置了布局动画的ViewGroup添加或者删除内部view时就会触发动画。
- 具体初始化动画的代码如下所示：
    ```
    mTransition = new LayoutTransition();
    mTransition.addTransitionListener(new LayoutTransition.TransitionListener() {
    
        @Override
        public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
        }
        
        @Override
        public void endTransition(LayoutTransition transition,
                ViewGroup container, View view, int transitionType) {
            if (!transition.isRunning() && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
                // transition动画结束，合并EditText
                 mergeEditText();
            }
        }
    });
    mTransition.enableTransitionType(LayoutTransition.APPEARING);
    mTransition.setDuration(300);
    layout.setLayoutTransition(mTransition);
    ```
- 有个问题需要注意一下，当控件销毁的时候，记得把监听给移除一下更好，代码如下所示
    ```
    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        if (mTransition!=null){
            //移除Layout变化监听
            mTransition.removeTransitionListener(transitionListener);
        }
    }
    ```
- 动画执行先后的顺序
    - 分析源码可以知道，默认情况下DISAPPEARING和CHANGE_APPEARING类型动画会立即执行，其他类型动画则会有个延迟。也就是说如果删除view，被删除的view将先执行动画消失，经过一些延迟受影响的view会进行动画补上位置，如果添加view，受影响的view将会先给添加的view腾位置执行CHANGE_APPEARING动画，经过一些时间的延迟才会执行APPEARING动画。这里就不贴分析源码的思路呢！



### 14.点击图片可以查看大图
- 编辑状态时，由于图片有空能比较大，在显示在富文本的时候，会裁剪局中显示，也就是图片会显示不全。那么后期如果是想添加点击图片查看，则需要暴露给开发者监听事件，需要考虑到后期拓展性，代码如下所示：
    ```
    // 图片删除图标叉掉处理
    btnListener = new OnClickListener() {
        @Override
        public void onClick(View v) {
            if (v instanceof HyperImageView){
                HyperImageView imageView = (HyperImageView)v;
                // 开放图片点击接口
                if (onHyperListener != null){
                    onHyperListener.onImageClick(imageView, imageView.getAbsolutePath());
                }
            } 
        }
    };
    ```



### 15.如何暴露设置文字属性方法
- 针对设置文字加粗，下划线，删除线等span属性。



### 16.文字中间添加图片注意事项
- 在文字中添加图片比较特殊，因此这里单独拿出来说一下。在文字内容中间插入图片，则需要分割字符串，分割成两个EditText，并在两个EditText中间插入图片，那么这个光标又定位在何处呢？
    - 对于光标前面的字符串保留，设置给当前获得焦点的EditText（此为分割出来的第一个EditText）
    - 把光标后面的字符串放在新创建的EditText中（此为分割出来的第二个EditText）
    - 在第二个EditText的位置插入一个空的EditText，以便连续插入多张图片时，有空间写文字，第二个EditText下移
    - 在空的EditText的位置插入图片布局，空的EditText下移。注意，这个过程添加动画过渡一下插入的效果比较好，不然会比较生硬
    ```
    //获取光标所在位置
    int cursorIndex = lastFocusEdit.getSelectionStart();
    //获取光标前面的字符串
    String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
    //获取光标后的字符串
    String editStr2 = lastEditStr.substring(cursorIndex).trim();
    
    lastFocusEdit.setText(editStr1);
    addEditTextAtIndex(lastEditIndex + 1, editStr2);
    addEditTextAtIndex(lastEditIndex + 1, "");
    addImageViewAtIndex(lastEditIndex + 1, imagePath);
    ```




### 17.键盘弹出和收缩优化



### 18.前后台切换编辑富文本优化
- 由于富文本中，用户会输入很多的内容，当关闭页面时候，需要提醒用户是否保存输入内容。同时，切换到后台的时候，需要注意保存输入内容，避免长时间切换后台进程内存吃紧，在回到前台输入的内容没有呢，查阅了简书，掘金等手机上的富文本编辑器，都会有这个细节点的优化。




### 19.合理运用面向对象编程思想
- 针对富文本插入图片操作
- 针对修改EditText文本内容加粗，下划线格式




### 20.用的设计模式介绍



### 21.生成html片段上传服务器
#### 21.1 提交富文本
- 客户端生成html片段到服务器
    - 在客户端提交帖子，文章。富文本包括图片，文字内容，还有文字span样式，同时会选择一些文章，帖子的标签。还有设置文章的类型，封面图，作者等许多属性。
    - 当点击提交的时候，客户端把这些数据，转化成html，还是转化成json对象提交给服务器呢？思考一下，会有哪些问题……
- 转化成html
    - 对于将单个富文本转化成html相对来说是比较容易的，因为富文本中之存在文字，图片等。转化成html细心就可以。
    - 但是对于设置富文本的标签，类型，作者，封面图，日期，其他关联属性怎么合并到html中呢，这个相对麻烦。
- 最后想说的是
    - 对于富文本写帖子，文章，如果写完富文本提交，则可以使用转化成html数据提交给服务器；
    - 对于富文本写完帖子，文章，还有下一步，设置标签，类型，封面图，作者，时间，还有其他属性，则可以使用转化成json数据提交给服务器；




#### 21.2 编辑富文本
- 服务器返回html给客户端加载
    - 涉及到富文本的加载，后台管理端编辑器生成的一段html 代码要渲染到移动端上面，一种方法是前端做成html页面，放到服务器上，移动端这边直接webView 加载url即可。
    - 还有一种后台接口直接返回这段html富文本的，String类型的，移动端直接加载的；具体的需求按实际情况而定。
- 加载html文件流畅问题
    - webView直接加载url体验上没那么流畅，相对的加载html文件会好点。但是对比原生，体验上稍微弱点。
    - 如果不用WebView，使用TextView显示html富文本，则会出现图片不显示，以及格式问题。
    - 如果不用WebView，使用自定义富文本RichText，则需要解析html显示，如果对html标签，js不熟悉，也不太好处理。
- 富文本编辑遇到的问题
    - 当富文本切换到编辑模式时，



### 22.生成json片段上传服务器
#### 22.1 提交富文本


#### 22.2 编辑富文本



### 23.图片上传策略问题思考
- 大多数开发者会采用的方式：
    - 先在编辑器里显示本地图片，等待用户编辑完成再上传全部图片，然后用上传返回的url替换之前html中显示本地图片的位置。
- 这样会遇到很多问题：
    - 如果图片很多，上传的数据量会很大，手机的网络状态经常不稳定，很容易上传失败。另外等待时间会很长，体验很差。
- 解决办法探讨：
    - 选图完成即上传，得到url之后直接插入，上传是耗时操作，再加上图片压缩的时间，这样编辑器显示图片会有可观的延迟时间，实际项目中可以加一个默认的占位图，另外加一个标记提醒用户是否上传完成，避免没有上传成功用户即提交的问题。
- 这种场景很容易想到：
    - 比如，在简书，掘金上写博客。写文章时，插入本地图片，即使你没有提交文章，也会把图片上传到服务器，然后返回一个图片链接给你，最后当你发表文章时，图片只需要用链接替代即可。



### 参考博客
- Android富文本编辑器（四）：HTML文本转换：https://www.jianshu.com/p/578085fb07d1
- Android 端 （图文混排）富文本编辑器的开发（一）：https://www.jianshu.com/p/155aa1e9f9d3
- 图文混排富文本文章编辑器实现详解：https://blog.csdn.net/ljzdyh/article/details/82497625








