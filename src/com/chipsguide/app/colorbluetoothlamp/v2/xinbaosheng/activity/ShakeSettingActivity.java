package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.activity;

import android.view.View;
import android.view.View.OnClickListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.utils.PreferenceUtil;

public class ShakeSettingActivity extends BaseActivity implements OnClickListener , OnCheckedChangeListener{
	private PreferenceUtil preferenceUtil;
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
		shakeOptionsRg.setOnCheckedChangeListener(this);
		int id = preferenceUtil.getShakeOption();
		shakeOptionsRg.check(id);
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		preferenceUtil.saveShakeOption(checkedId);
	}
	
}
