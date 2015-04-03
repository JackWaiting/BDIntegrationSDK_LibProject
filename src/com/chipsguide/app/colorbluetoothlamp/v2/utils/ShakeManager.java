package com.chipsguide.app.colorbluetoothlamp.v2.utils;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.media.AudioManager;
import android.media.SoundPool;
import android.os.Vibrator;

/**
 * 摇一摇工具类
 * 
 * @author chiemy
 * 
 */
public class ShakeManager implements SensorEventListener {
	// 两次检测的时间间隔
	private static final int UPTATE_INTERVAL_TIME = 2000;
	private static ShakeManager instance;
	// 传感器管理器
	private SensorManager sensorManager;
	// 传感器
	private Sensor sensor;
	// 感应监听器
	private OnShakeListener onShakeListener;
	private Context mContext;
	// 上次检测时间
	private long lastUpdateTime;
	private Vibrator vibrator;
	private static final long[] VIBRATE_PATTERN = { 100, 100, 100, 100 }; // 震动时间，停止时间，震动时间，……
	private SoundPool soundPool;
	private int soundID;

	// 构造器
	private ShakeManager(Context c) {
		// 获得监听对象
		mContext = c;
		vibrator = (Vibrator) mContext
				.getSystemService(Service.VIBRATOR_SERVICE);
		// 获得传感器管理器
		sensorManager = (SensorManager) mContext
				.getSystemService(Context.SENSOR_SERVICE);
		if (sensorManager != null) {
			// 获得重力传感器
			sensor = sensorManager.getDefaultSensor(Sensor.TYPE_ACCELEROMETER);
		}
	}

	public static ShakeManager getInstance(Context context) {
		if (instance == null) {
			instance = new ShakeManager(context);
		}
		return instance;
	}

	private boolean vibrate = true;

	/**
	 * 是否震动反馈
	 * 
	 * @param vibrator
	 */
	public void setVibrateFeedback(boolean vibrate) {
		this.vibrate = vibrate;
	}

	/*
	 * 
	 */
	public void setSoundRes(int resId) {
		// 参数：1.同时播放的最大数量 2.类型 3.质量（暂时无效）
		soundPool = new SoundPool(1, AudioManager.STREAM_MUSIC, 100);
		// 参数:1.context 2.资源文件 3.优先级（暂时无效，为与将来兼容置为1）
		soundID = soundPool.load(mContext, resId, 1);
	}

	/**
	 * 开始接收晃动
	 */
	public void start() {
		// 注册
		if (sensor != null) {
			// 会造成UI卡顿，所以启动一个线程
			new Thread(new Runnable() {
				@Override
				public void run() {
					sensorManager.registerListener(instance, sensor,
							SensorManager.SENSOR_DELAY_GAME);
				}
			}).start();
		}
	}

	/*
	 * 停止接收
	 */
	public void stop() {
		sensorManager.unregisterListener(this);
		if(soundPool != null){
			soundPool.release();
		}
	}

	/**
	 * 设置加速度传感器监听器
	 * 
	 * @param listener
	 */
	public void setOnShakeListener(OnShakeListener listener) {
		onShakeListener = listener;
	}

	/**
	 * 重力感应器感应获得变化数据
	 */
	@Override
	public void onSensorChanged(SensorEvent event) {
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER
				|| onShakeListener == null) {
			return;
		}
		long currentUpdateTime = System.currentTimeMillis();
		// 两次检测的时间间隔
		long timeInterval = currentUpdateTime - lastUpdateTime;
		float[] values = event.values;
		float x = values[0]; // x轴方向的重力加速度，向右为正
		float y = values[1]; // y轴方向的重力加速度，向前为正
		float z = values[2]; // z轴方向的重力加速度，向上为正
		if ((Math.abs(x) > 17 || Math.abs(y) > 17 || Math.abs(z) > 17)
				&& timeInterval > UPTATE_INTERVAL_TIME) {
			lastUpdateTime = currentUpdateTime;
			if (vibrate) {
				vibrator.vibrate(VIBRATE_PATTERN, -1);
			}
			if(soundID > 0){
				// 1.load方法返回值 2、左声道音量（0-1） 3、右声道音量 4、优先级 5、重播次数（0不循环，-1无限循环）
				// 6、播放速度(0.5-2, 1是正常速度)
				soundPool.play(soundID, 1, 1, 1, 0, 1);
			}
			if(onShakeListener != null){
				onShakeListener.onShake();
			}
		}
	}

	@Override
	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	// 摇晃监听接口
	public interface OnShakeListener {
		public void onShake();
	}
}