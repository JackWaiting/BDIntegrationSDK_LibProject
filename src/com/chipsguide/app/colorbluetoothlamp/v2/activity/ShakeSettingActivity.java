package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioButton;
import android.widget.RadioGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;

public class ShakeSettingActivity extends BaseActivity implements OnClickListener{
	private PreferenceUtil preferenceUtil;
	private RadioButton mRandomColor;
	private RadioButton mLightToggle;
	private RadioButton mPlayerToggle;
	private RadioButton mNextSong;
	@Override
	public int getLayoutId() {
		return R.layout.activity_shake_setting;
	}

	@Override
	public void initBase() {
		preferenceUtil = PreferenceUtil.getIntance(getApplicationContext());
	}

	@Override
	public void initUI() {
		findViewById(R.id.right_btn).setVisibility(View.INVISIBLE);
		RadioGroup shakeOptionsRg = (RadioGroup) findViewById(R.id.rg_shake_options);
		mRandomColor = (RadioButton)findViewById(R.id.rb_random_color);
		mLightToggle = (RadioButton)findViewById(R.id.rb_light_toggle);
		mPlayerToggle = (RadioButton)findViewById(R.id.rb_player_toggle);
		mNextSong = (RadioButton)findViewById(R.id.rb_next_song);
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
	public void onClick(View v)
	{
		super.onClick(v);
		preferenceUtil.saveShakeOption(v.getId());
		finish();
	}
	
}
