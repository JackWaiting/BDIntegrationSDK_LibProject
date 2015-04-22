package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.List;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.activity.BaseActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.MySubject;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.Observer;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.MyLogger;

public abstract class BaseFragment extends Fragment implements Observer {
	MyLogger flog = MyLogger.fLog();
	
	protected View root;
	private BaseActivity attachAct;
	protected MySubject mSubject;//被观察者
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBase();
		
		mSubject=MySubject.getSubject();
		mSubject.attach(BaseFragment.this);//加入观察者
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(!(activity instanceof BaseActivity)){
			throw new RuntimeException("can not initialize Basefragment without BaseActivity!");
		}
		attachAct = (BaseActivity) activity;
	}
	
	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {
		root = inflater.inflate(getLayoutId(), container, false);
		initView();
		initData();
		return root;
	}
	/**
	 * 初始化一些与UI无关的资源
	 */
	protected abstract void initBase();
	
	/**
	 * 布局资源id
	 * @return
	 */
	protected abstract int getLayoutId();
	
	protected abstract void initView();
	/**
	 * 在initView之后调用，初始化数据
	 */
	protected abstract void initData();
	
	protected View findViewById(int id) {
		return root.findViewById(id);
	}
	
	protected void showToast(int resId){
		attachAct.showToast(resId);
	}
	protected void showToast(String content){
		attachAct.showToast(content);
	}
	
	protected void cancelToast() {
		attachAct.cancelToast();
	}
	
	protected void startActivity(Class<? extends Activity> clas) {
		attachAct.startActivity(clas);
	}
	
	protected void startMusicPlayerActivity(List<Music> list, int currentPosition, PlayType type) {
		attachAct.startMusicPlayerActivity(list, currentPosition, type);
	}
	/**
	 * 检查网络连接
	 * @param toast
	 */
	protected boolean checkNetwork(boolean toast){
		return attachAct.checkNetwork(toast);
	}
	
	protected void hideInputMethod(){
		attachAct.hideInputMethod(null);
	}
	
	@Override
	public void updateVolume()
	{
		// TODO 固件音量
		
	}

	@Override
	public void updateConnectState(boolean isConnect)
	{
		// TODO 是否连接
		
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		mSubject.deleteach(this);
	}
}
