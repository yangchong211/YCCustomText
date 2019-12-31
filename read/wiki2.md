#### 基础概念目录介绍
- 01.业务要求插入单个图片最高200，最小高度100
- 02.根据手机屏幕按照比例进行展示图片宽高




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










