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





### 02.实现的方案介绍
- 使用ScrollView作为最外层，布局包含LineaLayout，图文混排内容，则是用TextView/EditText和ImageView去填充。
- 富文本编辑状态：ScrollView + LineaLayout + n个EditText + n个ImageView
- 富文本预览状态：ScrollView + LineaLayout + n个TextView + n个ImageView
- 删除的时候，根据光标的位置，如果光标遇到是图片，则可以用光标删除图片；如果光标遇到是文字，则可以用光标删除文字
- 当插入或者删除图片的时候，可以添加一个过渡动画效果，避免直接生硬的显示。如何在ViewGroup中添加view，删除view时给相应view和受影响的其他view添加动画，不太容易做。如果只是对受到影响的view添加动画，可以通过设置view的高度使之显示和隐藏，还可以利用ScrollView通过滚动隐藏和显示动画，但其他受影响的view则比较难处理，最终选择布局动画LayoutTransition 就可以很好地完成这个功能。




### 03.异常状态下保存状态信息



### 04.处理软键盘回删按钮逻辑
- 想了一下，当富文本处于编辑的状态，利用光标可以进行删除插入点之前的字符。删除的时候，根据光标的位置，如果光标遇到是图片，则可以用光标删除图片；如果光标遇到是文字，则可以用光标删除文字。
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



### 06.在指定位置插入输入文字


### 07.如果对选中文字加粗



### 08.利用Span对文字属性处理



### 09.如何设置插入多张图片


### 10.如何设置插入网络图片



### 11.如何避免插入图片OOM


### 12.如何删除图片或者文字




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



### 15.如何暴露设置文字属性方法



### 16.文字中间添加图片注意事项



### 17.键盘弹出和收缩优化



### 18.前后台切换编辑富文本优化



### 19.合理运用面向对象编程思想



### 20.用的设计模式介绍





### 参考项目和博客
- https://github.com/sendtion/XRichText
- https://github.com/chinalwb/Android-Rich-text-Editor












