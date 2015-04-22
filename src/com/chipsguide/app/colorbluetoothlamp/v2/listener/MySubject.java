package com.chipsguide.app.colorbluetoothlamp.v2.listener;

import java.util.ArrayList;
import java.util.List;

import com.chipsguide.app.colorbluetoothlamp.v2.utils.MyLogger;

/**
 * @author fiskz被观察者
 */
public class MySubject implements Subject {
	MyLogger flog = MyLogger.fLog();

	private List<Object> mActivityObservers;// 观察者集合
	private boolean isConnectState;//蓝牙是否连接
	private int mVolume = 0;//设备音量大小

	private static MySubject mSubject;

	public static MySubject getSubject()
	{
		if (mSubject == null)
		{
			mSubject = new MySubject();
		}
		return mSubject;
	}

	public MySubject()
	{
		super();
		isConnectState = false;
		mActivityObservers = new ArrayList<Object>();
	}

	public void setConnectState(boolean connectState)
	{
		this.isConnectState = connectState;
		noticeConnectState(isConnectState);
	}

	public boolean getConnectState()
	{
		return isConnectState;
	}

	public int getVolume()
	{
		return mVolume;
	}

	public void setVolume(int volume, boolean mute)
	{
		this.mVolume = volume;
		noticeVolume();
	}

	@Override
	public void attach(Observer observer)
	{
		mActivityObservers.add(observer);
		flog.d("观察者的大小： " + mActivityObservers.size());
	}

	@Override
	public void deleteach(Observer observer)
	{
		// TODO 删除对应的观察者
		mActivityObservers.remove(observer);
	}

	@Override
	public void noticeVolume()
	{
		// TODO 更新音量
		for (int i = 0; i < mActivityObservers.size(); i++)
		{
			((Observer) mActivityObservers.get(i)).updateVolume();
		}
	}

	@Override
	public void noticeConnectState(boolean isConnect)
	{
		// TODO 更新连接状态
		for (int i = 0; i < mActivityObservers.size(); i++)
		{
			((Observer) mActivityObservers.get(i)).updateConnectState(isConnectState);
		}
	}

}
