package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.activity;

import java.util.Timer;
import java.util.TimerTask;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.umeng.analytics.MobclickAgent;

public class WelcomeActivity extends Activity {
	@Override
	protected void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		this.setContentView(R.layout.activity_splash);
		this.loadingMainActivityDelayed();
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
				startActivity(new Intent(WelcomeActivity.this,
						MainActivity.class));

				finish();
			}
		}, 2 * 1000);
	}
}
