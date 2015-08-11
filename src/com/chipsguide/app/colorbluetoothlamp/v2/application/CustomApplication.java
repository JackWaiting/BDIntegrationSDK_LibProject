package com.chipsguide.app.colorbluetoothlamp.v2.application;

import java.util.LinkedList;
import java.util.List;

import android.app.Activity;
import android.app.Application;

import com.chipsguide.app.colorbluetoothlamp.v2.utils.CrashHandler;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class CustomApplication extends Application {
	public static final String APP_SIGN = "ColorBluetoothLamp_V2";// 标识
	/**
	 * 蓝牙地址过滤C9:7
	 */
	public static final String MAC_ADDRESS_FILTER_PREFIX = "C9:7";
	/**
	 * 蓝牙设备管理类
	 */
	private static BluetoothDeviceManager bluzDeviceMan;

	// 提醒断开连接进入设置下连接
	public static boolean isToastDisconn = true;
	//连接第一次是否更新色盘  以后都不更新色盘
	public static boolean isUpdateColor = true;
	public static float lampMax = 16f;// 白灯亮度最大值
	
	public static int pageItem = 0;//跳转到对应的page页面

	private static int mMode = -1;
	private static Activity mActivity;
	private static List<Activity> activityList = new LinkedList<Activity>();
	
	public static boolean isClickAlarm = false;
	public static boolean isConnect = false; 

	@Override
	public void onCreate()
	{
		super.onCreate();
		 CrashHandler.getInstance().init(this);
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
							MAC_ADDRESS_FILTER_PREFIX).build();
		}
		return bluzDeviceMan;
	}

	// 添加Activity到容器中
	public static void addActivity(Activity activity)
	{
		mActivity = activity;
		activityList.add(activity);
	}
	
	public static Activity getActivity()
	{
		if(mActivity != null)
		{
			return mActivity;
		}
		return null;
	}
	
	public static void setMode (int mode)
	{
		mMode = mode; 
	}
	
	public static int getMode()
	{
		return mMode;
	}
}
