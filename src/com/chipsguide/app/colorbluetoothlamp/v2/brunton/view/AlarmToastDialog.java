package com.chipsguide.app.colorbluetoothlamp.v2.brunton.view;

import android.app.Dialog;
import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.View;
import android.widget.Button;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceAlarmManager;

public class AlarmToastDialog extends Dialog implements android.view.View.OnClickListener{

	private Button ok;
	private Button delay;
	private BluetoothDeviceManagerProxy proxy;
	private BluetoothDeviceAlarmManager alarmManager;
	public AlarmToastDialog(Context context)
	{
		this(context, 0);
	}

	public AlarmToastDialog(Context context, int theme)
	{
		super(context, theme);
		proxy = BluetoothDeviceManagerProxy.getInstance(context);
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.layout_dialog_alarm_toast);
	}

	@Override
	public void onAttachedToWindow()
	{
		super.onAttachedToWindow();
		initView();
	}
	
	private void initView()
	{
		ok = (Button) this
				.findViewById(R.id.button_ok);
		delay = (Button) this
				.findViewById(R.id.button_delay);
		ok.setOnClickListener(this);
		delay.setOnClickListener(this);
		alarmManager = proxy.getBluetoothDeviceManager().getBluetoothDeviceAlarmManager();
	}
	
	@Override
	public boolean onKeyDown(int keyCode, KeyEvent event)
	{
		if (keyCode == KeyEvent.KEYCODE_BACK
                && event.getRepeatCount() == 0) {
			if(alarmManager != null)
			{
				alarmManager.delay();
			}
            return false;
        }
		return true;
	}

	@Override
	public void onClick(View v)
	{
		switch (v.getId())
		{
		case R.id.button_ok:
			if(alarmManager != null)
			{
				alarmManager.turnOff();
			}
			break;
		case R.id.button_delay:
			if(alarmManager != null)
			{
				alarmManager.delay();
			}
			break;
		}
	}
	
	@Override
	public void dismiss()
	{
		super.dismiss();
		if(alarmManager != null)
		{
			alarmManager.turnOff();
		}
	}

}
