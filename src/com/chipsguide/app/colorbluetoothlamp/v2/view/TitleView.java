package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class TitleView extends FrameLayout {
	private ImageView rightBtn,leftBtn;
	private TextView titleTv;

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
		titleTv.setFocusable(marque);
		setRightBtnVisibility(visible);
		setRightBtnImageRes(rightImgRes);
		setLeftBtnImageRes(leftImgRes);
		setTitleText(text);
		a.recycle();
	}
	
	public void setRightBtnImageRes(int resId) {
		rightBtn.setImageResource(resId);
	}
	
	public void setRightBtnVisibility(boolean visible) {
		rightBtn.setVisibility(visible ? View.VISIBLE : View.INVISIBLE);
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
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		rightBtn.setOnClickListener(l);
		leftBtn.setOnClickListener(l);
	}
}
