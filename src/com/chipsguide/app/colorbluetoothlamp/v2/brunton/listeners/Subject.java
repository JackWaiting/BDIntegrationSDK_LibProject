package com.chipsguide.app.colorbluetoothlamp.v2.brunton.listeners;


/**
 * @author msparking
 *被观察者接口
 */
public interface Subject {
	public void attach(Observer observer);//添加观察者
	public void deleteach(Observer observer);//删除观察者
	public void noticeConnectState();//通知连接状态的变化
}
