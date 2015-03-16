package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.os.Handler;
import android.view.View;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class CustomDialog extends Dialog {
	private ImageView progressIv, lampIv, successIv;
	private TextView msgTv;
	private String msgText;

	public CustomDialog(Context context) {
		super(context, 0);
	}

	public CustomDialog(Context context, int theme) {
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_dialog_loading);
	}

	@Override
	public void onAttachedToWindow() {
		super.onAttachedToWindow();
		initView();
	}
	
	
	public void setMessage(int resId) {
		msgText = getContext().getResources().getString(resId);
	}
	
	public void setMessage(String text) {
		msgText = text;
	}
	
	public void updateMessage(String msg){
		if(msgTv != null){
			msgTv.setText(msg);
		}
	}
	
	public void dismiss(boolean success, long delay) {
		if(success){
			progressIv.clearAnimation();
			progressIv.setVisibility(View.INVISIBLE);
			lampIv.setVisibility(View.INVISIBLE);
			successIv.setVisibility(View.VISIBLE);
		}
		new Handler().postDelayed(new Runnable() {
			@Override
			public void run() {
				CustomDialog.this.dismiss();
			}
		}, delay);
	}

	private void initView() {
		progressIv = (ImageView) this.findViewById(R.id.iv_progress);
		lampIv = (ImageView) this.findViewById(R.id.iv_lamp);
		successIv = (ImageView) this.findViewById(R.id.iv_complete);
		msgTv = (TextView) this.findViewById(R.id.tv_message);
		msgTv.setText(msgText);
		RotateAnimation anim = new RotateAnimation(359, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(2000);
		progressIv.startAnimation(anim);
	}
}
