package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.app.Dialog;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class ErrorToastDialog extends Dialog implements android.view.View.OnClickListener
{
	private ImageView mErrorOK;
	
	private Context context;
	
	public ErrorToastDialog(Context context)
	{
		this(context,0);
		this.context = context;
	}

	public ErrorToastDialog(Context context, int theme)
	{
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_toast_layout);
		initView();
	}
	
	private void initView()
	{
		mErrorOK = (ImageView)this.findViewById(R.id.imageview_error_ok);
		mErrorOK.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.imageview_error_ok:
			dismiss();
			context.startActivity(new Intent(android.provider.Settings.ACTION_BLUETOOTH_SETTINGS));
			break;
		default:
			dismiss();
			break;
		}
	}
}
