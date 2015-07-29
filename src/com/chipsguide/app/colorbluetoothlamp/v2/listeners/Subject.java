package com.chipsguide.app.colorbluetoothlamp.v2.listeners;


/**
 * @author msparking
 *被观察者接口
 */
public interface Subject {
	public void attach(Observer observer);//添加观察者
	public void deleteach(Observer observer);//删除观察者
	public void noticeVolume();//通知音量变化
	public void noticeBattery();//通知电量变化
	public void noticeConnectState();//通知连接状态的变化
}
