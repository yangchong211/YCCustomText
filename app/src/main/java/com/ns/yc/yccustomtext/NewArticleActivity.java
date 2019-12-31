package com.ns.yc.yccustomtext;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.graphics.Bitmap;
import android.graphics.Color;
import android.net.Uri;
import android.os.Bundle;
import android.support.annotation.NonNull;
import android.support.annotation.Nullable;
import android.support.v4.app.FragmentActivity;
import android.support.v4.content.ContextCompat;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.util.Log;
import android.view.Menu;
import android.view.MenuItem;
import android.view.MotionEvent;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import com.bumptech.glide.Glide;
import com.bumptech.glide.request.target.SimpleTarget;
import com.bumptech.glide.request.transition.Transition;
import com.google.gson.Gson;
import com.ns.yc.yccustomtextlib.edit.inter.OnHyperChangeListener;
import com.ns.yc.yccustomtextlib.edit.manager.HyperManager;
import com.ns.yc.yccustomtextlib.edit.inter.ImageLoader;
import com.ns.yc.yccustomtextlib.edit.inter.OnHyperEditListener;
import com.ns.yc.yccustomtextlib.edit.inter.OnHyperTextListener;
import com.ns.yc.yccustomtextlib.edit.model.HyperEditData;
import com.ns.yc.yccustomtextlib.edit.view.HyperImageView;
import com.ns.yc.yccustomtextlib.utils.HyperHtmlUtils;
import com.ns.yc.yccustomtextlib.utils.HyperLibUtils;
import com.ns.yc.yccustomtextlib.edit.view.HyperTextEditor;
import com.ns.yc.yccustomtextlib.edit.view.HyperTextView;
import com.pedaily.yc.ycdialoglib.fragment.CustomDialogFragment;
import com.pedaily.yc.ycdialoglib.toast.ToastUtils;
import com.zhihu.matisse.Matisse;
import com.zhihu.matisse.MimeType;
import com.zhihu.matisse.internal.entity.CaptureStrategy;

import java.util.List;

import cn.ycbjie.ycstatusbarlib.bar.StateAppBar;
import io.reactivex.Observable;
import io.reactivex.ObservableEmitter;
import io.reactivex.ObservableOnSubscribe;
import io.reactivex.Observer;
import io.reactivex.android.schedulers.AndroidSchedulers;
import io.reactivex.disposables.Disposable;
import io.reactivex.schedulers.Schedulers;

public class NewArticleActivity extends AppCompatActivity {

    private static final int REQUEST_CODE_CHOOSE = 520;
    private Toolbar toolbar;
    private HyperTextEditor hte_content;
    private HyperTextView htv_content;
    private int screenWidth;
    private int screenHeight;
    private Disposable subsInsert;
    private Disposable mDisposable;


    @Override
    public void onBackPressed() {
        super.onBackPressed();
        //判断键盘是否弹出
        boolean softInputVisible = HyperLibUtils.isSoftInputVisible(this);
        if (softInputVisible){
            HyperLibUtils.hideSoftInput(this);
        }
    }

    @Override
    protected void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_new);
        StateAppBar.setStatusBarColor(this, ContextCompat.getColor(this, R.color.colorPrimary));
        toolbar = findViewById(R.id.toolbar);
        hte_content = findViewById(R.id.hte_content);
        htv_content = findViewById(R.id.htv_content);
        initToolBar();
        screenWidth = CommonUtil.getScreenWidth(this);
        screenHeight = CommonUtil.getScreenHeight(this);
        initListener();
        initHyper();
        //解决点击EditText弹出收起键盘时出现的黑屏闪现现象
        View rootView = hte_content.getRootView();
        rootView.setBackgroundColor(Color.WHITE);
        hte_content.postDelayed(new Runnable() {
            @Override
            public void run() {
                EditText lastFocusEdit = hte_content.getLastFocusEdit();
                lastFocusEdit.requestFocus();
                //打开软键盘显示
                //HyperLibUtils.openSoftInput(NewArticleActivity.this);
            }
        },300);
    }

    private void initListener() {
        final TextView tv_length = findViewById(R.id.tv_length);
        TextView tv_0_1 = findViewById(R.id.tv_0_1);
        TextView tv_0_2 = findViewById(R.id.tv_0_2);
        TextView tv_1 = findViewById(R.id.tv_1);
        TextView tv_2 = findViewById(R.id.tv_2);
        TextView tv_3 = findViewById(R.id.tv_3);
        TextView tv_4 = findViewById(R.id.tv_4);
        TextView tv_4_2 = findViewById(R.id.tv_4_2);
        TextView tv_5 = findViewById(R.id.tv_5);
        TextView tv_6 = findViewById(R.id.tv_6);
        tv_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加粗
                hte_content.bold();
            }
        });
        tv_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //下划线
                hte_content.underline();
            }
        });
        tv_3.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //斜体
                hte_content.italic();
            }
        });
        tv_4.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //删除线样式
                hte_content.strikeThrough();
            }
        });
        tv_4_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //加粗斜体
                hte_content.boldItalic();
            }
        });
        tv_0_1.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //编辑
                hte_content.setVisibility(View.VISIBLE);
                htv_content.setVisibility(View.GONE);
            }
        });

        tv_0_2.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //保存
                showDataSync(getEditData());
            }
        });
        tv_5.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                List<HyperEditData> editList = hte_content.buildEditData();
                //生成json
                Gson gson = new Gson();
                String content = gson.toJson(editList);
                //转化成json字符串
                String string = HyperHtmlUtils.stringToJson(content);
                Intent intent = new Intent(NewArticleActivity.this, TextActivity.class);
                intent.putExtra("content", string);
                startActivity(intent);
            }
        });
        tv_6.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                //生成html
            }
        });
        hte_content.setOnHyperListener(new OnHyperEditListener() {
            @Override
            public void onImageClick(View view, String imagePath) {
                //图片点击事件
                ToastUtils.showRoundRectToast("图片点击"+imagePath);
            }

            @Override
            public void onRtImageDelete(String imagePath) {
                //图片删除成功事件
                ToastUtils.showRoundRectToast("图片删除成功");
            }

            @Override
            public void onImageCloseClick(final View view) {
                //图片删除图片点击事件
                CustomDialogFragment
                        .create((NewArticleActivity.this).getSupportFragmentManager())
                        .setTitle("删除图片")
                        .setContent("确定要删除该图片吗?")
                        .setCancelContent("取消")
                        .setOkContent("确定")
                        .setDimAmount(0.5f)
                        .setOkColor(NewArticleActivity.this.getResources().getColor(R.color.color_000000))
                        .setCancelOutside(true)
                        .setCancelListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CustomDialogFragment.dismissDialogFragment();
                            }
                        })
                        .setOkListener(new View.OnClickListener() {
                            @Override
                            public void onClick(View v) {
                                CustomDialogFragment.dismissDialogFragment();
                                hte_content.onImageCloseClick(view);
                            }
                        })
                        .show();
            }
        });
        hte_content.setOnHyperChangeListener(new OnHyperChangeListener() {
            @Override
            public void onImageClick(int contentLength, int imageLength) {
                //富文本的文字数量，图片数量统计
                tv_length.setText("文字共"+contentLength+"个字，图片共"+imageLength+"张");
            }
        });
        htv_content.setOnHyperTextListener(new OnHyperTextListener() {
            @Override
            public void onImageClick(View view, String imagePath) {
                //图片点击事件
            }
        });

    }

    private void initToolBar() {
        setSupportActionBar(toolbar);
        toolbar.setTitle("新建富文本");
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_new, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.insert_image:
                //插入图片
                HyperLibUtils.hideSoftInput(this);
                PermissionUtils.checkWritePermissionsRequest(this, new PermissionCallBack() {
                    @Override
                    public void onPermissionGranted(Context context) {
                        callGallery();
                    }

                    @Override
                    public void onPermissionDenied(Context context, int type) {

                    }
                });
                break;
            case R.id.save:
                //保存
                showDataSync(getEditData());
                break;
            default:
                break;
        }
        return super.onOptionsItemSelected(item);
    }


    @Override
    protected void onStop() {
        super.onStop();
        try {
            if (subsInsert != null && subsInsert.isDisposed()){
                subsInsert.dispose();
            }
            if (mDisposable != null && !mDisposable.isDisposed()){
                mDisposable.dispose();
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, final Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        if (resultCode == RESULT_OK) {
            if (data != null) {
                if (requestCode == REQUEST_CODE_CHOOSE){
                    //异步方式插入图片
                    insertImagesSync(data);
                }
            }
        }
    }


    /**
     * 异步方式插入图片
     */
    private void insertImagesSync(final Intent data){
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                try{
                    hte_content.measure(0, 0);
                    List<Uri> mSelected = Matisse.obtainResult(data);
                    // 可以同时插入多张图片
                    for (Uri imageUri : mSelected) {
                        String imagePath = HyperLibUtils.getFilePathFromUri(NewArticleActivity.this,  imageUri);

                        Bitmap bitmap = HyperLibUtils.getSmallBitmap(imagePath, screenWidth, screenHeight);
                        //压缩图片
                        imagePath = SDCardUtil.saveToSdCard(bitmap);
                        //Log.e(TAG, "###imagePath="+imagePath);
                        emitter.onNext(imagePath);
                    }

                    // 测试插入网络图片 http://pics.sc.chinaz.com/files/pic/pic9/201904/zzpic17414.jpg
                    //emitter.onNext("http://pics.sc.chinaz.com/files/pic/pic9/201903/zzpic16838.jpg");
                    //emitter.onNext("http://b.zol-img.com.cn/sjbizhi/images/10/640x1136/1572123845476.jpg");
                    //emitter.onNext("https://img.ivsky.com/img/tupian/pre/201903/24/richu_riluo-013.jpg");
                    emitter.onComplete();
                }catch (Exception e){
                    e.printStackTrace();
                    emitter.onError(e);
                }
            }
        })
                //.onBackpressureBuffer()
                .subscribeOn(Schedulers.io())//生产事件在io
                .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onComplete() {
                        ToastUtils.showRoundRectToast("图片插入成功");
                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showRoundRectToast("图片插入失败:"+e.getMessage());
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        subsInsert = d;
                    }

                    @Override
                    public void onNext(String imagePath) {
                        hte_content.insertImage(imagePath);
                    }
                });
    }


    /**
     * 异步方式显示数据
     */
    private void showDataSync(final String html){
        if (html==null || html.length()==0){
            return;
        }
        htv_content.clearAllLayout();
        Observable.create(new ObservableOnSubscribe<String>() {
            @Override
            public void subscribe(ObservableEmitter<String> emitter) {
                showEditData(emitter, html);
            }
        })
                //.onBackpressureBuffer()
                .subscribeOn(Schedulers.io())//生产事件在io
                .observeOn(AndroidSchedulers.mainThread())//消费事件在UI线程
                .subscribe(new Observer<String>() {
                    @Override
                    public void onComplete() {

                    }

                    @Override
                    public void onError(Throwable e) {
                        ToastUtils.showRoundRectToast("解析错误：图片不存在或已损坏");
                    }

                    @Override
                    public void onSubscribe(Disposable d) {
                        mDisposable = d;
                    }

                    @Override
                    public void onNext(String text) {
                        try {
                            htv_content.setVisibility(View.VISIBLE);
                            hte_content.setVisibility(View.GONE);
                            if (htv_content !=null) {
                                if (text.contains("<img") && text.contains("src=")) {
                                    //imagePath可能是本地路径，也可能是网络地址
                                    String imagePath = HyperLibUtils.getImgSrc(text);
                                    htv_content.addImageViewAtIndex(htv_content.getLastIndex(), imagePath);
                                } else {
                                    htv_content.addTextViewAtIndex(htv_content.getLastIndex(), text);
                                }
                            }
                        } catch (Exception e) {
                            e.printStackTrace();
                        }
                    }
                });

    }

    /**
     * 显示数据
     */
    private void showEditData(ObservableEmitter<String> emitter, String html) {
        try {
            List<String> textList = HyperLibUtils.cutStringByImgTag(html);
            for (int i = 0; i < textList.size(); i++) {
                String text = textList.get(i);
                emitter.onNext(text);
            }
            emitter.onComplete();
        } catch (Exception e){
            e.printStackTrace();
            emitter.onError(e);
        }
    }



    /**
     * 负责处理编辑数据提交等事宜，请自行实现
     */
    private String getEditData() {
        StringBuilder content = new StringBuilder();
        try {
            List<HyperEditData> editList = hte_content.buildEditData();
            for (HyperEditData itemData : editList) {
                if (itemData.getInputStr() != null) {
                    content.append(itemData.getInputStr());
                } else if (itemData.getImagePath() != null) {
                    content.append("<img src=\"").append(itemData.getImagePath()).append("\"/>");
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
        }
        return content.toString();
    }


    /**
     * 调用图库选择
     */
    private void callGallery(){
        Matisse.from(this)
                .choose(MimeType.of(MimeType.JPEG, MimeType.PNG, MimeType.GIF))//照片视频全部显示MimeType.allOf()
                .countable(true)//true:选中后显示数字;false:选中后显示对号
                .maxSelectable(3)//最大选择数量为9
                //.addFilter(new GifSizeFilter(320, 320, 5 * Filter.K * Filter.K))
                .gridExpectedSize(this.getResources().getDimensionPixelSize(R.dimen.photo))//图片显示表格的大小
                .restrictOrientation(ActivityInfo.SCREEN_ORIENTATION_UNSPECIFIED)//图像选择和预览活动所需的方向
                .thumbnailScale(0.85f)//缩放比例
                .theme(R.style.Matisse_Zhihu)//主题  暗色主题 R.style.Matisse_Dracula
                .imageEngine(new MyGlideEngine())//图片加载方式，Glide4需要自定义实现
                .capture(true) //是否提供拍照功能，兼容7.0系统需要下面的配置
                //参数1 true表示拍照存储在共有目录，false表示存储在私有目录；参数2与 AndroidManifest中authorities值相同，用于适配7.0系统 必须设置
                .captureStrategy(new CaptureStrategy(true,"com.sendtion.matisse.fileprovider"))//存储到哪里
                .forResult(REQUEST_CODE_CHOOSE);//请求码
    }

    private void initHyper(){
        HyperManager.getInstance().setImageLoader(new ImageLoader() {
            @Override
            public void loadImage(final String imagePath, final HyperImageView imageView, final int imageHeight) {
                Log.e("---", "imageHeight: "+imageHeight);
                //如果是网络图片
                if (imagePath.startsWith("http://") || imagePath.startsWith("https://")){
                    Glide.with(getApplicationContext()).asBitmap()
                            .load(imagePath)
                            .dontAnimate()
                            .into(new SimpleTarget<Bitmap>() {
                                @Override
                                public void onResourceReady(@NonNull Bitmap resource, @Nullable Transition<? super Bitmap> transition) {
                                    if (imageHeight > 0) {//固定高度
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
                                        Glide.with(getApplicationContext()).asBitmap().load(imagePath).centerCrop()
                                                .placeholder(R.drawable.img_load_fail).error(R.drawable.img_load_fail).into(imageView);
                                    } else {//自适应高度
                                        Glide.with(getApplicationContext()).asBitmap().load(imagePath)
                                                .placeholder(R.drawable.img_load_fail).error(R.drawable.img_load_fail).into(new TransformationScale(imageView));
                                    }
                                }
                            });
                } else { //如果是本地图片
                    if (imageHeight > 0) {//固定高度
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

                        Glide.with(getApplicationContext()).asBitmap().load(imagePath).centerCrop()
                                .placeholder(R.drawable.img_load_fail).error(R.drawable.img_load_fail).into(imageView);
                    } else {//自适应高度
                        Glide.with(getApplicationContext()).asBitmap().load(imagePath)
                                .placeholder(R.drawable.img_load_fail).error(R.drawable.img_load_fail).into(new TransformationScale(imageView));
                    }
                }
            }
        });
    }

}
