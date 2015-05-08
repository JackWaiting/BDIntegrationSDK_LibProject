package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
import android.bluetooth.BluetoothDevice;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Canvas;
import android.os.Bundle;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import android.view.View;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy.OnDeviceConnectedStateChangedListener;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.MainFragment;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.MainFragment.OnMainPageChangeListener;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NavFrag;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NavFrag.OnNavItemClickListener;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.ConnectStateListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.service.AlarmAlertService;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.view.ErrorToastDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TextSwitcherTitleView;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
import com.chipsguide.lib.timer.Alarms;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.platomix.lib.update.bean.VersionEntity;
import com.platomix.lib.update.core.UpdateAgent;
import com.platomix.lib.update.listener.OnCheckUpdateListener;

public class MainActivity extends BaseActivity implements
		OnNavItemClickListener, OnMainPageChangeListener,
		DialogInterface.OnClickListener,
		OnBluetoothDeviceConnectionStateChangedListener{
	private FragmentManager fragManager;
	private NavFrag navFrag;
	private Intent alarmAlertService;
	private Alarms alarms;
	private BluetoothDeviceManager mBluetoothDeviceManager;
	private BluetoothDeviceManagerProxy mManagerProxy;
	private BluetoothConnectionActivity mBluetoothConnectionActivity;

	private TextSwitcherTitleView titleView;
	private PlayerManager playerManager;
	private boolean background = false;
	
	@Override
	public int getLayoutId() {
		return R.layout.activity_main;
	}

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		if (savedInstanceState == null) {
			FragmentTransaction transaction = fragManager.beginTransaction();
			navFrag = new NavFrag();
			transaction.replace(R.id.menu_frame, navFrag);
			transaction.commit();
		} else {
			navFrag = (NavFrag) this.getSupportFragmentManager()
					.findFragmentById(R.id.menu_frame);
		}
		navFrag.setOnItemClickListener(this);
	}

	private void initBehindSlidingMenu() {
		SlidingMenu sm = getSlidingMenu();
		sm.setFadeEnabled(false);
		sm.setBehindOffset(getResources().getDisplayMetrics().widthPixels / 7 * 2);
		sm.setBehindScrollScale(0.25f);
		sm.setFadeDegree(0.25f);
		sm.setTouchModeAbove(SlidingMenu.TOUCHMODE_MARGIN);

		sm.setBackgroundImage(R.drawable.bg_sidebar);

		sm.setAboveCanvasTransformer(new SlidingMenu.CanvasTransformer() {
			@Override
			public void transformCanvas(Canvas canvas, float percentOpen) {
				float scale = (float) (1 - percentOpen * 0.05);
				canvas.scale(scale, scale, 0, canvas.getHeight() / 2);
			}
		});
	}

	@Override
	public void initBase() {
		checeNewVersion();
		fragManager = getSupportFragmentManager();
		initBehindSlidingMenu();
		alarms = Alarms.getInstance(getApplicationContext());
		alarms.setAllowInBack(true, AlarmAlertService.class);
		alarms.activieAllEnable();
		mBluetoothDeviceManager = ((CustomApplication)getApplicationContext()).getBluetoothDeviceManager();
		playerManager = PlayerManager.getInstance(getApplicationContext());
		
		mBluetoothConnectionActivity = new BluetoothConnectionActivity();
		mManagerProxy = BluetoothDeviceManagerProxy.getInstance(this);
		mManagerProxy.addOnBluetoothDeviceConnectionStateChangedListener(this);
	}

	@Override
	public void initUI() {
		titleView = (TextSwitcherTitleView) findViewById(R.id.titleView);
		titleView.setOnClickListener(this);
		titleView.setTitleText(R.string.color_lamp);
		titleView.setShowToastTv(true);

		MainFragment mainFrag = new MainFragment();
		fragManager.beginTransaction().replace(R.id.content_layout, mainFrag)
				.commit();
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
		alarmAlertService = new Intent(this, AlarmAlertService.class);
		startService(alarmAlertService);
		mManagerProxy.setDeviceConnectedStateChangedListener(new OnDeviceConnectedStateChangedListener()
		{
			
			@Override
			public void onConnectedChanged(boolean isConnected)
			{
				if(!isConnected && playerManager.isPlaying()){
					playerManager.pause();
				}
			}
		});
	}

	@Override
	public void onItemClick(int position, String title) {
	}

	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.left_btn:
			getSlidingMenu().showMenu(true);
			break;
		case R.id.right_btn:
			// TODO 开启播放界面
			startMusicPlayerActivity();
			break;
		default:
			break;
		}
	}

	@Override
	public void onMainPageChanged(int position, String title) {
		titleView.setTitleText(title);
	}

	@Override
	protected void onDestroy() {
		super.onDestroy();
		BluetoothDeviceManagerProxy.getInstance(this).destory();
		playerManager.destoryAll();
		stopService(alarmAlertService);
		alarms.cancel(true);
//		mSubject.destory();
		LampManager.getInstance(this).destory();
		mManagerProxy.removeOnBluetoothDeviceConnectionStateChangedListener(this);
		releaseManager();
	}
	
	private void releaseManager()
	{
		if (mBluetoothDeviceManager != null)
		{
			mBluetoothDeviceManager.setOnBluetoothDeviceConnectionStateChangedListener(null);
			mBluetoothDeviceManager.setOnBluetoothDeviceGlobalUIChangedListener(null);
			mBluetoothDeviceManager.release();
			mBluetoothDeviceManager = null;
		}
	}

	private boolean forceUpdate;

	private void checeNewVersion() {
		UpdateAgent.setOnCheckUpdateListener(checkUpdateListener);
		UpdateAgent.setDialogButtonClickListener(this);
		UpdateAgent.setNotifycationVisibility(true);
		UpdateAgent.checkUpdate(CustomApplication.APP_SIGN, this);
	}

	private OnCheckUpdateListener checkUpdateListener = new OnCheckUpdateListener() {
		@Override
		public boolean onCheckResult(int status, boolean force,
				VersionEntity entity) {
			if (status == UpdateStatus.YES) {
				forceUpdate = force;
			}
			return false;
		}
	};

	@Override
	public void onClick(DialogInterface dialog, int which) {
		switch (which) {
		case DialogInterface.BUTTON_NEGATIVE:
			if (forceUpdate) {
				finish();
			}
			break;
		}
	}
	
	private long preTime;
	private static final long INTERVAL = 2000;

	@Override
	public void onBackPressed() {
		long currentTime = System.currentTimeMillis();
		if (currentTime - preTime > INTERVAL) {
			showToast(R.string.press_again_to_exist);
			preTime = currentTime;
		}else{
			cancelToast();
			releaseManager();
			super.onBackPressed();
		}
	}
	/**
	 * 显示顶部提示
	 * @param resId
	 */
	public void showTitleToast(int resId){
		titleView.setToastText(resId);
	}
	
	/**
    * 自定义的打开 Bluetooth 的请求码，与 onActivityResult 中返回的 requestCode 匹配。
    */
	private static final int REQUEST_CODE_BLUETOOTH_ON = 1313;
	/**
	 * Bluetooth 设备可见时间，单位：秒。
	 */
	private static final int BLUETOOTH_DISCOVERABLE_DURATION = 250;
	
	@Override
	protected void onResume()
	{
		super.onResume();
		background = false;
		if (this.mBluetoothDeviceManager != null)
		{
			this.mBluetoothDeviceManager.setForeground(true);
		}
		refreshBluetooth();
	}
	
	@Override
	protected void onPause()
	{
		// TODO Auto-generated method stub
		super.onPause();
		background = true;
	}
	
	private void refreshBluetooth()
	{
		if ((mBluetoothDeviceManager.isBluetoothSupported()) && (!mBluetoothDeviceManager.isBluetoothEnabled()))
		{
			turnOnBluetooth();
		}
	}
	
   /**
    * 弹出系统弹框提示用户打开 Bluetooth
    */
    private void turnOnBluetooth()
    {
       // 请求打开 Bluetooth
       Intent requestBluetoothOn = new Intent(BluetoothAdapter.ACTION_REQUEST_ENABLE);
       // 设置 Bluetooth 设备可见时间
       requestBluetoothOn.putExtra( BluetoothAdapter.EXTRA_DISCOVERABLE_DURATION,BLUETOOTH_DISCOVERABLE_DURATION);
       // 请求开启 Bluetooth
       this.startActivityForResult(requestBluetoothOn, REQUEST_CODE_BLUETOOTH_ON);
    }
    
	@Override
	protected void onActivityResult(int requestCode, int resultCode, Intent data)
	{
		// requestCode 与请求开启 Bluetooth 传入的 requestCode 相对应
		if (requestCode == REQUEST_CODE_BLUETOOTH_ON)
		{
			switch (resultCode)
			{
			// 点击确认按钮
			case Activity.RESULT_OK:
			{
				// TODO 用户选择开启 Bluetooth，Bluetooth 会被开启
			}
				break;
			// 点击取消按钮或点击返回键
			case Activity.RESULT_CANCELED:
			{
				// TODO 用户拒绝打开 Bluetooth, Bluetooth 不会被开启
				finish();
			}
				break;
			}
		}
	}
	
	@Override
	public void onBluetoothDeviceConnectionStateChanged(
			BluetoothDevice bluetoothDevice, int state)
	{
		if(mBluetoothConnectionActivity != null)
		{
			((ConnectStateListener)mBluetoothConnectionActivity).onBluetoothDeviceConnectionState(bluetoothDevice, state);
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
			break;
		// 断开
		case BluetoothDeviceManager.ConnectionState.DISCONNECTED:
			flog.d("DISCONNECTED  断开连接");
			dismissConnectPD();
			break;
		case BluetoothDeviceManager.ConnectionState.TIMEOUT:
		case BluetoothDeviceManager.ConnectionState.CAN_NOT_CONNECT_INSIDE_APP:
			flog.d("CAN_NOT_CONNECT_INSIDE_APP 未连接成功");
			dismissConnectPD();
			ErrorToastDialog toastDialog = new ErrorToastDialog(this,
					R.style.full_screen);
			if(!background)
			{
				toastDialog.show();
			}
			// 提示，由于系统原因或者未知原因，应用内无法连接蓝牙，请自行在系统中连接设备，回到应用即可。
			break;
		}
		
	}
}
