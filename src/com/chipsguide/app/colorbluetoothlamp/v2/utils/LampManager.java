package com.chipsguide.app.colorbluetoothlamp.v2.utils;import java.util.ArrayList;import java.util.List;import java.util.Random;import android.R.integer;import android.bluetooth.BluetoothDevice;import android.content.Context;import android.content.Intent;import android.graphics.Color;import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager.OnBluetoothDeviceColorLampStatusChangedListener;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager.OnBluetoothDeviceColdAndWarmWhiteChangedListener;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager.OnBluetoothDeviceCommonLampGlobalUIChangedListener;import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceCommonLampManager.OnBluetoothDeviceCommonLampStatusChangedListener;import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceManagerReadyListener;import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;import com.chipsguide.lib.timer.util.MyLog;public class LampManager implements OnBluetoothDeviceManagerReadyListener,OnBluetoothDeviceCommonLampStatusChangedListener,OnBluetoothDeviceColorLampStatusChangedListener,OnBluetoothDeviceCommonLampGlobalUIChangedListener,OnBluetoothDeviceColdAndWarmWhiteChangedListener{	private String TAG="LampManager";	MyLogger flog = MyLogger.fLog();	private static LampManager mLampManager;	private Context mContext;	private BluetoothDeviceManager mBluetoothDeviceManager;	private BluetoothDeviceCommonLampManager mBluetoothDeviceCommonLampManager;//普通灯管理者	private BluetoothDeviceColorLampManager mBluetoothDeviceColorLampManager;//彩灯的管理者	private boolean colorLamp = false;// 彩灯	private boolean isLampState = false;// 灯的开关状态	public static int THYHM = 0;	//音乐律动模式下，控制颜色转化为普通模式	private boolean flag = true;//	/**	 * 彩灯状态监听集合	 */	private List<LampManager.LampListener> lampListeners = new ArrayList<LampManager.LampListener>();	private LampManager(Context context)	{		mContext = context;	}	//单列	public static LampManager getInstance(Context context)	{		if (mLampManager == null)		{			mLampManager = new LampManager(context);		}		return mLampManager;	}	public void init()	{		mBluetoothDeviceManager = ((CustomApplication) mContext				.getApplicationContext()).getBluetoothDeviceManager();		mBluetoothDeviceManager.setOnBluetoothDeviceManagerReadyListener(this);	}	public BluetoothDevice getBluetoothDevice()	{		if (mBluetoothDeviceManager != null)		{//连接蓝牙			return mBluetoothDeviceManager.getBluetoothDeviceConnectedA2dp();		}		return null;	}	@Override	public void onBluetoothDeviceManagerReady()	{		flog.d("onBluetoothDeviceManagerReady");		mBluetoothDeviceCommonLampManager = mBluetoothDeviceManager				.getBluetoothDeviceCommonLampManager();//普通灯的管理器		mBluetoothDeviceColorLampManager = mBluetoothDeviceManager				.getBluetoothDeviceColorLampManager();//彩灯的管理器		if(mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager.setCalibration(false);						mBluetoothDeviceColorLampManager			.setOnBluetoothDeviceColorLampStatusChangedListener(this);//彩灯状态改变			getStatus();//获得彩灯的状态		}				if (mBluetoothDeviceCommonLampManager != null)		{			mBluetoothDeviceCommonLampManager			.setOnBluetoothDeviceCommonLampStatusChangedListener(this);//普通灯状态的改变			mBluetoothDeviceCommonLampManager.			setOnBluetoothDeviceColdAndWarmWhiteChangedListener(this);//暖白的状态改变			mBluetoothDeviceCommonLampManager.setOnBluetoothDeviceCommonLampGlobalUIChangedListener(this);			mBluetoothDeviceCommonLampManager.getSupportColdAndWarmWhite();			getColdAndWarmWhite();//获取暖白的值		}		}	/**	 * commandType是反馈还是查询 on彩灯开关colorlamp【on-off】 brightness彩灯亮度（彩灯亮度不变化）	 * red-green-blue【RGB值】 rhythm灯效	 */	@Override	public void onBluetoothDeviceColorLampStatusChanged(int commandType,			boolean on, int brightness, int red, int green, int blue, int rhythm)	{		flog.d("彩灯 commandType " + commandType + " on : " + on);		flog.d("rhythm:" + rhythm);		isLampState = on;		colorLamp = true;		THYHM = rhythm;		commandTypeBack(commandType);//命令的类型 		notifyLampRhythm(rhythm);//通知监听器，灯效		notifyLampColor(red, green, blue);//通知灯的颜色	}	/**	 * commandType反馈or查询 on白灯开关 brightness亮度值	 */	@Override	public void onBluetoothDeviceCommonLampStatusChanged(int commandType,			boolean on, int brightness)	{		flog.d("白灯 commandType " + commandType + " on : " + on);		//根据彩灯来判断		if(commandType == BluetoothDeviceColorLampManager.CommandType.INQUIRY && !isLampState)		{			colorLamp = false;		}		if(commandType == BluetoothDeviceColorLampManager.CommandType.FEEDBACK)		{			colorLamp = false;		}		isLampState = on;		commandTypeBack(commandType);//命令的类型		notifyLampBrightness(brightness);//复亮度值		THYHM = 0;	}//命令的类型  是查询还是反馈  来得到  颜色 还是开关状态  	private void commandTypeBack(int commandType)	{		switch (commandType)		{		case BluetoothDeviceColorLampManager.CommandType.INQUIRY:			if (isLampState)			{				onLampStateInqiryBackChange(colorLamp, isLampState);			}			break;		case BluetoothDeviceColorLampManager.CommandType.FEEDBACK:			onLampStateFeedBackChange(colorLamp, isLampState);			break;		}	}	/**	 * @param color	 */	public void setColor(int color)	{		setColor(color,flag);	}	/**	 * @param color	 * @param flag true 音乐律动模式下，控制颜色转化为普通模式	 */	public void setColor(int color,boolean flag)	{		isRhythm(flag);		setColor(Color.red(color), Color.green(color), Color.blue(color),flag);	}	/**	 * @param flag true 音乐律动，彩虹，呼吸模式下	 * 控制颜色转化为普通模式	 */	private void isRhythm(boolean flag)	{		if (THYHM == BluetoothDeviceColorLampManager.Effect.RAINBOW && flag)		{			setLampEffect(BluetoothDeviceColorLampManager.Effect.NORMAL);		}		if (THYHM == BluetoothDeviceColorLampManager.Effect.PULSE && flag)		{			setLampEffect(BluetoothDeviceColorLampManager.Effect.NORMAL);		}		if (THYHM == BluetoothDeviceColorLampManager.Effect.RHYTHM && flag)		{			setLampEffect(BluetoothDeviceColorLampManager.Effect.NORMAL);		}	}	/**	 * @param red	 * @param green	 * @param blue	 */	public void setColor(int red, int green, int blue)	{		setColor(red, green, blue, flag);	}	/**	 * @param red	 * @param green	 * @param blue	 */	public void setColor(int red, int green, int blue,boolean flag)	{		isRhythm(flag);		if (mBluetoothDeviceColorLampManager != null)		{			if (red == 255 && green == 255 && blue == 255)			{				turnCommonOn();				return;			}			mBluetoothDeviceColorLampManager.setColor(red, green, blue);		}	}	/**	 * @param brightness亮度	 */	public void setBrightness(int brightness)	{		flog.d("setBrightness bai" + brightness);		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceCommonLampManager.setBrightness(brightness);		}	}	/**	 * @param effect灯效	 */	public void setLampEffect(int effect)	{		setLampEffect(effect, 0);	}	/**	 * @param effect灯效	 * @param velocity灯效速度	 *            [2-49]	 */	public void setLampEffect(int effect, int velocity)	{		if (mBluetoothDeviceColorLampManager != null)		{			if (velocity == 0)			{				mBluetoothDeviceColorLampManager.setEffect(effect);			}else			{				mBluetoothDeviceColorLampManager				.setLampEffect(effect, velocity);			}		}	}//普通灯颜色的开关	public void turnCommonOn()	{		if (mBluetoothDeviceCommonLampManager != null)		{			mBluetoothDeviceCommonLampManager.turnOn();		}	}	public void turnCommonOff()	{		if (mBluetoothDeviceCommonLampManager != null)		{			mBluetoothDeviceCommonLampManager.turnOff();		}	}//彩灯颜色的开关	public void turnColorOn()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager.turnOn();		}	}	public void turnColorOff()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager.turnOff();		}	}	/**	 * @param flag true 音乐律动模式下，控制颜色转化为普通模式	 */	public void randomColor(boolean flag)	{		Random r = new Random();		int red = r.nextInt(255) + 1;// 范围是[0+1,255)		int green = r.nextInt(255) + 1;// 范围是[0+1,255)		int blue = r.nextInt(255) + 1;// 范围是[0+1,255)		setColor(red, green, blue);	}	public boolean isColorLamp()	{		return colorLamp;	}	public void lampOn()	{		if (colorLamp)		{			turnColorOn();		} else		{			turnCommonOn();		}	}	public void lampOff()	{		if (colorLamp)		{			turnColorOff();		} else		{			turnCommonOff();		}	}	/**	 * 开关灯	 */	public void LampOnorOff()	{		if (isLampState)		{			lampOff();		} else		{			lampOn();		}	}	//获取彩灯状态	public void getStatus()	{		if (mBluetoothDeviceColorLampManager != null)		{			mBluetoothDeviceColorLampManager			.getStatus(BluetoothDeviceColorLampManager.StatusType.STATUS);			mBluetoothDeviceCommonLampManager.getLampstatus();		}	}	@Override	public void onBluetoothDeviceColorLampStatusChanged(int arg0, boolean arg1,			int arg2, int arg3, int arg4, int arg5, int arg6, int arg7,			int arg8, int arg9, int arg10)	{		// TODO Auto-generated method stub		flog.d("--------------");	}	/**	 * 添加灯效监听状态	 * 如果添加的类为Activity、Fragment或其中的非静态内部类，<br>	 * 那么在Activity、Fragment销毁时，一定要调用{@link BluetoothDeviceManagerProxy#removeOnBluetoothDeviceConnectionStateChangedListener(OnBluetoothDeviceConnectionStateChangedListener)}}	 * 将其移除，否则会引起内存泄露	 * @param listener	 */	public void addOnBluetoothDeviceLampListener(			LampListener listener) {		lampListeners.add(listener);	}	//销毁时掉用	public void removeOnBluetoothDeviceLampListener(			LampListener listener) {		lampListeners.remove(listener);	}//获取冷白色值	public void getColdAndWarmWhite()	{		 mBluetoothDeviceCommonLampManager.getColdAndWarmWhite();				MyLog.i(TAG,"初始获取冷白值------");	}		//设置冷白设置	public void setColdAndWarmWhite(int micSeekBarNum)	{		if(mBluetoothDeviceCommonLampManager != null)		{System.out.println("设置冷白设置+++"+micSeekBarNum);			mBluetoothDeviceCommonLampManager.setColdAndWarmWhite(micSeekBarNum);		}	}	@Override//冷暖白灯状态改变	public void onBluetoothDeviceColdAndWarmWhiteChanged(int CommandType, int SeekBarNum)	{		// TODO 暖灯			notifyLampSeekBarNum(SeekBarNum);//暖白值		System.out.println( "暖白值--CommandType--="+CommandType+"---SeekBarNum--="+SeekBarNum);		}	/**	 * @param red	 * @param green	 * @param blue	 */	public void onLampStateInqiryBackChange(boolean colorState,boolean OnorOff) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampStateInqiryBackChange(colorState,OnorOff);		}	}	/**	 * @param red	 * @param green	 * @param blue	 */	public void onLampStateFeedBackChange(boolean colorState,boolean OnorOff) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampStateFeedBackChange(colorState,OnorOff);		}	}	/**	 * 通知监听器，灯效变化	 * @param rhythm	 */	public void notifyLampRhythm(int rhythm) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampRhythmChange(rhythm);		}	}	/**	 * @param red	 * @param green	 * @param blue	 */	public void notifyLampColor(int red,int green,int blue) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampColor(red,green,blue);		}	}	/**	 * @param brightness	 */	public void notifyLampBrightness(int brightness) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).onLampBrightness(brightness);		}	}	/**	 * @param 冷暖灯值	 */	public void notifyLampSeekBarNum(int OnLampSeekBarNum) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).OnLampSeekBarNum(OnLampSeekBarNum);		}	}		//是否是白灯	public void notifyLampSupportColdAndWhite(boolean filament) {		int size = lampListeners.size();		for (int i = 0; i < size; i++) {			lampListeners.get(i).LampSupportColdAndWhite(filament);		}	}		/**	 * 释放	 */	public void destory()	{		lampListeners.clear();	}	//	private LampListener lamp;	//	//	public void setLampListener(LampListener lamp)	//	{	//		this.lamp = lamp;	//	}	//接口	public interface LampListener {		void onLampStateInqiryBackChange(boolean colorState, boolean OnorOff);//查询灯的颜色，开关状态		void onLampStateFeedBackChange(boolean colorState, boolean OnorOff);//得到灯的颜色，和开关状态的反馈		void onLampRhythmChange(int rhythm);//灯效变化		void onLampColor(int red, int green, int blue);//灯的颜色		void onLampBrightness(int brightness);//灯的亮度		void OnLampSeekBarNum(int SeekBarNum);				void LampSupportColdAndWhite(boolean filament);//判断是否是白灯	}	@Override	public void onBluetoothDeviceCommonLampSupportColdAndWhite(boolean filament) {		notifyLampSupportColdAndWhite(filament);		MyLog.i(TAG,"判断是否白灯filament-----+="+filament);	}}