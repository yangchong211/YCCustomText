package com.ns.yc.yccustomtextlib.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;

import com.ns.yc.yccustomtextlib.R;
import com.ns.yc.yccustomtextlib.inter.OnHyperTextListener;
import com.ns.yc.yccustomtextlib.manager.HyperManager;
import com.ns.yc.yccustomtextlib.utils.HyperLibUtils;
import com.ns.yc.yccustomtextlib.utils.HyperLogUtils;

import java.util.ArrayList;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/3/31
 *     desc  : 显示富文本
 *     revise:
 * </pre>
 */
public class HyperTextView extends ScrollView {

    /**
     * 常规padding是10dp
     */
    private static final int EDIT_PADDING = 10;
    /**
     * 新生的view都会打一个tag，对每个view来说，这个tag是唯一的
     */
    private int viewTagIndex = 1;
    /**
     * 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
     */
    private LinearLayout allLayout;
    private LayoutInflater inflater;
    private int editNormalPadding = 0;
    /**
     * 图片点击事件
     */
    private OnClickListener btnListener;
    /**
     * 图片地址集合
     */
    private ArrayList<String> imagePaths;
    /**
     * 关键词高亮
     */
    private String keywords;
    private OnHyperTextListener onHyperTextListener;
    /**
     * 插入的图片显示高度，为0显示原始高度
     */
    private int rtImageHeight = 0;
    /**
     * 两张相邻图片间距
     */
    private int rtImageBottom = 10;
    /**
     * 文字相关属性，初始提示信息，文字大小和颜色
     */
    private String rtTextInitHint = "没有内容";
    /**
     * 相当于16sp
     */
    private int rtTextSize = 16;
    private int rtTextColor = Color.parseColor("#757575");
    /**
     * 相当于8dp
     */
    private int rtTextLineSpace = 8;

    @Override
    protected void onDetachedFromWindow() {
        super.onDetachedFromWindow();
        HyperLogUtils.d("HyperTextView----onDetachedFromWindow------");
    }

    public HyperTextView(Context context) {
        this(context, null);
    }

    public HyperTextView(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HyperTextView(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        imagePaths = new ArrayList<>();
        inflater = LayoutInflater.from(context);
        //获取自定义属性
        initAttrs(context,attrs);
        initLayoutView(context);
        initListener();
        initFirstTextView(context);
    }

    /**
     * 初始化自定义属性
     * @param context						context上下文
     * @param attrs							attrs属性
     */
    private void initAttrs(Context context, AttributeSet attrs) {
        TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HyperTextView);
        rtImageHeight = ta.getInteger(R.styleable.HyperTextView_ht_view_image_height, 0);
        rtImageBottom = ta.getInteger(R.styleable.HyperTextView_ht_view_image_bottom, 10);
        rtTextSize = ta.getDimensionPixelSize(R.styleable.HyperTextView_ht_view_text_size, 16);
        rtTextLineSpace = ta.getDimensionPixelSize(R.styleable.HyperTextView_ht_view_text_line_space, 8);
        rtTextColor = ta.getColor(R.styleable.HyperTextView_ht_view_text_color, Color.parseColor("#757575"));
        rtTextInitHint = ta.getString(R.styleable.HyperTextView_ht_view_text_init_hint);
        ta.recycle();
    }


    private void initLayoutView(Context context) {
        // 1. 初始化allLayout
        allLayout = new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        //allLayout.setBackgroundColor(Color.WHITE);//去掉背景
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        //设置间距，防止生成图片时文字太靠边
        allLayout.setPadding(50,15,50,15);
        addView(allLayout, layoutParams);
    }


    private void initListener() {
        btnListener = new OnClickListener() {
            @Override
            public void onClick(View v) {
                if (v instanceof HyperImageView){
                    HyperImageView imageView = (HyperImageView) v;
                    //int currentItem = imagePaths.indexOf(imageView.getAbsolutePath());
                    //Toast.makeText(getContext(),"点击图片："+currentItem+"："+imageView.getAbsolutePath(), Toast.LENGTH_SHORT).show();
                    // 开放图片点击接口
                    if (onHyperTextListener != null){
                        onHyperTextListener.onImageClick(imageView, imageView.getAbsolutePath());
                    }
                }
            }
        };
    }


    private void initFirstTextView(Context context) {
        LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
                LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //editNormalPadding = dip2px(EDIT_PADDING);
        int padding = HyperLibUtils.dip2px(context, EDIT_PADDING);
        TextView firstText = createTextView(rtTextInitHint,padding);
        allLayout.addView(firstText, firstEditParam);
    }

    public void setOnHyperTextListener(OnHyperTextListener onRtImageClickListener) {
        this.onHyperTextListener = onRtImageClickListener;
    }

    /**
     * 清除所有的view
     */
    public void clearAllLayout(){
        if (allLayout!=null){
            allLayout.removeAllViews();
        }
    }

    /**
     * 获得最后一个子view的位置
     */
    public int getLastIndex(){
        if (allLayout!=null){
            int lastEditIndex = allLayout.getChildCount();
            return lastEditIndex;
        }
        return -1;
    }

    /**
     * 生成文本输入框
     */
    public TextView createTextView(String hint, int paddingTop) {
        TextView textView = new TextView(getContext());
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        textView.setLayoutParams(layoutParams);
        textView.setTextSize(16);
        textView.setTextColor(Color.parseColor("#616161"));
        textView.setTextIsSelectable(true);
        textView.setBackground(null);
        textView.setTag(viewTagIndex++);
        textView.setPadding(editNormalPadding, paddingTop, editNormalPadding, paddingTop);
        textView.setHint(hint);
        textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, rtTextSize);
        textView.setLineSpacing(rtTextLineSpace, 1.0f);
        textView.setTextColor(rtTextColor);
        return textView;
    }

    /**
     * 生成图片View
     */
    private RelativeLayout createImageLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.edit_imageview, null);
        layout.setTag(viewTagIndex++);
        View closeView = layout.findViewById(R.id.image_close);
        closeView.setVisibility(GONE);
        HyperImageView imageView = layout.findViewById(R.id.edit_imageView);
		imageView.setOnClickListener(btnListener);
        return layout;
    }


    public void setKeywords(String keywords) {
        this.keywords = keywords;
    }

    /**
     * 在特定位置插入EditText
     *
     * @param index 位置
     * @param editStr EditText显示的文字
     */
    public void addTextViewAtIndex(final int index, CharSequence editStr) {
        try {
            TextView textView = createTextView("", EDIT_PADDING);
            if (!TextUtils.isEmpty(keywords)) {
                //搜索关键词高亮
                SpannableStringBuilder textStr = HyperLibUtils.highlight(
                        editStr.toString(), keywords,Color.parseColor("#EE5C42"));
                textView.setText(textStr);
            } else {
                textView.setText(editStr);
            }
            allLayout.addView(textView, index);
        } catch (Exception e) {
            e.printStackTrace();
        }
    }

    /**
     * 在特定位置添加ImageView
     */
    public void addImageViewAtIndex(final int index, final String imagePath) {
        if (TextUtils.isEmpty(imagePath)){
            return;
        }
        imagePaths.add(imagePath);
        RelativeLayout imageLayout = createImageLayout();
        if (imageLayout == null){
            return;
        }
        final HyperImageView imageView = imageLayout.findViewById(R.id.edit_imageView);
        imageView.setAbsolutePath(imagePath);
        HyperManager.getInstance().loadImage(imagePath, imageView, rtImageHeight);
        // onActivityResult无法触发动画，此处post处理
        allLayout.addView(imageLayout, index);
    }

    /**
     * 在特定位置添加ImageView，折行
     */
    public void addImageViewAtIndex(final int index, String imagePath , boolean isWordWrap) {
        if(imagePath==null || imagePath.length()==0){
            return;
        }
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        final RelativeLayout imageLayout = createImageLayout();
        HyperImageView imageView = imageLayout.findViewById(R.id.edit_imageView);
        //Picasso.with(getContext()).load(imagePath).centerCrop().into(imageView);
        //Glide.with(getContext()).load(imagePath).crossFade().centerCrop().into(imageView);
        //imageView.setImageBitmap(bmp);    //
        //imageView.setBitmap(bmp);         //这句去掉，保留下面的图片地址即可，优化图片占用
        imageView.setAbsolutePath(imagePath);
        // 调整imageView的高度
        int imageHeight = 500;
        if (bmp != null) {
            imageHeight = allLayout.getWidth() * bmp.getHeight() / bmp.getWidth();
            // 使用之后，还是回收掉吧
            bmp.recycle();
        }
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, imageHeight);
        lp.bottomMargin = 10;
        imageView.setLayoutParams(lp);
        allLayout.addView(imageLayout, index);
    }


    /**
     * 根据view的宽度，动态缩放bitmap尺寸
     *
     * @param width view的宽度
     */
    public Bitmap getScaledBitmap(String filePath, int width) {
        if (TextUtils.isEmpty(filePath)){
            return null;
        }
        BitmapFactory.Options options = null;
        try {
            options = new BitmapFactory.Options();
            options.inJustDecodeBounds = true;
            BitmapFactory.decodeFile(filePath, options);
            int sampleSize = options.outWidth > width ? options.outWidth / width + 1 : 1;
            options.inJustDecodeBounds = false;
            options.inSampleSize = sampleSize;
        } catch (Exception e) {
            e.printStackTrace();
        }
        return BitmapFactory.decodeFile(filePath, options);
    }

}
