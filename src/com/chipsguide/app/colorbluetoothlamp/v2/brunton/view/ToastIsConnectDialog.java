package com.chipsguide.app.colorbluetoothlamp.v2.brunton.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity.BluetoothConnectionActivity;
//弹出框
public class ToastIsConnectDialog extends Dialog implements OnClickListener {
	private Context mContext;
	private TextView mTextViewDelay;
	private TextView mTextViewConnect;

	public ToastIsConnectDialog(Context context)
	{
		this(context, 0);
	}

	public ToastIsConnectDialog(Context context, int theme)
	{
		super(context, theme);
		mContext = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.layout_dialog_connect_toast);
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		initView();
	}

	private void initView()
	{
		mTextViewDelay = (TextView) this
				.findViewById(R.id.textview_connect_toast_delay);
		mTextViewConnect = (TextView) this
				.findViewById(R.id.textview_connect_toast);
		mTextViewDelay.setOnClickListener(this);
		mTextViewConnect.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.textview_connect_toast_delay:
			dismiss();
			break;
		case R.id.textview_connect_toast:
			Intent intent = new Intent(mContext, BluetoothConnectionActivity.class);
			mContext.startActivity(intent);
			dismiss();
			break;

		}
	}

}
