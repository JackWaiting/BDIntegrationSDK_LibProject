package com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.bluetooth.BluetoothDeviceManagerProxy.OnModeChangedListener;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.utils.PreferenceUtil;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class ShakeSettingActivity extends BaseActivity implements OnClickListener,OnModeChangedListener{
	private PreferenceUtil preferenceUtil;
	private RadioButton mRandomColor;
	private RadioButton mLightToggle;
	private RadioButton mPlayerToggle;
	private RadioButton mNextSong;
	private View lin01,lin02;
	private BluetoothDeviceManagerProxy bluzDeviceManProxy;
	@Override
	public int getLayoutId() {
		return R.layout.activity_shake_setting;
	}

	@Override
	public void initBase() {
		preferenceUtil = PreferenceUtil.getIntance(getApplicationContext());
		bluzDeviceManProxy = BluetoothDeviceManagerProxy.getInstance(this);
		bluzDeviceManProxy.addOnModeChangedListener(this);
	}

	@Override
	public void initUI() {
		findViewById(R.id.right_btn).setVisibility(View.INVISIBLE);
		RadioGroup shakeOptionsRg = (RadioGroup) findViewById(R.id.rg_shake_options);
		mRandomColor = (RadioButton)findViewById(R.id.rb_random_color);
		mLightToggle = (RadioButton)findViewById(R.id.rb_light_toggle);
		mPlayerToggle = (RadioButton)findViewById(R.id.rb_player_toggle);
		mNextSong = (RadioButton)findViewById(R.id.rb_next_song);
		lin01 = (View)findViewById(R.id.lin_01);
		lin02 = (View)findViewById(R.id.lin_02);
		int id = preferenceUtil.getShakeOption();
		shakeOptionsRg.check(id);
		
		mRandomColor.setOnClickListener(this);
		mLightToggle.setOnClickListener(this);
		mPlayerToggle.setOnClickListener(this);
		mNextSong.setOnClickListener(this);
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		CustomApplication.addActivity(this);
		mode2Linein(bluzDeviceManProxy.getBluetoothManagerMode());
	}

	@Override
	public void onClick(View v)
	{
		super.onClick(v);
		preferenceUtil.saveShakeOption(v.getId());
		finish();
	}
	
	@Override
	public void updateConnectState()
	{
	}

	@Override
	public void updateAlarm(int state)
	{
		if(CustomApplication.getActivity() == this)
		{
			if(state == 1)
			{
				createAlarmToast();
			}else if(state == 0)
			{
				dismissAlarmDialog();
			}else
			{
				dismissAlarmDialog();
			}
		}
	}

	@Override
	public void onModeChanged(int mode)
	{
		mode2Linein(mode);
	}

	private void mode2Linein(int mode)
	{
		if(mode == BluetoothDeviceManager.Mode.LINE_IN)
		{
			mPlayerToggle.setVisibility(View.INVISIBLE);
			mNextSong.setVisibility(View.INVISIBLE);
			lin01.setVisibility(View.INVISIBLE);
			lin02.setVisibility(View.INVISIBLE);
		}else
		{
			mPlayerToggle.setVisibility(View.VISIBLE);
			mNextSong.setVisibility(View.VISIBLE);
			lin01.setVisibility(View.VISIBLE);
			lin02.setVisibility(View.VISIBLE);
		}
	}
	
	@Override
	protected void onDestroy()
	{
		super.onDestroy();
		bluzDeviceManProxy.removeOnModeChangedListener(this);
	}
	
}
