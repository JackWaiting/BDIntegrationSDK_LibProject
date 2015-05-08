package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.lang.reflect.Type;
import java.util.List;

import android.app.Activity;
import android.app.Dialog;
import android.bluetooth.BluetoothDevice;
import android.content.Context;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.Window;
import android.view.inputmethod.InputMethodManager;
import android.widget.Toast;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.MySubject;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.Observer;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.MyLogger;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.NetworkState;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ConnectDialog;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
import com.google.gson.Gson;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.jeremyfeinstein.slidingmenu.lib.app.SlidingFragmentActivity;
import com.umeng.analytics.MobclickAgent;

public abstract class BaseActivity extends SlidingFragmentActivity implements OnBluetoothDeviceConnectionStateChangedListener,
					OnClickListener,Observer{
	private Toast mToast;
	MyLogger flog = MyLogger.fLog();
	protected ConnectDialog mConnectpd = null;
	protected MySubject mSubject;//被观察者
	private BluetoothDeviceManagerProxy mManagerProxy;
	private ConnectStateListener mConnectStateListener;
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		requestWindowFeature(Window.FEATURE_NO_TITLE);
		setContentView(getLayoutId());
		setBehindContentView(R.layout.menu_frame);
		getSlidingMenu().setTouchModeAbove(SlidingMenu.TOUCHMODE_NONE);
		initBase();
		initUI();
		initData();
		initListener();
		
		mSubject=MySubject.getSubject();
		mSubject.attach(BaseActivity.this);//加入观察者
		mManagerProxy = BluetoothDeviceManagerProxy.getInstance(this);
		mManagerProxy.addOnBluetoothDeviceConnectionStateChangedListener(this);
	}
	
	// 设置布局文件
	public abstract int getLayoutId();

	// 初始换数据
	public abstract void initBase();

	// 初始化控件
	public abstract void initUI();

	// 控件填充数据
	public abstract void initData();

	// 绑定监听
	public abstract void initListener();

	Dialog dialog;

	protected void showProgressDialog(String msg) {
	}

	protected void dismissProgressDialog() {
	}
	/**
	 * 吐司，可以在子线程中调用
	 * @param resId
	 */
	public void showToast(final int resId) {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				makeToast(resId);
			}
		});
	}
	/**
	 * 吐司，可以在子线程中调用
	 * @param content
	 */
	public void showToast(final String content){
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				makeToast(content);
			}
		});
	}
	/**
	 * 取消吐司
	 */
	public void cancelToast() {
		runOnUiThread(new Runnable() {
			@Override
			public void run() {
				if(mToast != null){
					mToast.cancel();
				}
			}
		});
	}
	
	public void startActivity(Class<?extends Activity> c){
		Intent intent = new Intent(this,c);
		startActivity(intent);
	}
	
	private void makeToast(String text){
		mToast = Toast.makeText(BaseActivity.this, text, Toast.LENGTH_SHORT);
		mToast.show();
	}
	private void makeToast(int resId){
		mToast = Toast.makeText(BaseActivity.this, resId, Toast.LENGTH_SHORT);
		mToast.show();
	}
	
	/**
	 * 检查网络连接
	 * @param showToast 如果没有网络，是否Toast提示
	 * @return
	 */
	public boolean checkNetwork(boolean showToast) {
		if (NetworkState.isAvailable(this)){
			return true;
		}else if(showToast){
			showToast(R.string.no_network);
		}
		return false;
	}
	
	/**
	 * 隐藏输入法
	 */
	public void hideInputMethod(View view) {
		InputMethodManager m = (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
		if(getCurrentFocus() != null && m.isActive()){
			if(view == null){
				view = getCurrentFocus();
			}
			m.hideSoftInputFromWindow(view.getWindowToken(), InputMethodManager.HIDE_NOT_ALWAYS);
		}
	}
	
	protected <T> T parse(String json, Type cls) {
		try {
			Gson gson = new Gson();
			return gson.fromJson(json, cls);
		} catch (Exception e) {
		}
		return null;
	}
	
	/**
	 * 打开播放界面，覆盖上次的播放列表
	 * @param list
	 * @param currentPosition
	 * @param type
	 */
	public void startMusicPlayerActivity(List<Music> list, int currentPosition, PlayType type){
		PlayerManager.getInstance(getApplicationContext()).setMusicList(list, currentPosition, type);
		Intent intent = new Intent(this, MusicPlayerActivity.class);
		startActivity(intent);
	}
	
	public void startMusicPlayerActivity(){
		startActivity(MusicPlayerActivity.class);
	}
	
	@Override
	public void onClick(View v) {
		switch(v.getId()){
		case R.id.left_btn:
			finish();
			break;
		case R.id.right_btn:
			startMusicPlayerActivity();
			break;
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		MobclickAgent.onResume(this);
	}
	
	@Override
	protected void onPause() {
		super.onPause();
		MobclickAgent.onPause(this);
	}
	
	/**
	 * 加载动画
	 */
	public void createConnPD()
	{
		if (mConnectpd == null)
		{
			if (this.getParent() != null)
			{
				mConnectpd = new ConnectDialog(this.getParent(),
						R.style.Dialog_Fullscreen);
			} else
			{
				mConnectpd = new ConnectDialog(this, R.style.Dialog_Fullscreen);
			}
			showConnectPD();
		} else
		{
			showConnectPD();
		}
	}

	public void setText(int resId)
	{
		if (mConnectpd != null)
		{
			mConnectpd.setMessage(resId);
		}

	}

	public void showConnectPD()
	{
		if (mConnectpd != null && !mConnectpd.isShowing())
		{
			mConnectpd.show();
		}

	}

	public void dismissConnectPD()
	{
		if (mConnectpd != null && mConnectpd.isShowing())
		{
			mConnectpd.dismiss();
			mConnectpd = null;
		}
	}
	
	@Override
	public void onBluetoothDeviceConnectionStateChanged(
			BluetoothDevice bluetoothDevice, int state)
	{
		if(mConnectStateListener != null)
		{
			mConnectStateListener.onBluetoothDeviceConnectionState(bluetoothDevice, state);
		}
		switch (state)
		{
		// a2dp连接中
		case BluetoothDeviceManager.ConnectionState.A2DP_CONNECTING:
			setText(R.string.audio_connectioning);
			flog.d("A2DP_CONNECTING  a2dp连接中");
			break;

		// a2dp连接失败
		case BluetoothDeviceManager.ConnectionState.A2DP_FAILURE:
			flog.d("A2DP_FAILURE  a2dp连接失败");
			dismissConnectPD();
			break;

		// a2dp配对
		case BluetoothDeviceManager.ConnectionState.A2DP_PAIRING:
			flog.d("A2DP_PAIRING  a2dp配对中");
			break;

		// a2dp连接
		case BluetoothDeviceManager.ConnectionState.A2DP_CONNECTED:
			flog.d("A2DP_CONNECTED  a2dp连接成功");
			setText(R.string.audio_connectionend);
			break;

		// a2dp断开
		case BluetoothDeviceManager.ConnectionState.A2DP_DISCONNECTED:
			flog.d("A2DP_DISCONNECTED  a2dp断开");
			break;

		// spp连接中
		case BluetoothDeviceManager.ConnectionState.SPP_CONNECTING:
			flog.d("SPP_CONNECTING  spp连接中");
			setText(R.string.data_connectioning);
			break;

		// /spp连接成功
		case BluetoothDeviceManager.ConnectionState.SPP_CONNECTED:
			flog.d("SPP_CONNECTED spp连接成功");
			setText(R.string.data_connectionend);
			break;

		// spp断开
		case BluetoothDeviceManager.ConnectionState.SPP_DISCONNECTED:
			flog.d("SPP_DISCONNECTED  spp断开");
			break;

		// spp连接失败
		case BluetoothDeviceManager.ConnectionState.SPP_FAILURE:
			flog.d("SPP_FAILURE  spp连接失败");
			dismissConnectPD();
			break;

		// 连接
		case BluetoothDeviceManager.ConnectionState.CONNECTED:
			flog.d("CONNECTED  连接成功");
			setText(R.string.connectionend);
			dismissConnectPD();
			mSubject.setConnectState(true);
			break;
		// 断开
		case BluetoothDeviceManager.ConnectionState.DISCONNECTED:
			flog.d("DISCONNECTED  断开连接");
			dismissConnectPD();
			mSubject.setConnectState(false);
			break;
		}
	}
	
	public interface ConnectStateListener{
		void onBluetoothDeviceConnectionState(BluetoothDevice bluetoothDevice, int state);
	}
	
	public void setmConnectStateListener(ConnectStateListener connectStateListener)
	{
		mConnectStateListener = connectStateListener;
	}
	
	@Override
	protected void onDestroy() {
		super.onDestroy();
		mSubject.deleteach(this);
		mManagerProxy.removeOnBluetoothDeviceConnectionStateChangedListener(this);
	}
}
