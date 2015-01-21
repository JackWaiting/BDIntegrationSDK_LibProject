package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.view.View;
import android.view.View.OnClickListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class ShakeSettingActivity extends BaseActivity implements OnClickListener{

	@Override
	public int getLayoutId() {
		return R.layout.activity_shake_setting;
	}

	@Override
	public void initBase() {
	}

	@Override
	public void initUI() {
		findViewById(R.id.right_btn).setVisibility(View.INVISIBLE);
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}
	
}
