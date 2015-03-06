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

public class ConnectDialog extends Dialog implements Runnable {

	private ImageView progressIv;
	private TextView mConnectDialogText;
	private TextView mConnect_anmin;
	private String textAnminString = "";
	private Handler mHandler = new Handler();

	public ConnectDialog(Context context)
	{
		super(context, 0);
	}

	public ConnectDialog(Context context, int theme)
	{
		super(context, theme);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_dialog_connect_loading);
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		initView();
		startAnim();
	}

	@Override
	public void run()
	{
		if (textAnminString.length() < 4)
		{
			textAnminString = textAnminString + ".";
		} else
		{
			textAnminString = "";
		}
		mConnect_anmin.setText(textAnminString);
		mHandler.postDelayed(this, 600);
	}

	public void setMessage(int resId)
	{
		mConnectDialogText = (TextView) this
				.findViewById(R.id.textview_connect);
		mConnect_anmin = (TextView) this
				.findViewById(R.id.textview_connect_anmin);
		if (resId > 0)
		{
			mConnect_anmin.setVisibility(View.VISIBLE);
			mConnectDialogText.setText(resId);
		} else
		{
			mConnect_anmin.setVisibility(View.INVISIBLE);
			mConnectDialogText.setText("");
		}
	}

	private void initView()
	{
		progressIv = (ImageView) this.findViewById(R.id.iv_progress);
		mConnectDialogText = (TextView) this
				.findViewById(R.id.textview_connect);
		mConnect_anmin = (TextView) this
				.findViewById(R.id.textview_connect_anmin);
		mConnect_anmin.post(this);
	}

	private void startAnim()
	{
		RotateAnimation anim = new RotateAnimation(0, 360,
				Animation.RELATIVE_TO_SELF, 0.5f, Animation.RELATIVE_TO_SELF,
				0.5f);
		anim.setInterpolator(new LinearInterpolator());
		anim.setRepeatCount(Animation.INFINITE);
		anim.setDuration(2000);
		progressIv.startAnimation(anim);
	}
}
