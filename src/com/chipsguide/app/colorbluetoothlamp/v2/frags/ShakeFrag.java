package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.view.View;
import android.view.View.OnClickListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.ShakeSettingActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ShakeUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ShakeUtil.OnShakeListener;

public class ShakeFrag extends BaseFragment implements OnClickListener, OnShakeListener{
	private ShakeUtil shakeUtil;
	@Override
	protected void initBase() {
		shakeUtil = new ShakeUtil(getActivity());
		shakeUtil.setOnShakeListener(this);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_shake;
	}

	@Override
	protected void initView() {
		findViewById(R.id.btn_shake_setting).setOnClickListener(this);
	}

	@Override
	protected void initData() {
	}
	
	@Override
	public void onClick(View v) {
		switch (v.getId()) {
		case R.id.btn_shake_setting:
			startActivity(ShakeSettingActivity.class);
			break;
		default:
			break;
		}
	}
	
	@Override
	public void onShake() {
		showToast("摇动了");
	}
	
	@Override
	public void onResume() {
		super.onResume();
		shakeUtil.start();
	}
	
	@Override
	public void onPause() {
		super.onPause();
		shakeUtil.stop();
	}
}
