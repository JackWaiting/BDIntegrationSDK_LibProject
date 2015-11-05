package com.chipsguide.app.colorbluetoothlamp.v2.brunton.listeners;

/**
 * @author msparking
 *观察者
 */
public interface Observer {

	/**
	 * 更新蓝牙连接状态
	 */
	public void updateConnectState();
	
	/**
	 * 当闹钟时间到了，（如果处于alarm模式且不在闹钟界面，需要更新）
	 */
	public void updateAlarm(int state);
}
