package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.View;
import android.view.Window;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class RemoveDeviceDialog extends Dialog implements
		android.view.View.OnClickListener {
	private TextView mBack;
	private TextView mCommit;
	Context context;
	private UpdateRemoveDebiceDialogClickListener dialogClickListener;

	public interface UpdateRemoveDebiceDialogClickListener {
		void ItemDialogClickListener();
	}

	public void setOnUpdateDialogClickListener(
			UpdateRemoveDebiceDialogClickListener dialogClickListener)
	{
		this.dialogClickListener = dialogClickListener;
	}

	public RemoveDeviceDialog(Context context, String title)
	{
		super(context);
		this.context = context;
	}

	public RemoveDeviceDialog(Context context, int theme, String title)
	{
		super(context, theme);
		this.context = context;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		this.setContentView(R.layout.dialog_removedevice_layout);
		initView();
	}

	private void initView()
	{
		mBack = (TextView) this.findViewById(R.id.remove_textview_back);
		mCommit = (TextView) this.findViewById(R.id.remove_textview_commit);
		mBack.setOnClickListener(this);
		mCommit.setOnClickListener(this);
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.remove_textview_commit:
			this.dismiss();
			dialogClickListener.ItemDialogClickListener();
			break;
		case R.id.remove_textview_back:
			dismiss();
			break;
		default:
			break;
		}
	}
}
