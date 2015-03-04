package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.animation.Animation;
import android.view.animation.LinearInterpolator;
import android.view.animation.RotateAnimation;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class CustomDialog extends Dialog {
	private ImageView progressIv;
	private TextView msgTv;
	private int msgId;

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
		msgId = resId;
	}

	private void initView() {
		progressIv = (ImageView) this.findViewById(R.id.iv_progress);
		msgTv = (TextView) this.findViewById(R.id.tv_message);
		if(msgId > 0){
			msgTv.setText(msgId);
		}else{
			msgTv.setText("");
		}
		RotateAnimation anim = new RotateAnimation(359, 0,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(2000);
		progressIv.startAnimation(anim);
	}
}
