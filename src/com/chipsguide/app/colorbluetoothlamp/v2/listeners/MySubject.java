package com.chipsguide.app.colorbluetoothlamp.v2.listeners;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;


/**
 * @author msparking
 *被观察者
 */
public class MySubject implements Subject{

	private List<Activity> mActivityObservers;//观察者集合
	private String mConnectStateString;
	private int mBattery=5;
	private int mVolume=0;
	private boolean mMute;
	
	private static MySubject mSubject;
	public static MySubject getSubject()
	{
		if(mSubject == null)
		{
			mSubject = new MySubject();
		}
		return mSubject;
	}
	public MySubject() {
		super();
		mConnectStateString="";
		mActivityObservers=new ArrayList<Activity>();
		// TODO Auto-generated constructor stub
		
	}
	
	
	public void setConnectState(String connectState){
	      this.mConnectStateString = connectState;
	      noticeConnectState();
	}
	public String getConnectState(){
	      return mConnectStateString;
	}
	
	public int getBattery() {
		return mBattery;
	}
	public void setBattery(int battery) {
		this.mBattery = battery;
		noticeBattery();
	}
	
	public int getVolume() {
		return mVolume;
	}
	public boolean getMute(){
		return mMute;
	}
	public void setVolume(int volume,boolean mute) {
		this.mVolume = volume;
		this.mMute=mute;
		noticeVolume();
	}
	
	@Override
	public void attach(Observer observer) {
		// TODO Auto-generated method stub
		mActivityObservers.add((Activity) observer);
	}

	@Override
	public void deleteach(Observer observer) {
		// TODO Auto-generated method stub
		mActivityObservers.remove(observer);
	}
	@Override
	public void noticeVolume() {
		// TODO Auto-generated method stub
		for(int i=0;i<mActivityObservers.size();i++)
		       ((Observer)mActivityObservers.get(i)).updateVolume();
	}
	@Override
	public void noticeBattery() {
		// TODO Auto-generated method stub
		for(int i=0;i<mActivityObservers.size();i++)
		       ((Observer)mActivityObservers.get(i)).updateBattery();
	}
	@Override
	public void noticeConnectState() {
		// TODO Auto-generated method stub
		for(Activity mTempActivity:mActivityObservers){
			((Observer)mTempActivity).updateConnectState();
		}
	}

	public void noticeAlarm(String activity){
		for(Activity mTempActivity:mActivityObservers){
			System.out.println(mTempActivity.getLocalClassName().toString());
			if(mTempActivity.getLocalClassName().toString().equals(activity)){
				((Observer)mTempActivity).updateAlarming();
		    	return;
		    }
		}
	
	}


}
