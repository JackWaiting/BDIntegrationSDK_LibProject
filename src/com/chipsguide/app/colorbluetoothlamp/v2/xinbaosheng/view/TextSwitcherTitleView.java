package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.util.AttributeSet;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextSwitcher;
import android.widget.ViewSwitcher.ViewFactory;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class TextSwitcherTitleView extends FrameLayout implements ViewFactory{
	private ImageView rightBtn,leftBtn;
	private TextSwitcher titleTv;
	
	public TextSwitcherTitleView(Context context, AttributeSet attrs) {
		super(context, attrs);
		LayoutInflater.from(getContext()).inflate(R.layout.common_switch_title_layout, this);
		rightBtn = (ImageView) findViewById(R.id.right_btn);
		leftBtn = (ImageView) findViewById(R.id.left_btn);
		titleTv = (TextSwitcher) findViewById(R.id.title_tv);
		titleTv.setFactory(this);
		titleTv.setInAnimation(context, R.anim.top_in);
		titleTv.setOutAnimation(context, R.anim.bottom_out);
		
		TypedArray a = context.obtainStyledAttributes(attrs,R.styleable.TitleView);
		int leftImgRes = a.getResourceId(R.styleable.TitleView_leftImg, R.drawable.selector_btn_nav);
		int rightImgRes = a.getResourceId(R.styleable.TitleView_rightImg, R.drawable.selector_btn_player);
		String text = a.getString(R.styleable.TitleView_text);
		boolean visible = a.getBoolean(R.styleable.TitleView_rightBtnVisibility, true);
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
	
	private String text;
	public void setTitleText(String text){
		if(text != null && text.equals(this.text)){
			return;
		}
		this.text = text;
		titleTv.setText(text);
	}
	
	public void setTitleText(int resId){
		String text = getContext().getResources().getString(resId);
		setTitleText(text);
	}
	
	@Override
	public void setOnClickListener(OnClickListener l) {
		rightBtn.setOnClickListener(l);
		leftBtn.setOnClickListener(l);
	}

	@Override
	public View makeView() {
		MyTextView tv = new MyTextView(getContext());
		tv.setTextColor(Color.WHITE);
		tv.setGravity(Gravity.CENTER);
		tv.setTextSize(TypedValue.COMPLEX_UNIT_SP,20);
		return tv;
	}
	
}
