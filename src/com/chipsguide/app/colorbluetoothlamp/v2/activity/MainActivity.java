package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.app.Activity;
import android.bluetooth.BluetoothAdapter;
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
import com.chipsguide.app.colorbluetoothlamp.v2.frags.MainFragment;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.MainFragment.OnMainPageChangeListener;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NavFrag;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NavFrag.OnNavItemClickListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.service.AlarmAlertService;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TextSwitcherTitleView;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
import com.chipsguide.lib.timer.Alarms;
import com.jeremyfeinstein.slidingmenu.lib.SlidingMenu;
import com.platomix.lib.update.bean.VersionEntity;
import com.platomix.lib.update.core.UpdateAgent;
import com.platomix.lib.update.listener.OnCheckUpdateListener;

public class MainActivity extends BaseActivity implements
		OnNavItemClickListener, OnMainPageChangeListener,
		DialogInterface.OnClickListener {
	private FragmentManager fragManager;
	private NavFrag navFrag;
	private Intent alarmAlertService;
	private Alarms alarms;
	private BluetoothDeviceManager mBluetoothDeviceManager;

	private TextSwitcherTitleView titleView;
	private PlayerManager playerManager;
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
	}

	@Override
	public void initUI() {
		titleView = (TextSwitcherTitleView) findViewById(R.id.titleView);
		titleView.setOnClickListener(this);
		titleView.setTitleText(R.string.color_lamp);

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
		mSubject.destory();
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
			super.onBackPressed();
		}
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
		if (this.mBluetoothDeviceManager != null)
		{
			this.mBluetoothDeviceManager.setForeground(true);
		}
		refreshBluetooth();
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
	public void updateVolume()
	{
		// TODO 固件音量
		
	}

	@Override
	public void updateConnectState(boolean isConnect)
	{
		// TODO 蓝牙连接状态
		if(!isConnect && playerManager.isPlaying()){
			playerManager.pause();
		}
	}
	
}
