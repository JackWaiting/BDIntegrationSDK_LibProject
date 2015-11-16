package com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.utils.PreferenceUtil;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity {
	private PreferenceUtil preference;

	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_splash);
		this.loadingMainActivityDelayed();
		preference = PreferenceUtil.getIntance(this);
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

	private void loadingMainActivityDelayed() {
		new Timer().schedule(new TimerTask() {
			@Override
			public void run() {
				Intent intent = new Intent();
				intent.setClass(WelcomeActivity.this, MainActivity.class);
				startActivity(intent);
				finish();
			}
		}, 2 * 1000);
	}
}
