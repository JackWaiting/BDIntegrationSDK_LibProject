package com.chipsguide.app.colorbluetoothlamp.v2.brunton.listeners;

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
	public void noticeConnectState() {
		// TODO Auto-generated method stub
		for(Activity mTempActivity:mActivityObservers){
			((Observer)mTempActivity).updateConnectState();
		}
	}

	public void noticeAlarm(int state)
	{
		for(Activity mTempActivity:mActivityObservers)
		{
			((Observer)mTempActivity).updateAlarm(state);
		}
	}

}
