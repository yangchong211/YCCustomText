#### 基础概念目录介绍
- 01.业务要求插入单个图片最高200，最小高度100




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













