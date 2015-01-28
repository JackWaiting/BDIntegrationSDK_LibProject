package com.chipsguide.app.colorbluetoothlamp.v2.utils;

import android.app.Service;
import android.content.Context;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.os.Vibrator;

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
	
	public static ShakeManager getInstance(Context context){
		if(instance == null){
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

	/**
	 * 开始接收晃动
	 */
	public void start() {
		// 注册
		if (sensor != null) {
			//会造成UI卡顿，所以启动一个线程
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
		if (event.sensor.getType() != Sensor.TYPE_ACCELEROMETER || onShakeListener == null) {
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
				vibrator.vibrate(new long[] { 100, 100}, -1);
			}
			onShakeListener.onShake();
		}
	}

	public void onAccuracyChanged(Sensor sensor, int accuracy) {
	}

	// 摇晃监听接口
	public interface OnShakeListener {
		public void onShake();
	}
}