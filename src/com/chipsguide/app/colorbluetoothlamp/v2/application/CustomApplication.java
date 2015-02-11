package com.chipsguide.app.colorbluetoothlamp.v2.application;

import android.app.Application;

import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class CustomApplication extends Application {
	public static final String APP_SIGN = "ColorBluetoothLamp_V2";
	/**
	 * 蓝牙地址过滤
	 */
	public static final String MAC_ADDRESS_FILTER_PREFIX = "C9:2";
	/**
	 * 蓝牙设备管理类
	 */
	private static BluetoothDeviceManager bluzDeviceMan;

	@Override
	public void onCreate() {
		super.onCreate();
	}
	
	
}
