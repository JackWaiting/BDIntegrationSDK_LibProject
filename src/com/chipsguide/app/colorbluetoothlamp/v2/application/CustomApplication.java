package com.chipsguide.app.colorbluetoothlamp.v2.application;

import android.app.Application;

import com.chipsguide.app.colorbluetoothlamp.v2.utils.CrashHandler;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class CustomApplication extends Application {
	public static final String APP_SIGN = "ColorBluetoothLamp_V2";
	//public static final String APP_SIGN = "Test_VehicularMp3Chipsguide";
	/**
	 * 蓝牙地址过滤
	 */
	public static final String MAC_ADDRESS_FILTER_PREFIX = "";
	/**
	 * 蓝牙设备管理类
	 */
	private static BluetoothDeviceManager bluzDeviceMan;
	
	//提示第一次进入，判断蓝牙是否连接
	public static boolean isFirstConnect = true;

	@Override
	public void onCreate()
	{
		super.onCreate();
//		CrashHandler.getInstance().init(this);
	}

	/**
	 * 获取蓝牙管理类
	 * 
	 * @return
	 */
	public BluetoothDeviceManager getBluetoothDeviceManager()
	{
		if (bluzDeviceMan == null)
		{
			bluzDeviceMan = BluetoothDeviceManager
					.getInstance(this.getApplicationContext())
					.setBluetoothDevice(
							BluetoothDeviceManager.Device.LAMP_COLOR)
					.setBluetoothDeviceSub(
							BluetoothDeviceManager.Device.LAMP_COMMON)
							
					.setBluetoothDeviceMacAddressFilterPrefix(
							MAC_ADDRESS_FILTER_PREFIX)
							.build();
		}
		return bluzDeviceMan;
	}
}
