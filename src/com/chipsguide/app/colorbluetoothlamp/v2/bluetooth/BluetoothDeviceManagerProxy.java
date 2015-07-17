package com.chipsguide.app.colorbluetoothlamp.v2.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;

import com.chipsguide.app.colorbluetoothlamp.v2.activity.MainActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.MyLogger;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceCardMusicManagerReadyListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceGlobalUIChangedListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceHotplugChangedListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceUsbMusicManagerReadyListener;
import com.chipsguide.lib.bluetooth.interfaces.templets.IBluetoothDeviceMusicManager;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceCardMusicManager;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager.DevicePlugglable;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceUsbMusicManager;

public class BluetoothDeviceManagerProxy{
	
	MyLogger flog = MyLogger.fLog();
	
	public static final String TAG = "BluetoothDeviceManagerProxy";
	public static BluetoothDeviceManagerProxy proxy;
	/**
	 * 广播，tf卡热插拔事件
	 */
	public static final String ACTION_TF_CARD_PLUG_CHANGED = "tf_card_plug_changed";
	/**
	 * 广播，usb热插拔事件
	 */
	public static final String ACTION_USB_PLUG_CHANGED = "usb_plug_changed";

	public static final String ACTION_MODE_CHANGE = "mode_change";

	public static final String EXTRA_PLUG_IN = "plug_in";

	public static final String EXTRA_NEW_MODE = "new_mode";

	public static final String EXTRA_OLD_MODE = "old_mode";

	public static final String EXTRA_FIRST_MODE_CHANGE = "first_time_mode_change";

	public static final String EXTRA_FIRST_CARD_PLUG = "first_time_plug_in";
	/**
	 * 在应用内切换的模式
	 */
	public static final String EXTRA_MODE_CHANGE_IN_APP = "mode_change_in_app";

	public static Typeface fontFangzheng;
	/**
	 * 是否已连接
	 */
	private boolean connected;
	/**
	 * 蓝牙设备管理类
	 */
	private static BluetoothDeviceManager bluzDeviceMan;
	/**
	 * 已连接的蓝牙设备
	 */
	private BluetoothDevice connectedDevice;
	private LampManager mLampManager;
	/**
	 * 连接状态监听集合
	 */
	private List<OnBluetoothDeviceConnectionStateChangedListener> conStateListeners;

	/**
	 * 蓝牙设备卡音乐管理类
	 */
	private IBluetoothDeviceMusicManager deviceMusicManager;

	private static int deviceManagerMode = -1;

	private boolean hasTfCard;
	private boolean hasUsb;
	/**
	 * 设置设备音乐，优先选择的模式
	 */
	private int priorityMode = -1;
	/**
	 * 是否为第一次（连接成功后）模式变化
	 */
	public static boolean firstModeChange = true;
	/**
	 * 播放管理器
	 */
	private PlayerManager playerManager;
	
	private Context context;
	private BluetoothDeviceManagerProxy(Context context){
		this.context = context;
		getBluetoothDeviceManager();//获取蓝牙管理类
		conStateListeners = new ArrayList<OnBluetoothDeviceConnectionStateChangedListener>();//初始化连接状态监听的集合
		playerManager = PlayerManager.getInstance(context);
		mLampManager = LampManager.getInstance(context);
	}
	//单列
	public static BluetoothDeviceManagerProxy getInstance(Context context){
		if(proxy == null){
			proxy = new BluetoothDeviceManagerProxy(context);
		}
		return proxy;
	}
	//设置蓝牙管理器
	public void setBluetoothDeviceManager(BluetoothDeviceManager manager) {
		bluzDeviceMan = manager;
	}

	/**
	 * 获取蓝牙管理类
	 * 
	 * @return
	 */
	private BluetoothDeviceManager getBluetoothDeviceManager() {
		if (bluzDeviceMan == null) {
			bluzDeviceMan = BluetoothDeviceManager.getInstance(context);
			//若获取蓝牙和灯的颜色，普通灯都为空就返回
			if (bluzDeviceMan == null
					|| bluzDeviceMan.setBluetoothDevice(BluetoothDeviceManager.Device.LAMP_COLOR) == null || 
					bluzDeviceMan.setBluetoothDeviceSub(BluetoothDeviceManager.Device.LAMP_COMMON) == null) {
				return null;
			}
			bluzDeviceMan.build();//创建
			bluzDeviceMan//蓝牙地址过滤
			.setBluetoothDeviceMacAddressFilterPrefix(CustomApplication.MAC_ADDRESS_FILTER_PREFIX);
			bluzDeviceMan//连接状态监听
			.setOnBluetoothDeviceConnectionStateChangedListener(connStateChangeListener);
			bluzDeviceMan//UI改变监听
			.setOnBluetoothDeviceGlobalUIChangedListener(globalUiChangedListener);
			bluzDeviceMan//热插拔监听
			.setOnBluetoothDeviceHotplugChangedListener(hotplugChangedListener);
		}
		return bluzDeviceMan;
	}
	//保持连接
	public boolean isConnected() {
		return connected;
	}
	//插入TF卡是否保持连接
	public boolean isPlugTFCard(){
		if(bluzDeviceMan == null){
			Log.d(TAG, "bluzDeviceMan == null");
			return false;
		}
		return bluzDeviceMan.isPlugIn(DevicePlugglable.CARD) && isConnected();
	}

	/**
	 * 是否处于音乐相关模式（）
	 * @return
	 */
	public int getMusicManagerMode() {
		if(isMusicManagerMode(deviceManagerMode)){
			return deviceManagerMode;
		}
		return -1;
	}

	/**
	 * 获取卡播放管理类，如果当前的模式是音乐管理相关模式（CARD,USB），则使用上次的管理类<br>
	 * @return
	 */
	public void getBluetoothDeviceMusicManager() {	
		if (bluzDeviceMan != null && deviceMusicManager != null && localMusicManagerlistener != null
				&& isMusicManagerMode(deviceManagerMode)) {
		//	自定义蓝牙设备音乐准备回调监听 音乐管理准备好的
			localMusicManagerlistener.onMusicManagerReady(deviceMusicManager,
					deviceManagerMode);
		} else {
			bluzDeviceMan = getBluetoothDeviceManager();//获取蓝牙管理类
			if(bluzDeviceMan == null){
				localMusicManagerlistener.onMusicManagerReadyFailed(priorityMode);//设置优先选择的模式
			}else{
				if (isMusicManagerMode(deviceManagerMode)) {//是否为音乐管理相关模式
					bluzDeviceMan.setMode(deviceManagerMode);
				} else {
					if (bluzDeviceMan
							.isPlugIn(DevicePlugglable.USB)) {
						priorityMode = BluetoothDeviceManager.Mode.USB;
						if (bluzDeviceMan != null) {
							bluzDeviceMan.setMode(priorityMode);
						}
					} else if (bluzDeviceMan
							.isPlugIn(DevicePlugglable.CARD)) {
						priorityMode = BluetoothDeviceManager.Mode.CARD;
						if (bluzDeviceMan != null) {
							bluzDeviceMan.setMode(priorityMode);
						}
					} 
				}
			}
		}
	}

	/**
	 * 是否为音乐管理相关模式
	 * 
	 * @param mode
	 * @return
	 */
	private boolean isMusicManagerMode(int mode) {
		return (mode == BluetoothDeviceManager.Mode.CARD || mode == BluetoothDeviceManager.Mode.USB);
	}

	public boolean isInMusicManagerMode(){
		return isMusicManagerMode(deviceManagerMode);
	}

	/**
	 * 获取卡播放管理类,并设置模式
	 * 
	 * @param readyListener
	 * @param cardMusicManagerMode
	 *            BluetoothDeviceManager.Mode.CARD或者BluetoothDeviceManager.Mode.
	 *            USB
	 */
	public void getBluetoothDeviceMusicManager(int cardMusicManagerMode) {
		switch (cardMusicManagerMode) {
		case BluetoothDeviceManager.Mode.CARD: // 是否能设置卡模式       
			if (!bluzDeviceMan
					.isPlugIn(DevicePlugglable.CARD)) {
				localMusicManagerlistener   //自定义蓝牙设备音乐准备回调监听设置失败
				.onMusicManagerReadyFailed(cardMusicManagerMode);
				return;
			}
			break;
		case BluetoothDeviceManager.Mode.USB: // 是否能设置usb模式
			if (!bluzDeviceMan
					.isPlugIn(DevicePlugglable.USB)) {
				localMusicManagerlistener
				.onMusicManagerReadyFailed(cardMusicManagerMode);
				return;
			}
			break;
		}
		boolean ready = false;
		if (deviceMusicManager != null) {
			if (deviceMusicManager instanceof BluetoothDeviceCardMusicManager
					&& deviceManagerMode == BluetoothDeviceManager.Mode.CARD
					&& cardMusicManagerMode == deviceManagerMode) {
				ready = true;
			} else if (deviceMusicManager instanceof BluetoothDeviceUsbMusicManager
					&& deviceManagerMode == BluetoothDeviceManager.Mode.USB
					&& cardMusicManagerMode == deviceManagerMode) {
				ready = true;
			}
			if (ready) {//为true 设置音乐管理类准备好
				localMusicManagerlistener.onMusicManagerReady(
						deviceMusicManager, deviceManagerMode);
				return;
			}
		}
		if (bluzDeviceMan != null) {
			bluzDeviceMan.setMode(cardMusicManagerMode);
		} else {
			localMusicManagerlistener
			.onMusicManagerReadyFailed(cardMusicManagerMode);
		}
	}

	private OnDeviceMusicManagerReadyListener remoteMusicManagerReadyListener;//蓝牙音乐管理准备的监听

	public void setOnBluetoothDeviceMuisicReadyListener(
			OnDeviceMusicManagerReadyListener listener) {
		remoteMusicManagerReadyListener = listener;
	}

	/**
	 * 自定义蓝牙设备音乐准备回调监听
	 */

	private OnDeviceMusicManagerReadyListener localMusicManagerlistener = new OnDeviceMusicManagerReadyListener() {

		@Override
		public void onMusicManagerReady(IBluetoothDeviceMusicManager manager,
				int mode) {
			if (remoteMusicManagerReadyListener != null) {
				remoteMusicManagerReadyListener.onMusicManagerReady(manager,
						mode);
			}
		}

		@Override
		public void onMusicManagerReadyFailed(int mode) {
			if (remoteMusicManagerReadyListener != null) {
				remoteMusicManagerReadyListener.onMusicManagerReadyFailed(mode);
			}
		}

	};

	/**
	 * 获取设备音乐的模式
	 * 
	 * @return
	 */
	public int getBluetoothManagerMode() {
		return deviceManagerMode;
	}


	private static boolean changeToA2DPInApp;
	/**
	 * 改为A2DP模式
	 */
	public static void changeToA2DPMode() {
		if (bluzDeviceMan != null
				&& deviceManagerMode != BluetoothDeviceManager.Mode.A2DP) {
			changeToA2DPInApp = true;
			bluzDeviceMan.setMode(BluetoothDeviceManager.Mode.A2DP);
			Log.e(TAG, "changeToA2DPMode");
		}
	}
	
	/**
	 * 改为
	 */
	public void changeToAlarm() {
		if (bluzDeviceMan != null) {
			bluzDeviceMan.setMode(BluetoothDeviceManager.Mode.ALARM);
		}
	}

	public interface OnDeviceUiChangedListener{
		void onVolumeChanged(boolean firstCallback, int volume, boolean on);
	}

	public interface OnDeviceConnectedStateChangedListener{
		void onConnectedChanged(boolean connected);
	}
	
	public static class SimpleDeviceUiChangedListener implements OnDeviceUiChangedListener{
		@Override
		public void onVolumeChanged(boolean firstCallback, int volume, boolean on) {
		}
	}

	public static class SimpleDeviceConnectStateChangedListener implements OnDeviceConnectedStateChangedListener{
		@Override
		public void onConnectedChanged(boolean isConnect) {
		}
	}

	private OnDeviceUiChangedListener mDeviceUiChangedListener;
	private OnDeviceConnectedStateChangedListener mDeviceConnectedStateChangedListener;
	/**
	 * 添加设备UI相关变化监听，如音量
	 * 注：退出界面时，调用removeDeviceUiChangedListener
	 * @param listener
	 */
	public void setDeviceUiChangedListener(OnDeviceUiChangedListener listener) {
		mDeviceUiChangedListener = listener;
		if(mDeviceUiChangedListener != null){
			mDeviceUiChangedListener.onVolumeChanged(true, currentVolume, true);
		}
	}

	/**
	 * 添加设备连接变化
	 * 注：退出界面时，调用removeDeviceConnectedStateChangedListener
	 * @param listener
	 */
	public void setDeviceConnectedStateChangedListener(OnDeviceConnectedStateChangedListener listener) {
		mDeviceConnectedStateChangedListener = listener;
		if(mDeviceConnectedStateChangedListener != null){
			mDeviceConnectedStateChangedListener.onConnectedChanged(connected);
		}
	}
	/**
	 * 移除
	 */
	public void removeDeviceUiChangedListener() {
		mDeviceUiChangedListener = null;
	}

	/**
	 * 移除
	 */
	public void removeDeviceConnectedStateChangedListener() {
		mDeviceConnectedStateChangedListener = null;
	}


	private int currentVolume;
	/**
	 * 调节设备音量
	 * @param volume 百分比，0 - 32;
	 */
	public void adjustVolume(int volume) {
		if(bluzDeviceMan != null && isConnected()){
			volume = Math.min(100, volume);
			volume = Math.max(0, volume);
			currentVolume = volume;
			bluzDeviceMan.setVolume(volume);
		}
	}
	/*
	 * 获取设备音量
	 */
	public int getCurrentVolume() {
		return currentVolume;
	}

	/**
	 * 添加连接状态改变监听
	 * 如果添加的类为Activity、Fragment或其中的非静态内部类，<br>
	 * 那么在Activity、Fragment销毁时，一定要调用{@link BluetoothDeviceManagerProxy#removeOnBluetoothDeviceConnectionStateChangedListener(OnBluetoothDeviceConnectionStateChangedListener)}}
	 * 将其移除，否则会引起内存泄露
	 * @param listener
	 */
	public void addOnBluetoothDeviceConnectionStateChangedListener(
			OnBluetoothDeviceConnectionStateChangedListener listener) {
		conStateListeners.add(listener);
	}

	public void removeOnBluetoothDeviceConnectionStateChangedListener(
			OnBluetoothDeviceConnectionStateChangedListener listener) {
		conStateListeners.remove(listener);
	}

	/**
	 * 通知监听器，蓝牙连接状态改变
	 * 
	 * @param device
	 * @param state
	 */
	private void notifyConntectionStateChanged(BluetoothDevice device, int state) {
		int size = conStateListeners.size();
		for (int i = 0; i < size; i++) {
			conStateListeners.get(i).onBluetoothDeviceConnectionStateChanged(
					device, state);
		}
	}

	/**
	 * 断开蓝牙连接
	 */
	public void disconnected() {
		if (bluzDeviceMan != null && connected) {
			bluzDeviceMan.disconnect(connectedDevice);
		}
	}

	/**
	 * 蓝牙连接状态改变监听
	 */
	private OnBluetoothDeviceConnectionStateChangedListener connStateChangeListener = new OnBluetoothDeviceConnectionStateChangedListener() {

		@Override
		public void onBluetoothDeviceConnectionStateChanged(
				BluetoothDevice device, int state) {
			connectedDevice = device;
			switch (state) {
			case BluetoothDeviceManager.ConnectionState.CONNECTED://保持连接
				connected = true;
				// bluzDeviceMan.setMode(BluetoothDeviceManager.Mode.A2DP);
				break;
			case BluetoothDeviceManager.ConnectionState.DISCONNECTED://断开连接
				connected = false;
				cardPlugFirstCallback = true;//插入卡的回调 true
				usbPlugFirstCallback = true;
				deviceMusicManager = null;//卡音乐管理为空
				firstModeChange = true;//是否为第一次（连接成功后）模式变化
				volumeFirstCallback = true;//音量第一次的回调
				break;
			}
			notifyConntectionStateChanged(device, state);
			if(mDeviceConnectedStateChangedListener != null){
				//连接状态的改变
				mDeviceConnectedStateChangedListener.onConnectedChanged(connected);
			}
		}
	};

	private boolean volumeFirstCallback = true;//音量第一次的回调
	/**
	 * 
	 *///全局IU跟新的监听
	private OnBluetoothDeviceGlobalUIChangedListener globalUiChangedListener = new OnBluetoothDeviceGlobalUIChangedListener() {

		@Override
		public void onBluetoothDeviceBatteryChanged(int arg0, boolean arg1) {
		}

		@Override
		public void onBluetoothDeviceEQChanged(int arg0) {
		}

		@Override
		public void onBluetoothDeviceModeChanged(int mode) {
			flog.e( ">>>mode change: mode == " + mode);
			CustomApplication.setMode(mode);
			connected = true;
			switch (mode) {
			case BluetoothDeviceManager.Mode.CARD:
				bluzDeviceMan//卡模式管理类准备监听
				.setOnBluetoothDeviceCardMusicManagerReadyListener(cardMusicReadyListener);
				mLampManager.effect2normal(true);
				mode2view();
				break;
			case BluetoothDeviceManager.Mode.USB:
				bluzDeviceMan
				.setOnBluetoothDeviceUsbMusicManagerReadyListener(usbMusicManagerReadyListener);
				mLampManager.effect2normal(true);
				mode2view();
				break;
			case BluetoothDeviceManager.Mode.A2DP:
				deviceMusicManager = null;
				mode2view();
				break;
			case BluetoothDeviceManager.Mode.LINE_IN:
				deviceMusicManager = null;
				if(playerManager.isPlaying())
				{
					playerManager.pause();
				}
				mLampManager.effect2normal(true);
				mode2view();
				break;
			default:
				deviceMusicManager = null;
				break;
			}
			if(mOnModeChangedListener != null)
			{
				mOnModeChangedListener.onModeChanged(mode);
			}
			sendModeChangeBroadcast(mode, deviceManagerMode);
			deviceManagerMode = mode;
		}

		private void mode2view()
		{
			if(!(CustomApplication.getActivity() instanceof MainActivity))
			{
//				context.startActivity(new Intent(context,MainActivity.class));
				CustomApplication.getActivity().finish();
			}
		}

		@Override
		public void onBluetoothDeviceVolumeChanged(int volume, boolean on) {
			currentVolume = volume;
			if(volumeFirstCallback){
				volumeFirstCallback = false;
			}
			if(mDeviceUiChangedListener != null){
				mDeviceUiChangedListener.onVolumeChanged(volumeFirstCallback, volume, on);
			}
		}
	};

	private void sendModeChangeBroadcast(int newMode, int oldMode) {//通知模式改变
		Intent intent = new Intent(ACTION_MODE_CHANGE);
		intent.putExtra(EXTRA_NEW_MODE, newMode);
		intent.putExtra(EXTRA_OLD_MODE, oldMode);
		intent.putExtra(EXTRA_FIRST_MODE_CHANGE, firstModeChange);
		intent.putExtra(EXTRA_MODE_CHANGE_IN_APP, changeToA2DPInApp);
		context.sendOrderedBroadcast(intent, null);

		changeToA2DPInApp = false;
		if(firstModeChange){
			firstModeChange = false;
		}
	}

	private boolean cardPlugFirstCallback = true;
	private boolean usbPlugFirstCallback = true;
	private OnBluetoothDeviceHotplugChangedListener hotplugChangedListener = new OnBluetoothDeviceHotplugChangedListener() {

		@Override
		public void onBluetoothDeviceCardPlugChanged(boolean arg0) {
			Log.d(TAG, ">>>card hot plug = " + arg0);
			hasTfCard = arg0;
			selectPriorityMode();
			Intent intent = new Intent(ACTION_TF_CARD_PLUG_CHANGED);
			intent.putExtra(EXTRA_FIRST_CARD_PLUG, cardPlugFirstCallback);
			intent.putExtra(EXTRA_PLUG_IN, arg0);
			context.sendOrderedBroadcast(intent, null);
			if(cardPlugFirstCallback){
				cardPlugFirstCallback = false;
			}
		}

		@Override
		public void onBluetoothDeviceLineinPlugChanged(boolean arg0) {
			// TODO line in插入、拔出时的相应操作
		}

		@Override
		public void onBluetoothDeviceUhostPlugChanged(boolean arg0) {
			// TODO U盘插入、拔出的时相应的操作
			Log.d(TAG, ">>>usb hot plug = " + arg0);
			if(usbPlugFirstCallback){
				usbPlugFirstCallback = false;
				return;
			}
			hasUsb = arg0;
			selectPriorityMode();
			Intent intent = new Intent(ACTION_USB_PLUG_CHANGED);
			intent.putExtra(EXTRA_PLUG_IN, arg0);
			context.sendOrderedBroadcast(intent, null);
		}
	};

	/**
	 * 设置设备音乐优先模式,usb优先
	 */
	private void selectPriorityMode() {
		if (hasUsb) {
			priorityMode = BluetoothDeviceManager.Mode.USB;
		} else if (hasTfCard) {
			priorityMode = BluetoothDeviceManager.Mode.CARD;
		} else {
			priorityMode = -1;
		}
		//deviceManagerMode = priorityMode;
	}

	/**
	 * 卡模式管理类准备监听
	 */
	private OnBluetoothDeviceCardMusicManagerReadyListener cardMusicReadyListener = new OnBluetoothDeviceCardMusicManagerReadyListener() {
		@Override
		public void onBluetoothDeviceCardMusicManagerReady() {
			Log.d(TAG, ">>>Bluetooth Device Card Music Manager Ready");
			deviceMusicManager = bluzDeviceMan
					.getBluetoothDeviceCardMusicManager();
			if(deviceMusicManager == null){
				localMusicManagerlistener.onMusicManagerReadyFailed(deviceManagerMode);
			}else{
				localMusicManagerlistener.onMusicManagerReady(deviceMusicManager,
						deviceManagerMode);
			}
		}
	};

	private OnBluetoothDeviceUsbMusicManagerReadyListener usbMusicManagerReadyListener = new OnBluetoothDeviceUsbMusicManagerReadyListener() {

		@Override
		public void onBluetoothDeviceUsbMusicManagerReady() {
			Log.d(TAG, ">>>Bluetooth Device Usb Music Manager Ready");
			deviceMusicManager = bluzDeviceMan
					.getBluetoothDeviceUsbMusicManager();
			localMusicManagerlistener.onMusicManagerReady(deviceMusicManager,
					deviceManagerMode);
		}
	};

	/**
	 * 释放蓝牙，播放器等资源
	 */
	public void destory() {
		changeToA2DPMode();
		conStateListeners.clear();
		removeDeviceUiChangedListener();
		removeModeChangedListener();
		bluzDeviceMan = null;
		disconnected();
		deviceMusicManager = null;
		proxy = null;
		if (bluzDeviceMan != null) {
			if (bluzDeviceMan.isDiscovering()) {
				bluzDeviceMan.cancelDiscovery();
			}
			bluzDeviceMan.setOnBluetoothDeviceConnectionStateChangedListener(null);
			bluzDeviceMan.setOnBluetoothDeviceGlobalUIChangedListener(null);
			bluzDeviceMan.release();
		}
	}

	private OnModeChangedListener mOnModeChangedListener;
	public interface OnModeChangedListener{
		void onModeChanged(int mode);
	}
	
	public void setOnModeChangedListener(OnModeChangedListener listener) {
		mOnModeChangedListener = listener;
	}
	public void removeModeChangedListener()
	{
		mOnModeChangedListener = null;
	}
	
}
