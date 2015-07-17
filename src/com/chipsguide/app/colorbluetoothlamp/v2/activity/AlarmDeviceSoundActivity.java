package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.content.Intent;
import android.view.View;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;

public class AlarmDeviceSoundActivity extends BaseActivity {
	public static final String EXTRA_SOUND_PATH = "sound_path";
	private int soundPath;
	
	@Override
	public int getLayoutId() {
		return R.layout.activity_alarm_device_sound;
	}

	@Override
	public void initBase() {
		CustomApplication.addActivity(this);
		soundPath = getIntent().getIntExtra(EXTRA_SOUND_PATH,0);
	}

	@Override
	public void initUI() {
	}

	@Override
	public void initData() {
		
	}

	@Override
	public void initListener() {
		findViewById(R.id.text_silent).setOnClickListener(this);
		findViewById(R.id.text_select).setOnClickListener(this);
	}
	
	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch(v.getId()){
		case R.id.text_silent:
			soundPath = 0;
			finish();
			break;
		case R.id.text_select:
			soundPath = 1;
			finish();
			break;
		}
	}
	
	@Override
	public void finish() {
		Intent intent = new Intent();
		intent.putExtra(EXTRA_SOUND_PATH, soundPath);
		setResult(RESULT_OK, intent);
		super.finish();
	}

}
