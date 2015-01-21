package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.app.Activity;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.activity.BaseActivity;

public abstract class BaseFragment extends Fragment {
	protected View root;
	private BaseActivity attachAct;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		initBase();
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
}
