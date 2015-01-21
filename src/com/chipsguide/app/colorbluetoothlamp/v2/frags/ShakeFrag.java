package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.view.View;
import android.view.View.OnClickListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.ShakeSettingActivity;

public class ShakeFrag extends BaseFragment implements OnClickListener{

	@Override
	protected void initBase() {
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

}
