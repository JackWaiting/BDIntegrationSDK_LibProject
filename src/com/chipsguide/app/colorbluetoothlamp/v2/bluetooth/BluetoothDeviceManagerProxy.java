package com.chipsguide.app.colorbluetoothlamp.v2.bluetooth;

import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.graphics.Typeface;
import android.util.Log;

import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
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
	/**
	 * 连接状态监听集合
	 */
	private List<OnBluetoothDeviceConnectionStateChangedListener> conStateListeners;

	/**
	 * 蓝牙设备卡音乐管理类
	 */
	private IBluetoothDeviceMusicManager deviceMusicManager;

	private static int deviceManagerMode = -1;
	/**
	 * 蓝牙地址过滤
	 */
	private static final String MAC_ADDRESS_FILTER_PREFIX = "C9:60:01";

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

	private Context context;
	private BluetoothDeviceManagerProxy(Context context){
		this.context = context;
		getBluetoothDeviceManager();
		conStateListeners = new ArrayList<OnBluetoothDeviceConnectionStateChangedListener>();
	}
	public static BluetoothDeviceManagerProxy getInstance(Context context){
		if(proxy == null){
			proxy = new BluetoothDeviceManagerProxy(context);
		}
		return proxy;
	}
	
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
			if (bluzDeviceMan == null
					|| bluzDeviceMan
							.setBluetoothDevice(BluetoothDeviceManager.Device.LAMP_COLOR) == null || 
							bluzDeviceMan.setBluetoothDeviceSub(BluetoothDeviceManager.Device.LAMP_COMMON) == null) {
				return null;
			}
					
			bluzDeviceMan.build();
			bluzDeviceMan
					.setBluetoothDeviceMacAddressFilterPrefix(MAC_ADDRESS_FILTER_PREFIX);
			bluzDeviceMan
					.setOnBluetoothDeviceConnectionStateChangedListener(connStateChangeListener);
			bluzDeviceMan
					.setOnBluetoothDeviceGlobalUIChangedListener(globalUiChangedListener);
			bluzDeviceMan
					.setOnBluetoothDeviceHotplugChangedListener(hotplugChangedListener);
		}
		return bluzDeviceMan;
	}
	
	public boolean isConnected() {
		return connected;
	}
	
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
			localMusicManagerlistener.onMusicManagerReady(deviceMusicManager,
					deviceManagerMode);
		} else {
			bluzDeviceMan = getBluetoothDeviceManager();
			if(bluzDeviceMan == null){
				localMusicManagerlistener.onMusicManagerReadyFailed(priorityMode);
			}else{
				if (isMusicManagerMode(deviceManagerMode)) {
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
				localMusicManagerlistener
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
			if (ready) {
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

	private OnDeviceMusicManagerReadyListener remoteMusicManagerReadyListener;

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
		}
	}
	
	/**
	 * 添加连接状态改变监听
	 * 
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
			case BluetoothDeviceManager.ConnectionState.CONNECTED:
				connected = true;
				// bluzDeviceMan.setMode(BluetoothDeviceManager.Mode.A2DP);
				break;
			case BluetoothDeviceManager.ConnectionState.DISCONNECTED:
				connected = false;
				cardPlugFirstCallback = true;
				usbPlugFirstCallback = true;
				deviceMusicManager = null;
				firstModeChange = true;
				break;
			}
			notifyConntectionStateChanged(device, state);
		}
	};

	/**
	 * 
	 */
	private OnBluetoothDeviceGlobalUIChangedListener globalUiChangedListener = new OnBluetoothDeviceGlobalUIChangedListener() {

		@Override
		public void onBluetoothDeviceBatteryChanged(int arg0, boolean arg1) {
		}

		@Override
		public void onBluetoothDeviceEQChanged(int arg0) {
		}

		@Override
		public void onBluetoothDeviceModeChanged(int mode) {
			Log.d(TAG, ">>>mode change: mode == " + mode);
			switch (mode) {
			case BluetoothDeviceManager.Mode.CARD:
				bluzDeviceMan
						.setOnBluetoothDeviceCardMusicManagerReadyListener(cardMusicReadyListener);
				break;
			case BluetoothDeviceManager.Mode.USB:
				bluzDeviceMan
						.setOnBluetoothDeviceUsbMusicManagerReadyListener(usbMusicManagerReadyListener);
				break;
			case BluetoothDeviceManager.Mode.A2DP:
				deviceMusicManager = null;
				break;
			default:
				deviceMusicManager = null;
				break;
			}
			sendModeChangeBroadcast(mode, deviceManagerMode);
			deviceManagerMode = mode;
		}

		@Override
		public void onBluetoothDeviceVolumeChanged(int arg0, boolean arg1) {
		}

	};
	
	private void sendModeChangeBroadcast(int newMode, int oldMode) {
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

	boolean cardPlugFirstCallback = true;
	boolean usbPlugFirstCallback = true;
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
			localMusicManagerlistener.onMusicManagerReady(deviceMusicManager,
					deviceManagerMode);
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
		PlayerManager.getInstance(context).destoryAll();
		changeToA2DPMode();
		conStateListeners.clear();
		if (bluzDeviceMan != null) {
			bluzDeviceMan.release();
			if (bluzDeviceMan.isDiscovering()) {
				bluzDeviceMan.cancelDiscovery();
			}
		}
		bluzDeviceMan = null;
		disconnected();
		deviceMusicManager = null;
		proxy = null;
	}

}
