package com.chipsguide.app.colorbluetoothlamp.v2.utils;import java.util.ArrayList;import java.util.List;import java.util.Random;import android.bluetooth.BluetoothDevice;import android.content.Context;import android.graphics.Color;import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager.OnBluetoothDeviceColorLampStatusChangedListener;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager.OnBluetoothDeviceCommonLampStatusChangedListener;import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceManagerReadyListener;import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;public class LampManager implements OnBluetoothDeviceManagerReadyListener,		OnBluetoothDeviceCommonLampStatusChangedListener,		OnBluetoothDeviceColorLampStatusChangedListener {	MyLogger flog = MyLogger.fLog();	private static LampManager mLampManager;	private Context mContext;	private BluetoothDeviceManager mBluetoothDeviceManager;	private BluetoothDeviceCommonLampManager mBluetoothDeviceCommonLampManager;	private BluetoothDeviceColorLampManager mBluetoothDeviceColorLampManager;	private boolean colorLamp = false;// 彩灯	private boolean isLampState = false;// 灯的开关状态	public static int THYHM = 0;	//音乐律动模式下，控制颜色转化为普通模式	private boolean flag = true;//	/**	 * 彩灯状态监听集合	 */	private List<LampManager.LampListener> lampListeners = new ArrayList<LampManager.LampListener>();		private LampManager(Context context)	{		mContext = context;	}	public static LampManager getInstance(Context context)	{		if (mLampManager == null)		{			mLampManager = new LampManager(context);		}		return mLampManager;	}	public void init()	{		mBluetoothDeviceManager = ((CustomApplication) mContext				.getApplicationContext()).getBluetoothDeviceManager();		mBluetoothDeviceManager.setOnBluetoothDeviceManagerReadyListener(this);	}		public BluetoothDevice getBluetoothDevice()	{		if (mBluetoothDeviceManager != null)		{			return mBluetoothDeviceManager.getBluetoothDeviceConnectedA2dp();		}		return null;	}	@Override	public void onBluetoothDeviceManagerReady()	{		flog.d("onBluetoothDeviceManagerReady");		mBluetoothDeviceCommonLampManager = mBluetoothDeviceManager				.getBluetoothDeviceCommonLampManager();		mBluetoothDeviceColorLampManager = mBluetoothDeviceManager				.getBluetoothDeviceColorLampManager();		mBluetoothDeviceColorLampManager				.setOnBluetoothDeviceColorLampStatusChangedListener(this);		mBluetoothDeviceCommonLampManager				.setOnBluetoothDeviceCommonLampStatusChangedListener(this);		getStatus();	}	/**	 * commandType是反馈还是查询 on彩灯开关colorlamp【on-off】 brightness彩灯亮度（彩灯亮度不变化）	 * red-green-blue【RGB值】 rhythm灯效	 */	@Override	public void onBluetoothDeviceColorLampStatusChanged(int commandType,			boolean on, int brightness, int red, int green, int blue, int rhythm)	{		flog.d("彩灯 commandType " + commandType + " on : " + on);		flog.d("rhythm:" + rhythm);		isLampState = on;		colorLamp = true;		THYHM = rhythm;		commandTypeBack(commandType);		notifyLampRhythm(rhythm,red,green,blue);		notifyLampColor(red, green, blue);	}	/**	 * commandType反馈or查询 on白灯开关 brightness白灯开关	 */	@Override	public void onBluetoothDeviceCommonLampStatusChanged(int commandType,			boolean on, int brightness)	{		flog.d("白灯 commandType " + commandType + " on : " + on);		isLampState = on;		colorLamp = false;		commandTypeBack(commandType);		notifyLampBrightness(brightness);	}	private void commandTypeBack(int commandType)	{		switch (commandType)		{		case BluetoothDeviceColorLampManager.CommandType.INQUIRY:			if (isLampState)			{				onLampStateInqiryBackChange(colorLamp, isLampState);			}			break;		case BluetoothDeviceColorLampManager.CommandType.FEEDBACK:			onLampStateFeedBackChange(colorLamp, isLampState);			break;		}	}	/**	 * @param color	 */	public void setColor(int color)	{		setColor(color,flag);	}		/**	 * @param color	 * @param flag true 音乐律动模式下，控制颜色转化为普通模式	 */	public void setColor(int color,boolean flag)	{		isRhythm(flag);		setColor(Color.red(color), Color.green(color), Color.blue(color),flag);	}	/**	 * @param flag true 音乐律动，彩虹，呼吸模式下	 * 控制颜色转化为普通模式	 */	private void isRhythm(boolean flag)	{				if (THYHM == BluetoothDeviceColorLampManager.Effect.RAINBOW && flag)		{			setLampEffect(BluetoothDeviceColorLampManager.Effect.NORMAL);		}		if (THYHM == BluetoothDeviceColorLampManager.Effect.PULSE && flag)		{			setLampEffect(BluetoothDeviceColorLampManager.Effect.NORMAL);		}		if (THYHM == BluetoothDeviceColorLampManager.Effect.RHYTHM && flag)		{			setLampEffect(BluetoothDeviceColorLampManager.Effect.NORMAL);		}	}	/**	 * @param red	 * @param green	 * @param blue	 */	public void setColor(int red, int green, int blue)	{		setColor(red, green, blue, flag);	}		/**	 * @param red	 * @param green	 * @param blue	 */	public void setColor(int red, int green, int blue,boolean flag)	{		isRhythm(flag);		if (mBluetoothDeviceColorLampManager != null)		{			if (red == 255 && green == 255 && blue == 255)			{				turnCommonOn();				return;			}			mBluetoothDeviceColorLampManager.setColor(red, green, blue);		}	}	/**	 * @param brightness	 */	public void setBrightness(int brightness)	{		flog.d("setBrightness bai" + brightness);		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceCommonLampManager.setBrightness(brightness);		}	}	/**	 * @param effect灯效	 */	public void setLampEffect(int effect)	{		setLampEffect(effect, 0);	}	/**	 * @param effect灯效	 * @param velocity灯效速度	 *            [2-49]	 */	public void setLampEffect(int effect, int velocity)	{		if (mBluetoothDeviceColorLampManager != null)		{			if (velocity == 0)			{				mBluetoothDeviceColorLampManager.setEffect(effect);			}else			{				mBluetoothDeviceColorLampManager				.setLampEffect(effect, velocity);			}		}	}	public void turnCommonOn()	{		if (mBluetoothDeviceCommonLampManager != null)		{			mBluetoothDeviceCommonLampManager.turnOn();		}	}	public void turnCommonOff()	{		if (mBluetoothDeviceCommonLampManager != null)		{			mBluetoothDeviceCommonLampManager.turnOff();		}	}	public void turnColorOn()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager.turnOn();		}	}	public void turnColorOff()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager.turnOff();		}	}	/**	 * @param flag true 音乐律动模式下，控制颜色转化为普通模式	 */	public void randomColor(boolean flag)	{		Random r = new Random();		int red = r.nextInt(255) + 1;// 范围是[0+1,255)		int green = r.nextInt(255) + 1;// 范围是[0+1,255)		int blue = r.nextInt(255) + 1;// 范围是[0+1,255)		setColor(red, green, blue);	}	public void lampOn()	{		if (colorLamp)		{			turnColorOn();		} else		{			turnCommonOn();		}	}	public void lampOff()	{		if (colorLamp)		{			turnColorOff();		} else		{			turnCommonOff();		}	}	/**	 * 开关灯	 */	public void LampOnorOff()	{		if (isLampState)		{			lampOff();		} else		{			lampOn();		}	}	public void getStatus()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager					.getStatus(BluetoothDeviceColorLampManager.StatusType.STATUS);			mBluetoothDeviceCommonLampManager.getLampstatus();		}	}	@Override	public void onBluetoothDeviceColorLampStatusChanged(int arg0, boolean arg1,			int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,			int arg8, int arg9, int arg10)	{		// TODO Auto-generated method stub		flog.d("--------------");	}		/**	 * 添加灯效监听状态	 * 如果添加的类为Activity、Fragment或其中的非静态内部类，<br>	 * 那么在Activity、Fragment销毁时，一定要调用{@link BluetoothDeviceManagerProxy#removeOnBluetoothDeviceConnectionStateChangedListener(OnBluetoothDeviceConnectionStateChangedListener)}}	 * 将其移除，否则会引起内存泄露	 * @param listener	 */	public void addOnBluetoothDeviceLampListener(			LampListener listener) {		lampListeners.add(listener);	}	public void removeOnBluetoothDeviceLampListener(			LampListener listener) {		lampListeners.remove(listener);	}		/**	 * @param red	 * @param green	 * @param blue	 */	public void onLampStateInqiryBackChange(boolean colorState,boolean OnorOff) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampStateInqiryBackChange(colorState,OnorOff);		}	}		/**	 * @param red	 * @param green	 * @param blue	 */	public void onLampStateFeedBackChange(boolean colorState,boolean OnorOff) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampStateFeedBackChange(colorState,OnorOff);		}	}	/**	 * 通知监听器，灯效	 * @param rhythm	 */	public void notifyLampRhythm(int rhythm ,int red ,int green ,int blue) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampRhythmChange(rhythm,red,green,blue);		}	}		/**	 * @param red	 * @param green	 * @param blue	 */	public void notifyLampColor(int red,int green,int blue) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampColor(red,green,blue);		}	}		/**	 * @param brightness	 */	public void notifyLampBrightness(int brightness) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampBrightness(brightness);		}	}		/**	 * 释放	 */	public void destory()	{		lampListeners.clear();	}//	private LampListener lamp;////	public void setLampListener(LampListener lamp)//	{//		this.lamp = lamp;//	}	public interface LampListener {		void onLampStateInqiryBackChange(boolean colorState, boolean OnorOff);		void onLampStateFeedBackChange(boolean colorState, boolean OnorOff);		void onLampRhythmChange(int rhythm ,int red, int green, int blue);		void onLampColor(int red, int green, int blue);				void onLampBrightness(int brightness);	}}