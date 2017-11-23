package com.ns.yc.yccustomtextlib.hyper;

import android.animation.LayoutTransition;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.util.AttributeSet;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;

import com.bumptech.glide.Glide;
import com.ns.yc.yccustomtextlib.R;

import java.io.File;
import java.util.ArrayList;
import java.util.List;


/**
 * ================================================
 * 作    者：杨充
 * 版    本：1.0
 * 创建日期：2016/3/31
 * 描    述：显示富文本
 * 修订历史：可编辑富文本
 * ================================================
 */
public class HyperTextEditor extends ScrollView {

    private static final int EDIT_PADDING = 10;         // editText常规padding是10dp
    private int viewTagIndex = 1;                       // 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
    private LinearLayout allLayout;                     // 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
    private LayoutInflater inflater;
    private OnKeyListener keyListener;                  // 所有EditText的软键盘监听器
    private OnClickListener btnListener;                // 图片右上角红叉按钮监听器
    private OnFocusChangeListener focusListener;        // 所有EditText的焦点监听listener
    private EditText lastFocusEdit;                     // 最近被聚焦的EditText

    public HyperTextEditor(Context context) {
        this(context, null);
    }

    public HyperTextEditor(Context context, AttributeSet attrs) {
        this(context, attrs, 0);
    }

    public HyperTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
        super(context, attrs, defStyleAttr);
        init(context);
    }

    private void init(Context context) {
        inflater = LayoutInflater.from(context);
        // 1. 初始化allLayout
        allLayout = new LinearLayout(context);
        allLayout.setOrientation(LinearLayout.VERTICAL);
        //allLayout.setBackgroundColor(Color.WHITE);
        setupLayoutTransitions();
        LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
        allLayout.setPadding(dp2px(context,10), dp2px(context,15), dp2px(context,10), dp2px(context,15));       //设置间距，防止生成图片时文字太靠边，不能用margin，否则有黑边
        addView(allLayout, layoutParams);

        // 2. 初始化键盘退格监听
        // 主要用来处理点击回删按钮时，view的一些列合并操作
        keyListener = new OnKeyListener() {

            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (event.getAction() == KeyEvent.ACTION_DOWN
                        && event.getKeyCode() == KeyEvent.KEYCODE_DEL) {
                    EditText edit = (EditText) v;
                    onBackspacePress(edit);
                }
                return false;
            }
        };

        // 3. 图片叉掉处理
        btnListener = new OnClickListener() {

            @Override
            public void onClick(View v) {
                RelativeLayout parentView = (RelativeLayout) v.getParent();
                onImageCloseClick(parentView);
            }
        };

        focusListener = new OnFocusChangeListener() {

            @Override
            public void onFocusChange(View v, boolean hasFocus) {
                if (hasFocus) {
                    lastFocusEdit = (EditText) v;
                }
            }
        };

        LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
        //editNormalPadding = dip2px(EDIT_PADDING);
        EditText firstEdit = createEditText("请输入内容", dip2px(context, EDIT_PADDING));
        allLayout.addView(firstEdit, firstEditParam);
        lastFocusEdit = firstEdit;
    }

    /**
     * 初始化transition动画
     */
    private void setupLayoutTransitions() {
        // 只在图片View添加或remove时，触发transition动画
        LayoutTransition transition = new LayoutTransition();
        allLayout.setLayoutTransition(transition);
        transition.addTransitionListener(new LayoutTransition.TransitionListener() {

            @Override
            public void startTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {

            }

            @Override
            public void endTransition(LayoutTransition transition, ViewGroup container, View view, int transitionType) {
                if (!transition.isRunning() && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
                    // transition动画结束，合并EditText
                    // mergeEditText();
                }
            }
        });
        transition.setDuration(300);
    }

    public int dip2px(Context context, float dipValue) {
        float m = context.getResources().getDisplayMetrics().density;
        return (int) (dipValue * m + 0.5f);
    }

    /**
     * 处理软键盘backSpace回退事件
     * @param editTxt 光标所在的文本输入框
     */
    private void onBackspacePress(EditText editTxt) {
        int startSelection = editTxt.getSelectionStart();
        // 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
        if (startSelection == 0) {
            int editIndex = allLayout.indexOfChild(editTxt);
            View preView = allLayout.getChildAt(editIndex - 1); // 如果editIndex-1<0,
            // 则返回的是null
            if (null != preView) {
                if (preView instanceof RelativeLayout) {
                    // 光标EditText的上一个view对应的是图片
                    onImageCloseClick(preView);
                } else if (preView instanceof EditText) {
                    // 光标EditText的上一个view对应的还是文本框EditText
                    String str1 = editTxt.getText().toString();
                    EditText preEdit = (EditText) preView;
                    String str2 = preEdit.getText().toString();
                    allLayout.removeView(editTxt);
                    StringBuilder sb = new StringBuilder();
                    sb.append(str2);
                    sb.append(str1);
                    // 文本合并
                    preEdit.setText(sb.toString());
                    preEdit.requestFocus();
                    preEdit.setSelection(str2.length(), str2.length());
                    lastFocusEdit = preEdit;
                }
            }
        }
    }

    /**
     * 处理图片叉掉的点击事件
     *
     * @param view 整个image对应的relativeLayout view
     * @type 删除类型 0代表backspace删除 1代表按红叉按钮删除
     */
    private void onImageCloseClick(View view) {
        int disappearingImageIndex = allLayout.indexOfChild(view);
        //删除文件夹里的图片
        List<EditData> dataList = buildEditData();
        EditData editData = dataList.get(disappearingImageIndex);
        //Log.i("", "editData: "+editData);
        if (editData.imagePath != null) {
            deleteFile(editData.imagePath);
        }
        allLayout.removeView(view);
    }

    public void clearAllLayout() {
        allLayout.removeAllViews();
    }

    public int getLastIndex() {
        return allLayout.getChildCount();
    }

    /**
     * 生成文本输入框
     */
    public EditText createEditText(String hint, int paddingTop) {
        EditText editText = (EditText) inflater.inflate(R.layout.hyper_edit_text, null);
        editText.setOnKeyListener(keyListener);
        editText.setTag(viewTagIndex++);
        editText.setPadding(0, paddingTop, 0, paddingTop);
        editText.setHint(hint);
        editText.setOnFocusChangeListener(focusListener);
        return editText;
    }

    /**
     * 生成图片View
     */
    private RelativeLayout createImageLayout() {
        RelativeLayout layout = (RelativeLayout) inflater.inflate(R.layout.edit_image_view, null);
        layout.setTag(viewTagIndex++);
        View closeView = layout.findViewById(R.id.image_close);
        //closeView.setVisibility(GONE);
        closeView.setTag(layout.getTag());
        closeView.setOnClickListener(btnListener);
        return layout;
    }

    /**
     * 根据绝对路径添加view
     */
    public void insertImage(String imagePath, int width) {
        Bitmap bmp = getScaledBitmap(imagePath, width);
        insertImage(bmp, imagePath);
    }

    /**
     * 插入一张图片
     */
    public void insertImage(Bitmap bitmap, String imagePath) {
        String lastEditStr = lastFocusEdit.getText().toString();
        int cursorIndex = lastFocusEdit.getSelectionStart();
        String editStr1 = lastEditStr.substring(0, cursorIndex).trim();
        int lastEditIndex = allLayout.indexOfChild(lastFocusEdit);

        if (lastEditStr.length() == 0 || editStr1.length() == 0) {
            // 如果EditText为空，或者光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
            addImageViewAtIndex(lastEditIndex, imagePath);
        } else {
            // 如果EditText非空且光标不在最顶端，则需要添加新的imageView和EditText
            lastFocusEdit.setText(editStr1);
            String editStr2 = lastEditStr.substring(cursorIndex).trim();
            if (editStr2.length() == 0) {
                editStr2 = " ";
            }
            if (allLayout.getChildCount() - 1 == lastEditIndex) {
                addEditTextAtIndex(lastEditIndex + 1, editStr2);
            }
            addImageViewAtIndex(lastEditIndex + 1, imagePath);
            lastFocusEdit.requestFocus();
            lastFocusEdit.setSelection(editStr1.length(), editStr1.length());//TODO
        }
        hideKeyBoard();
    }

    /**
     * 隐藏小键盘
     */
    public void hideKeyBoard() {
        InputMethodManager imm = (InputMethodManager) getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
    }

    /**
     * 在特定位置插入EditText
     * @param index   位置
     * @param editStr EditText显示的文字
     */
    public void addEditTextAtIndex(final int index, CharSequence editStr) {
        EditText editText2 = createEditText("", EDIT_PADDING);
        editText2.setText(editStr);
        editText2.setOnFocusChangeListener(focusListener);
        allLayout.addView(editText2, index);
    }

    /**
     * 在特定位置添加ImageView
     */
    public void addImageViewAtIndex(final int index, String imagePath) {
        final RelativeLayout imageLayout = createImageLayout();
        HyperImageView imageView = (HyperImageView) imageLayout.findViewById(R.id.edit_imageView);
        Glide.with(getContext()).load(imagePath).crossFade().centerCrop().into(imageView);
        //Picasso.with(getContext()).load(imagePath).centerCrop().into(imageView);
        imageView.setAbsolutePath(imagePath);//保留这句，后面保存数据会用
        imageView.setScaleType(ImageView.ScaleType.CENTER_CROP);//裁剪剧中
        // 调整imageView的高度，根据宽度来调整高度
        Bitmap bmp = BitmapFactory.decodeFile(imagePath);
        int imageHeight = 500;
        if (bmp != null) {
            imageHeight = allLayout.getWidth() * bmp.getHeight() / bmp.getWidth();
            bmp.recycle();
        }
        //设置图片固定高度
        RelativeLayout.LayoutParams lp = new RelativeLayout.LayoutParams(LayoutParams.MATCH_PARENT, imageHeight);
        lp.bottomMargin = 10;
        imageView.setLayoutParams(lp);
        allLayout.addView(imageLayout, index);
    }

    /**
     * 根据view的宽度，动态缩放bitmap尺寸
     */
    public Bitmap getScaledBitmap(String filePath, int width) {
        BitmapFactory.Options options = new BitmapFactory.Options();
        options.inJustDecodeBounds = true;
        BitmapFactory.decodeFile(filePath, options);
        int sampleSize = options.outWidth > width ? options.outWidth / width + 1 : 1;
        options.inJustDecodeBounds = false;
        options.inSampleSize = sampleSize;
        return BitmapFactory.decodeFile(filePath, options);
    }

    /**
     * 对外提供的接口, 生成编辑数据上传
     */
    public List<EditData> buildEditData() {
        List<EditData> dataList = new ArrayList<EditData>();
        int num = allLayout.getChildCount();
        for (int index = 0; index < num; index++) {
            View itemView = allLayout.getChildAt(index);
            EditData itemData = new EditData();
            if (itemView instanceof EditText) {
                EditText item = (EditText) itemView;
                itemData.inputStr = item.getText().toString();
            } else if (itemView instanceof RelativeLayout) {
                HyperImageView item = (HyperImageView) itemView.findViewById(R.id.edit_imageView);
                itemData.imagePath = item.getAbsolutePath();
            }
            dataList.add(itemData);
        }
        return dataList;
    }

    public class EditData {
        //注意一定要是public修饰
        public String inputStr;
        public String imagePath;
    }

    /** 删除文件 **/
    public void deleteFile(String filePath) {
        if(filePath==null){
            return;
        }
        File file = new File(filePath);
        if (file.isFile() && file.exists()){
            file.delete(); // 删除文件
        }
    }

    /**
     * dp转px
     * @param context 上下文
     * @param dpValue dp值
     * @return px值
     */
    public static int dp2px(Context context, float dpValue) {
        final float scale = context.getResources().getDisplayMetrics().density;
        return (int) (dpValue * scale + 0.5f);
    }

}
