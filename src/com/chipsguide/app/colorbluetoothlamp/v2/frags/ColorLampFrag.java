package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.graphics.Color;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnColorChangeListener;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager.OnBluetoothDeviceColorLampStatusChangedListener;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager.OnBluetoothDeviceCommonLampStatusChangedListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceManagerReadyListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class ColorLampFrag extends BaseFragment implements
		OnColorChangeListener {
	
	private int color;
	
	private BluetoothDeviceManager mBluetoothDeviceManager;
	private BluetoothDeviceCommonLampManager mBluetoothDeviceCommonLampManager;
	private BluetoothDeviceColorLampManager mBluetoothDeviceColorLampManager;
	
	private ImageView mColor01;
	private ImageView mColor02;
	private ImageView mColor03;
	private ImageView mColor04;
	private ImageView mColor05;

	@Override
	protected void initBase()
	{
		mBluetoothDeviceManager = ((CustomApplication) getActivity()
				.getApplicationContext()).getBluetoothDeviceManager();
		mBluetoothDeviceManager
				.setOnBluetoothDeviceManagerReadyListener(readyListener);
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.frag_color_lamp;
	}

	@Override
	protected void initView()
	{
		ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
		colorPicker.setOnColorChangeListener(this);
		mColor01 = (ImageView)this.findViewById(R.id.view_color_1);
		mColor02 = (ImageView)this.findViewById(R.id.view_color_2);
		mColor03 = (ImageView)this.findViewById(R.id.view_color_3);
		mColor04 = (ImageView)this.findViewById(R.id.view_color_4);
		mColor05 = (ImageView)this.findViewById(R.id.view_color_5);
	}

	@Override
	protected void initData()
	{
	}

	@Override
	public void onColorChange(int alpha, int red, int green, int blue)
	{
		color = Color.argb(alpha, red, green, blue);
//		findViewById(R.id.layout).setBackgroundColor(color);
	}

	@Override
	public void onColorChangeEnd(int alpha, int red, int green, int blue)
	{
		if (mBluetoothDeviceColorLampManager != null)
		{
			mBluetoothDeviceColorLampManager.setColor(red, green, blue);
		}
	}

	OnBluetoothDeviceManagerReadyListener readyListener = new OnBluetoothDeviceManagerReadyListener()
	{

		@Override
		public void onBluetoothDeviceManagerReady()
		{
			flog.e("onBluetoothDeviceManagerReady");
			mBluetoothDeviceCommonLampManager = mBluetoothDeviceManager
					.getBluetoothDeviceCommonLampManager();
			mBluetoothDeviceColorLampManager = mBluetoothDeviceManager
					.getBluetoothDeviceColorLampManager();

			mBluetoothDeviceColorLampManager
					.setOnBluetoothDeviceColorLampStatusChangedListener(new OnBluetoothDeviceColorLampStatusChangedListener()
					{
						/**
						 * commandType 是反馈还是查询 on 彩灯开关 colorlamp on-off
						 * brightness 彩灯亮度 （彩灯亮度不变化） red green blue RGB值 rhythm
						 * 灯效
						 */
						@Override
						public void onBluetoothDeviceColorLampStatusChanged(
								int commandType, boolean on, int brightness,
								int red, int green, int blue, int rhythm)
						{

						}
					});
			mBluetoothDeviceCommonLampManager
					.setOnBluetoothDeviceCommonLampStatusChangedListener(new OnBluetoothDeviceCommonLampStatusChangedListener()
					{

						@Override
						public void onBluetoothDeviceCommonLampStatusChanged(
								int commandType, boolean on, int brightness)
						{

						}
					});

			mBluetoothDeviceCommonLampManager.getLampstatus();
			mBluetoothDeviceColorLampManager.getStatus();
		}

	};

}
