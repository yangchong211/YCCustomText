#### 基础概念目录介绍
- 01.业务要求插入单个图片最高200，最小高度100
- 02.根据手机屏幕按照比例进行展示图片宽高
- 03.在富文本编辑页面携带数据传递到下一个页面注意点




### 01.业务要求插入单个图片最高200，最小高度100
- 直接展示代码如下所示
    ```
    Glide.with(getApplicationContext())
        .asBitmap()
        .load(imagePath)
        .centerCrop()
        .placeholder(R.drawable.img_load_fail)
        .error(R.drawable.img_load_fail)
        .into(new SimpleTarget<Bitmap>() {
            @Override
            public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                int width = resource.getWidth();
                int height = resource.getHeight();
                HyperLogUtils.d("本地图片--3--"+height+"----"+width);
                imageView.setImageBitmap(resource);
                ViewParent parent = imageView.getParent();
                int imageHeight = 0;
                //单个图片最高200，最小高度100，图片按高度宽度比例，通过改变夫布局来控制动态高度
                if (height> HyperLibUtils.dip2px(NewArticleActivity.this,200)){
                    if (parent instanceof RelativeLayout){
                        ViewGroup.LayoutParams layoutParams = ((RelativeLayout) parent).getLayoutParams();
                        layoutParams.height = HyperLibUtils.dip2px(NewArticleActivity.this,200);
                        ((RelativeLayout) parent).setLayoutParams(layoutParams);
                    } else if (parent instanceof FrameLayout){
                        ViewGroup.LayoutParams layoutParams = ((FrameLayout) parent).getLayoutParams();
                        layoutParams.height = HyperLibUtils.dip2px(NewArticleActivity.this,200);
                        ((FrameLayout) parent).setLayoutParams(layoutParams);
                        HyperLogUtils.d("本地图片--4--");
                    }
                    imageHeight = HyperLibUtils.dip2px(NewArticleActivity.this,200);
                } else if (height>HyperLibUtils.dip2px(NewArticleActivity.this,100)
                        && height<HyperLibUtils.dip2px(NewArticleActivity.this,200)){
                    //自是因高度
                    HyperLogUtils.d("本地图片--5--");
                    imageHeight = height;
                } else {
                    if (parent instanceof RelativeLayout){
                        ViewGroup.LayoutParams layoutParams = ((RelativeLayout) parent).getLayoutParams();
                        layoutParams.height = HyperLibUtils.dip2px(NewArticleActivity.this,100);
                        ((RelativeLayout) parent).setLayoutParams(layoutParams);
                    } else if (parent instanceof FrameLayout){
                        ViewGroup.LayoutParams layoutParams = ((FrameLayout) parent).getLayoutParams();
                        layoutParams.height = HyperLibUtils.dip2px(NewArticleActivity.this,100);
                        ((FrameLayout) parent).setLayoutParams(layoutParams);
                        HyperLogUtils.d("本地图片--6--");
                    }
                    imageHeight = HyperLibUtils.dip2px(NewArticleActivity.this,100);
                }
                //设置图片的属性，图片的底边距，以及图片的动态高度
                if (imageView.getLayoutParams() instanceof RelativeLayout.LayoutParams){
                    RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, imageHeight);//固定图片高度，记得设置裁剪剧中
                    lp.bottomMargin = 10;//图片的底边距
                    imageView.setLayoutParams(lp);
                } else if (imageView.getLayoutParams() instanceof FrameLayout.LayoutParams){
                    FrameLayout.LayoutParams lp = new FrameLayout.LayoutParams(
                            FrameLayout.LayoutParams.MATCH_PARENT, imageHeight);//固定图片高度，记得设置裁剪剧中
                    lp.bottomMargin = 10;//图片的底边距
                    imageView.setLayoutParams(lp);
                }
            }
        });
    ```


### 02.根据手机屏幕按照比例进行展示图片宽高
- 代码如下所示
    ```
    public class TransformationScale extends ImageViewTarget<Bitmap> {
    
        private ImageView target;
    
        public TransformationScale(ImageView target) {
            super(target);
            this.target = target;
        }
    
        @Override
        protected void setResource(Bitmap resource) {
            //设置图片
            target.setImageBitmap(resource);
            if (resource != null) {
                //获取原图的宽高
                int width = resource.getWidth();
                int height = resource.getHeight();
                //获取imageView的宽
                int imageViewWidth = target.getWidth();
                //计算缩放比例
                float sy = (float) (imageViewWidth * 0.1) / (float) (width * 0.1);
                //计算图片等比例放大后的高
                int imageHeight = (int) (height * sy);
                //ViewGroup.LayoutParams params = target.getLayoutParams();
                //params.height = imageHeight;
                //固定图片高度，记得设置裁剪剧中
                RelativeLayout.LayoutParams params = new RelativeLayout.LayoutParams(
                        RelativeLayout.LayoutParams.MATCH_PARENT, imageHeight);
                params.bottomMargin = 10;
                target.setLayoutParams(params);
            }
        }
    }
    ```


### 03.在富文本编辑页面携带数据传递到下一个页面注意点
- Intent 在 Activity 间传递基础类型数据或者可序列化的对象数据。但是 Intent 对数据大小是有限制的，当超过这个限制后，就会触发 TransactionTooLargeException 异常。
    ```
    /**
     * <pre>
     *     @author yangchong
     *     blog  : https://github.com/yangchong211
     *     time  : 2019/9/18
     *     desc  : 数据缓冲区，替代intent传递大数据方案
     *     revise:
     * </pre>
     */
    public class ModelStorage {
    
        private List<HyperEditData> hyperEditData = new ArrayList<>();
    
        public static ModelStorage getInstance(){
            return SingletonHolder.instance;
        }
    
        private static class SingletonHolder{
            private static final ModelStorage instance = new ModelStorage();
        }

        public List<HyperEditData> getHyperEditData() {
            return hyperEditData;
        }
    
        public void setHyperEditData(List<HyperEditData> hyperEditData) {
            this.hyperEditData.clear();
            this.hyperEditData.addAll(hyperEditData);
        }
    }
    ```
- 如何存数据，还有如何取数据呢？代码如下所示
    ```
    //存数据
    ModelStorage.getInstance().setHyperEditData(hyperEditData);
    //取数据
    List<HyperEditData> hyperEditData = ModelStorage.getInstance().getHyperEditData();
    ```






