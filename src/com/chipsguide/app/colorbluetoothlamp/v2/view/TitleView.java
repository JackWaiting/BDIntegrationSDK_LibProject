package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.AnimationSet;
import android.view.animation.AnimationUtils;
import android.view.animation.TranslateAnimation;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PixelUtil;

public class TitleView extends FrameLayout {
	private ImageView rightBtn,leftBtn;
	private TextView titleTv,toastTv;
	private boolean showToastTv;
	private boolean hasAddToastTv;

	public TitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(getContext()).inflate(R.layout.common_title_layout, this);
		rightBtn = (ImageView) findViewById(R.id.right_btn);
		leftBtn = (ImageView) findViewById(R.id.left_btn);
		titleTv = (TextView) findViewById(R.id.title_tv);
		
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.TitleView);
		int leftImgRes = a.getResourceId(R.styleable.TitleView_leftImg, R.drawable.selector_btn_nav);
		int rightImgRes = a.getResourceId(R.styleable.TitleView_rightImg, R.drawable.selector_btn_player);
		String text = a.getString(R.styleable.TitleView_text);
		boolean visible = a.getBoolean(R.styleable.TitleView_rightBtnVisibility, true);
		boolean marque = a.getBoolean(R.styleable.TitleView_marque, true);
		boolean showToastTv = a.getBoolean(R.styleable.TitleView_showToastTv, false);
		setShowToastTv(showToastTv);
		titleTv.setFocusable(marque);
		setRightBtnVisibility(visible);
		setRightBtnImageRes(rightImgRes);
		setLeftBtnImageRes(leftImgRes);
		setTitleText(text);
		a.recycle();
		
		setClipChildren(false);
	}
	
	public void setRightBtnImageRes(int resId) {
		rightBtn.setImageResource(resId);
	}
	
	public void setRightBtnVisibility(boolean visible) {
		rightBtn.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}

	public void setLiftBtnVisibility(boolean visible) {
		leftBtn.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
	}
	
	public void setLeftBtnImageRes(int resId){
		leftBtn.setImageResource(resId);
	}
	
	public void setTitleText(String text){
		titleTv.setText(text);
	}
	
	public void setTitleText(int resId){
		titleTv.setText(resId);
	}
	
	public void setToastText(int resId) {
		setToastText(getResources().getString(resId));
	}
	
	public boolean isShowToastTv() {
		return showToastTv;
	}
	
	public void setShowToastTv(boolean showToastTv) {
		this.showToastTv = showToastTv;
		if(hasAddToastTv){
			return;
		}
		hasAddToastTv = true;
		toastTv = new MyTextView(getContext());
		int padding = PixelUtil.dp2px(5, getContext());
		toastTv.setPadding(PixelUtil.dp2px(20, getContext()), padding, 0, padding);
		toastTv.setBackgroundColor(getResources().getColor(R.color.color_orange));
		toastTv.setTextColor(Color.WHITE);
		toastTv.setTextSize(TypedValue.COMPLEX_UNIT_SP, 16);
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, LayoutParams.WRAP_CONTENT);
		params.gravity = Gravity.BOTTOM;
		addView(toastTv, 0, params);
		setClipChildren(false);
	}
	
	private AnimationSet set;
	private void initAnimation() {
		set = new AnimationSet(true);
		TranslateAnimation animIn = getTransAnim(0, 1, 400, 0);
		TranslateAnimation animOut = getTransAnim(0, -1, 400, 1400);
		set.addAnimation(animIn);
		set.addAnimation(animOut);
	}
	
	private TranslateAnimation getTransAnim(float fromY, float toY, long duration, long startOffset) {
		int type = Animation.RELATIVE_TO_SELF;
		TranslateAnimation transAnim = new TranslateAnimation(type, 0, type, 0, type, fromY, type, toY);
		transAnim.setDuration(duration);
		transAnim.setStartOffset(startOffset);
		transAnim.setFillEnabled(true);  
		transAnim.setFillAfter(true);
		return transAnim;
	}
	
	/*
	 * 需要父类setClipChildren(false),否则不显示
	 */
	public void setToastText(String text) {
		if(!showToastTv){
			return;
		}
		toastTv.setVisibility(View.VISIBLE);
		toastTv.clearAnimation();
		toastTv.setText(text);
		if(set == null){
			initAnimation();
		}
		toastTv.startAnimation(set);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		rightBtn.setOnClickListener(l);
		leftBtn.setOnClickListener(l);
	}
}
