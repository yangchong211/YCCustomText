/*
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
*/
package com.ns.yc.yccustomtextlib.edit.view;

import android.animation.LayoutTransition;
import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.os.Parcelable;
import android.support.annotation.Nullable;
import android.text.Editable;
import android.text.SpannableStringBuilder;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.FrameLayout;
import android.widget.ScrollView;

import com.ns.yc.yccustomtextlib.edit.inter.OnHyperChangeListener;
import com.ns.yc.yccustomtextlib.edit.manager.HyperManager;
import com.ns.yc.yccustomtextlib.R;
import com.ns.yc.yccustomtextlib.edit.inter.OnHyperEditListener;
import com.ns.yc.yccustomtextlib.edit.model.HyperEditData;
import com.ns.yc.yccustomtextlib.edit.span.SpanTextHelper;
import com.ns.yc.yccustomtextlib.edit.state.TextEditorState;
import com.ns.yc.yccustomtextlib.utils.HyperLibUtils;
import com.ns.yc.yccustomtextlib.utils.HyperLogUtils;

import java.util.ArrayList;
import java.util.List;


/**
 * <pre>
 *     @author 杨充
 *     blog  : https://github.com/yangchong211
 *     time  : 2016/3/31
 *     desc  : 编辑富文本
 *     revise:
 * </pre>
 */
public class HyperTextEditor extends ScrollView {

	/**
	 * editText常规padding是10dp
	 */
	private static final int EDIT_PADDING = 10;
	/**
	 * 新生的view都会打一个tag，对每个view来说，这个tag是唯一的。
	 */
	private int viewTagIndex = 1;
	/**
	 * 这个是所有子view的容器，scrollView内部的唯一一个ViewGroup
	 */
	private LinearLayout layout;
	/**
	 * inflater对象
	 */
	private LayoutInflater inflater;
	/**
	 * 所有EditText的软键盘监听器
	 */
	private OnKeyListener keyListener;
	/**
	 * 图片右上角红叉按钮监听器
	 */
	private OnClickListener btnListener;
	/**
	 * 所有EditText的焦点监听listener
	 */
	private OnFocusChangeListener focusListener;
	/**
	 * 所有EditText的文本变化监听listener
	 */
	private TextWatcher textWatcher;
	/**
	 * 最近被聚焦的EditText
	 */
	private EditText lastFocusEdit;
	/**
	 * 只在图片View添加或remove时，触发transition动画
	 */
	private LayoutTransition mTransition;
	private int editNormalPadding = 0;
	private int disappearingImageIndex = 0;
	/**
	 * 图片地址集合
	 */
	private ArrayList<String> imagePaths;
	/**
	 * 关键词高亮
	 */
	private String keywords;
	/**
	 * 插入的图片显示高度
	 */
	private int rtImageHeight;
	/**
	 * 父控件的上和下padding
	 */
	private int topAndBottom;
	/**
	 * 父控件的左和右padding
	 */
	private int leftAndRight;
	/**
	 * 两张相邻图片间距
	 */
    private int rtImageBottom = 10;
	/**
	 * 文字相关属性，初始提示信息，文字大小和颜色
	 */
    private String rtTextInitHint = "请输入内容";
	/**
	 * 文字大小
	 */
	private int rtTextSize = 16;
	/**
	 * 文字颜色
	 */
    private int rtTextColor = Color.parseColor("#757575");
	private int rtHintTextColor = Color.parseColor("#B0B1B8");
	/**
	 * 文字行间距
	 */
	private int rtTextLineSpace = 8;
	/**
	 * 富文本的文字长度
	 */
	private int contentLength = 0;
	/**
	 * 富文本的图片个数
	 */
	private int imageLength = 0;
	/**
	 * 删除图片的位置
	 */
	private int delIconLocation = 0;
	private OnHyperEditListener onHyperListener;
	private OnHyperChangeListener onHyperChangeListener;

	/**
	 * 保存重要信息
	 * @return
	 */
	@Nullable
	@Override
	protected Parcelable onSaveInstanceState() {
		Parcelable superState = super.onSaveInstanceState();
		TextEditorState viewState = new TextEditorState(superState);
		viewState.rtImageHeight = rtImageHeight;
		return viewState;
	}

	/**
	 * 复现
	 * @param state								state
	 */
	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		TextEditorState viewState = (TextEditorState) state;
		rtImageHeight = viewState.rtImageHeight;
		super.onRestoreInstanceState(viewState.getSuperState());
		requestLayout();
	}

	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if (mTransition!=null){
			//移除Layout变化监听
			mTransition.removeTransitionListener(transitionListener);
			HyperLogUtils.d("HyperTextEditor----onDetachedFromWindow------移除Layout变化监听");
		}
	}

	public HyperTextEditor(Context context) {
		this(context, null);
	}

	public HyperTextEditor(Context context, AttributeSet attrs) {
		this(context, attrs, 0);
	}

	public HyperTextEditor(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		imagePaths = new ArrayList<>();
		inflater = LayoutInflater.from(context);
		initAttrs(context,attrs);
		initLayoutView(context);
		initListener();
		initFirstEditText(context);
	}

	private void initLayoutView(Context context) {
		//初始化layout
		layout = new LinearLayout(context);
		layout.setOrientation(LinearLayout.VERTICAL);
		//禁止载入动画
		setUpLayoutTransitions();
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		//设置间距，防止生成图片时文字太靠边，不能用margin，否则有黑边
		layout.setPadding(leftAndRight,topAndBottom,leftAndRight,topAndBottom);
		addView(layout, layoutParams);
	}

	/**
	 * 初始化自定义属性
	 * @param context						context上下文
	 * @param attrs							attrs属性
	 */
	private void initAttrs(Context context, AttributeSet attrs) {
		//获取自定义属性
		TypedArray ta = context.obtainStyledAttributes(attrs, R.styleable.HyperTextEditor);
		topAndBottom = ta.getDimensionPixelSize(R.styleable.HyperTextEditor_editor_layout_top_bottom, 15);
		leftAndRight = ta.getDimensionPixelSize(R.styleable.HyperTextEditor_editor_layout_right_left, 40);
		rtImageHeight = ta.getDimensionPixelSize(R.styleable.HyperTextEditor_editor_image_height, 250);
		rtImageBottom = ta.getDimensionPixelSize(R.styleable.HyperTextEditor_editor_image_bottom, 10);
		rtTextSize = ta.getDimensionPixelSize(R.styleable.HyperTextEditor_editor_text_size, 16);
		rtTextLineSpace = ta.getDimensionPixelSize(R.styleable.HyperTextEditor_editor_text_line_space, 8);
		rtTextColor = ta.getColor(R.styleable.HyperTextEditor_editor_text_color, Color.parseColor("#757575"));
		rtHintTextColor = ta.getColor(R.styleable.HyperTextEditor_editor_hint_text_color,Color.parseColor("#B0B1B8"));
		rtTextInitHint = ta.getString(R.styleable.HyperTextEditor_editor_text_init_hint);
		delIconLocation = ta.getInt(R.styleable.HyperTextEditor_editor_del_icon_location,0);
		ta.recycle();
	}


	private void initListener() {
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
				} else if (v instanceof ImageView){
					FrameLayout parentView = (FrameLayout) v.getParent();
					// 图片删除图片点击事件
					if (onHyperListener != null){
						onHyperListener.onImageCloseClick(parentView);
					}
					//onImageCloseClick(parentView);
				}
			}
		};
		focusListener = new OnFocusChangeListener() {
			@Override
			public void onFocusChange(View v, boolean hasFocus) {
				if (hasFocus) {
					lastFocusEdit = (EditText) v;
					HyperLogUtils.d("HyperTextEditor---onFocusChange--"+lastFocusEdit);
				}
			}
		};
		textWatcher = new TextWatcher() {
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count, int after) {

			}

			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				addHyperEditorChangeListener();
				HyperLogUtils.d("HyperTextEditor---onTextChanged--文字--"+contentLength+"--图片-"+imageLength);
			}

			@Override
			public void afterTextChanged(Editable s) {

			}
		};
	}


	private void initFirstEditText(Context context) {
		LinearLayout.LayoutParams firstEditParam = new LinearLayout.LayoutParams(
				LayoutParams.MATCH_PARENT, LayoutParams.MATCH_PARENT);
		int padding = HyperLibUtils.dip2px(context, EDIT_PADDING);
		EditText firstEdit = createEditText(rtTextInitHint, padding);
		layout.addView(firstEdit, firstEditParam);
		lastFocusEdit = firstEdit;
	}


	/**
	 * 处理软键盘backSpace回退事件
	 * @param editText 					光标所在的文本输入框
	 */
	@SuppressLint("SetTextI18n")
	private void onBackspacePress(EditText editText) {
		try {
			int startSelection = editText.getSelectionStart();
			// 只有在光标已经顶到文本输入框的最前方，在判定是否删除之前的图片，或两个View合并
			if (startSelection == 0) {
				//获取当前控件在layout父容器中的索引
				int editIndex = layout.indexOfChild(editText);
				// 如果editIndex-1<0,
				View preView = layout.getChildAt(editIndex - 1);
				if (null != preView) {
					if (preView instanceof FrameLayout) {
						// 光标EditText的上一个view对应的是图片，删除图片操作
						onImageCloseClick(preView);
					} else if (preView instanceof EditText) {
						// 光标EditText的上一个view对应的还是文本框EditText，删除文字操作
						String str1 = editText.getText().toString();
						EditText preEdit = (EditText) preView;
						String str2 = preEdit.getText().toString();
						// 合并文本view时，不需要transition动画
						layout.setLayoutTransition(null);
						//移除editText文本控件
						layout.removeView(editText);
						// 恢复transition动画
						layout.setLayoutTransition(mTransition);
						// 文本合并操作
						preEdit.setText(str2 + str1);
						preEdit.requestFocus();
						preEdit.setSelection(str2.length(), str2.length());
						lastFocusEdit = preEdit;
					}
				} else {
					HyperLogUtils.d("HyperTextEditor----onBackspacePress------没有上一个view");
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 处理图片上删除的点击事件
	 * 删除类型 0代表backspace删除 1代表按红叉按钮删除
	 * @param view 							整个image对应的relativeLayout view
	 */
	public void onImageCloseClick(View view) {
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
                    addHyperEditorChangeListener();
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

	/**
	 * 监听富文本：文字+图片数量变化
	 * 分别在图片插入，图片删除，以及文本变化时添加监听事件
	 */
	private void addHyperEditorChangeListener() {
		getContentAndImageCount();
		int contentLength = getContentLength();
		int imageLength = getImageLength();
		if (onHyperChangeListener!=null){
			onHyperChangeListener.onImageClick(contentLength,imageLength);
		}
	}

	/**
	 * 清空所有布局
	 */
	public void clearAllLayout(){
		if (layout!=null){
			layout.removeAllViews();
		}
	}

	/**
	 * 获取索引位置
     */
	public int getLastIndex(){
		if (layout!=null){
			int childCount = layout.getChildCount();
			return childCount;
		}
		return -1;
	}

	/**
	 * 添加生成文本输入框
	 * @param hint								内容
	 * @param paddingTop						到顶部高度
	 * @return
	 */
	public EditText createEditText(String hint, int paddingTop) {
		EditText editText = new DeletableEditText(getContext());
		LayoutParams layoutParams = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		editText.setLayoutParams(layoutParams);
		editText.setTextSize(16);
		editText.setTextColor(Color.parseColor("#616161"));
		editText.setCursorVisible(true);
		editText.setBackground(null);
		editText.setOnKeyListener(keyListener);
		editText.setOnFocusChangeListener(focusListener);
		editText.addTextChangedListener(textWatcher);
		editText.setTag(viewTagIndex++);
		editText.setPadding(editNormalPadding, paddingTop, editNormalPadding, paddingTop);
		editText.setHint(hint);
		editText.setTextSize(TypedValue.COMPLEX_UNIT_PX, rtTextSize);
		editText.setTextColor(rtTextColor);
		editText.setHintTextColor(rtHintTextColor);
		editText.setLineSpacing(rtTextLineSpace, 1.0f);
		return editText;
	}

	/**
	 * 生成图片View
	 */
	private FrameLayout createImageLayout() {
		FrameLayout layout = (FrameLayout) inflater.inflate(R.layout.hte_edit_imageview, null);
		layout.setTag(viewTagIndex++);
		ImageView closeView = layout.findViewById(R.id.image_close);
		FrameLayout.LayoutParams layoutParams = (LayoutParams) closeView.getLayoutParams();
		layoutParams.bottomMargin = HyperLibUtils.dip2px(layout.getContext(),10.0f);
		switch (delIconLocation){
			//左上角
			case 1:
				layoutParams.gravity = Gravity.TOP | Gravity.START;
				closeView.setLayoutParams(layoutParams);
				break;
			//右上角
			case 2:
				layoutParams.gravity = Gravity.TOP | Gravity.END;
				closeView.setLayoutParams(layoutParams);
				break;
			//左下角
			case 3:
				layoutParams.gravity = Gravity.BOTTOM | Gravity.START;
				closeView.setLayoutParams(layoutParams);
				break;
			//右下角
			case 4:
				layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
				closeView.setLayoutParams(layoutParams);
				break;
			//其他右下角
			default:
				layoutParams.gravity = Gravity.BOTTOM | Gravity.END;
				closeView.setLayoutParams(layoutParams);
				break;
		}
		closeView.setTag(layout.getTag());
		closeView.setOnClickListener(btnListener);
		HyperImageView imageView = layout.findViewById(R.id.edit_imageView);
		imageView.setOnClickListener(btnListener);
		return layout;
	}

	/**
	 * 插入一张图片
	 * @param imagePath							图片路径地址
	 */
	public void insertImage(String imagePath) {
		//bitmap == null时，可能是网络图片，不能做限制
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
				addEditTextAtIndex(lastEditIndex + 1, "");
				addImageViewAtIndex(lastEditIndex + 1, imagePath);
			} else if (editStr1.length() == 0) {
				//如果光标已经顶在了editText的最前面，则直接插入图片，并且EditText下移即可
				addImageViewAtIndex(lastEditIndex, imagePath);
				//同时插入一个空的EditText，防止插入多张图片无法写文字
				addEditTextAtIndex(lastEditIndex + 1, "");
			} else if (editStr2.length() == 0) {
				// 如果光标已经顶在了editText的最末端，则需要添加新的imageView和EditText
				addEditTextAtIndex(lastEditIndex + 1, "");
				addImageViewAtIndex(lastEditIndex + 1, imagePath);
			} else {
				//如果光标已经顶在了editText的最中间，则需要分割字符串，分割成两个EditText，并在两个EditText中间插入图片
				//把光标前面的字符串保留，设置给当前获得焦点的EditText（此为分割出来的第一个EditText）
				lastFocusEdit.setText(editStr1);
				//把光标后面的字符串放在新创建的EditText中（此为分割出来的第二个EditText）
				addEditTextAtIndex(lastEditIndex + 1, editStr2);
				//在第二个EditText的位置插入一个空的EditText，以便连续插入多张图片时，有空间写文字，第二个EditText下移
				addEditTextAtIndex(lastEditIndex + 1, "");
				//在空的EditText的位置插入图片布局，空的EditText下移
				addImageViewAtIndex(lastEditIndex + 1, imagePath);
			}
			//隐藏小键盘
			hideKeyBoard();
			//监听富文本：文字+图片数量变化
			addHyperEditorChangeListener();
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 隐藏小键盘
	 */
	public void hideKeyBoard() {
		InputMethodManager imm = (InputMethodManager)
				getContext().getSystemService(Context.INPUT_METHOD_SERVICE);
		if (imm != null && lastFocusEdit != null) {
			imm.hideSoftInputFromWindow(lastFocusEdit.getWindowToken(), 0);
		}
	}

	public void setKeywords(String keywords) {
		this.keywords = keywords;
	}

	/**
	 * 在特定位置插入EditText
	 * @param index							位置
	 * @param editStr						EditText显示的文字
	 */
	public void addEditTextAtIndex(final int index, CharSequence editStr) {
		try {
			EditText editText = createEditText("插入文字", EDIT_PADDING);
			if (!TextUtils.isEmpty(keywords)) {
				//搜索关键词高亮
				SpannableStringBuilder textStr = HyperLibUtils.highlight(
						editStr.toString(), keywords  , Color.parseColor("#EE5C42"));
				editText.setText(textStr);
			} else if (!TextUtils.isEmpty(editStr)) {
				//判断插入的字符串是否为空，如果没有内容则显示hint提示信息
				editText.setText(editStr);
			}

			// 请注意此处，EditText添加、或删除不触动Transition动画
			layout.setLayoutTransition(null);
			layout.addView(editText, index);
			// remove之后恢复transition动画
			layout.setLayoutTransition(mTransition);
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

	/**
	 * 在特定位置添加ImageView
	 */
	public void addImageViewAtIndex(final int index, final String imagePath) {
		if (TextUtils.isEmpty(imagePath)){
			return;
		}
		try {
			imagePaths.add(imagePath);
			final FrameLayout imageLayout = createImageLayout();
			HyperImageView imageView = imageLayout.findViewById(R.id.edit_imageView);
			imageView.setAbsolutePath(imagePath);
			HyperManager.getInstance().loadImage(imagePath, imageView, rtImageHeight);
			layout.addView(imageLayout, index);
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 初始化transition动画
	 */
	private void setUpLayoutTransitions() {
		mTransition = new LayoutTransition();
		//添加Layout变化监听
		mTransition.addTransitionListener(transitionListener);
		//若向ViewGroup中添加一个ImageView，ImageView对象可以设置动画(即APPEARING 动画形式)，
		//ViewGroup中的其它ImageView对象此时移动到新的位置的过程中也可以设置相关的动画(即CHANGE_APPEARING 动画形式)。
		mTransition.enableTransitionType(LayoutTransition.APPEARING);
		//设置整个Layout变换动画时间
		mTransition.setDuration(300);
		layout.setLayoutTransition(mTransition);
	}

	private LayoutTransition.TransitionListener transitionListener =
			new LayoutTransition.TransitionListener() {

		/**
		 * LayoutTransition某一类型动画开始
		 * @param transition				transition
		 * @param container					container容器
		 * @param view						view控件
		 * @param transitionType			类型
		 */
		@Override
		public void startTransition(LayoutTransition transition, ViewGroup container,
									View view, int transitionType) {

		}

		/**
		 * LayoutTransition某一类型动画结束
		 * @param transition				transition
		 * @param container					container容器
		 * @param view						view控件
		 * @param transitionType			类型
		 */
		@Override
		public void endTransition(LayoutTransition transition,
								  ViewGroup container, View view, int transitionType) {
			if (!transition.isRunning() && transitionType == LayoutTransition.CHANGE_DISAPPEARING) {
				// transition动画结束，合并EditText
				mergeEditText();
			}
		}
	};

	/**
	 * 图片删除的时候，如果上下方都是EditText，则合并处理
	 */
	private void mergeEditText() {
		try {
			View preView = layout.getChildAt(disappearingImageIndex - 1);
			View nextView = layout.getChildAt(disappearingImageIndex);
			if (preView instanceof EditText && nextView instanceof EditText) {
				EditText preEdit = (EditText) preView;
				EditText nextEdit = (EditText) nextView;
				String str1 = preEdit.getText().toString();
				String str2 = nextEdit.getText().toString();
				String mergeText = "";
				if (str2.length() > 0) {
					mergeText = str1 + "\n" + str2;
				} else {
					mergeText = str1;
				}

				layout.setLayoutTransition(null);
				layout.removeView(nextEdit);
				preEdit.setText(mergeText);
				//设置光标的定位
				preEdit.requestFocus();
				preEdit.setSelection(str1.length(), str1.length());
				//设置动画
				layout.setLayoutTransition(mTransition);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	/**
	 * 对外提供的接口, 生成编辑数据上传
	 */
	public List<HyperEditData> buildEditData() {
		List<HyperEditData> dataList = new ArrayList<>();
		try {
			int num = layout.getChildCount();
			for (int index = 0; index < num; index++) {
				View itemView = layout.getChildAt(index);
				HyperEditData hyperEditData = new HyperEditData();
				if (itemView instanceof EditText) {
				    //文本
					EditText item = (EditText) itemView;
					hyperEditData.setInputStr(item.getText().toString());
					hyperEditData.setType(1);
				} else if (itemView instanceof FrameLayout) {
				    //图片
					HyperImageView item = itemView.findViewById(R.id.edit_imageView);
					hyperEditData.setImagePath(item.getAbsolutePath());
                    hyperEditData.setType(2);
				}
				dataList.add(hyperEditData);
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		HyperLogUtils.d("HyperTextEditor----buildEditData------dataList---"+dataList.size());
		return dataList;
	}

	/**
	 * 用于统计文本文字的数量和图片的数量
	 */
	public void getContentAndImageCount() {
		contentLength = 0;
		imageLength = 0;
		if (layout==null){
			return;
		}
		try {
			int num = layout.getChildCount();
			for (int index = 0; index < num; index++) {
				View itemView = layout.getChildAt(index);
				if (itemView instanceof EditText) {
					//文本
					EditText item = (EditText) itemView;
					if (item.getText()!=null){
						String string = item.getText().toString().trim();
						int length = string.length();
						contentLength = contentLength + length;
					}
				} else if (itemView instanceof FrameLayout) {
					//图片
					imageLength++;
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
		HyperLogUtils.d("HyperTextEditor----buildEditData------dataList---");
	}

	/**
	 * 上传图片成功之后，替换本地图片路径
	 * @param url						网络图片
	 * @param localPath					本地图片
	 */
	public void setImageUrl(String url,String localPath) {
		HyperLogUtils.d("HyperTextEditor----setImageUrl1------"+url+"----"+localPath);
		if (layout==null){
			return;
		}
		try {
			int num = layout.getChildCount();
			for (int index = 0; index < num; index++) {
				View itemView = layout.getChildAt(index);
				if (itemView instanceof FrameLayout) {
					//图片控件，需要替换图片url
					HyperImageView item = itemView.findViewById(R.id.edit_imageView);
					if (item.getAbsolutePath().equals(localPath)){
						item.setAbsolutePath(url);
						HyperLogUtils.d("HyperTextEditor----setImageUrl2------"+url+"----"+localPath);
						return;
					}
				}
			}
		} catch (Exception e) {
			e.printStackTrace();
		}
	}

	public void setOnHyperListener(OnHyperEditListener listener){
		this.onHyperListener = listener;
	}

    public void setOnHyperChangeListener(OnHyperChangeListener onHyperChangeListener) {
        this.onHyperChangeListener = onHyperChangeListener;
    }

    public EditText getLastFocusEdit() {
		return lastFocusEdit;
	}

	public int getContentLength() {
		return contentLength;
	}

	public int getImageLength() {
		return imageLength;
	}

	/**
	 * 修改加粗样式
	 */
	public void bold() {
		SpanTextHelper.getInstance().bold(lastFocusEdit);
	}

	/**
	 * 修改斜体样式
	 */
	public void italic() {
		SpanTextHelper.getInstance().italic(lastFocusEdit);
	}

	/**
	 * 修改加粗斜体样式
	 */
	public void boldItalic() {
		SpanTextHelper.getInstance().boldItalic(lastFocusEdit);
	}

	/**
	 * 修改删除线样式
	 */
	public void strikeThrough() {
		SpanTextHelper.getInstance().strikeThrough(lastFocusEdit);
	}

	/**
	 * 修改下划线样式
	 */
	public void underline() {
		SpanTextHelper.getInstance().underline(lastFocusEdit);
	}

}
