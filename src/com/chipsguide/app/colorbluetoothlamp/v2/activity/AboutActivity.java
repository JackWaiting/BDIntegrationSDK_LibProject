package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.view.View;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.platomix.lib.update.bean.VersionEntity;
import com.platomix.lib.update.core.UpdateAgent;
import com.platomix.lib.update.listener.OnCheckUpdateListener;
import com.platomix.lib.update.util.AppInfoUtil;

public class AboutActivity extends BaseActivity {

	@Override
	public int getLayoutId() {
		return R.layout.activity_about;
	}

	@Override
	public void initBase() {
	}

	@Override
	public void initUI() {
		TextView appVersionTv = (TextView) findViewById(R.id.tv_app_version);
		String versionName = AppInfoUtil.getInstance(this).getAppVersionName();
		appVersionTv.setText(getString(R.string.app_name) + " V " + versionName);
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
		findViewById(R.id.item_version_update).setOnClickListener(this);
	}

	@Override
	public void onClick(View v) {
		super.onClick(v);
		switch (v.getId()) {
		case R.id.item_version_update:
			if (checkNetwork(true)) {
				checeNewVersion();
				showToast(R.string.check_new_version);
			}
			break;
		default:
			break;
		}
	}
	
	private void checeNewVersion() {
		UpdateAgent.setOnCheckUpdateListener(checkUpdateListener);
		UpdateAgent.setNotifycationVisibility(true);
		UpdateAgent.checkUpdate(CustomApplication.APP_SIGN, this);
	}
	
	private OnCheckUpdateListener checkUpdateListener = new OnCheckUpdateListener() {
		@Override
		public boolean onCheckResult(int status, boolean force,
				VersionEntity entity) {
			if (status != UpdateStatus.YES) {
				showToast(R.string.no_newversion);
			}
			return false;
		}
	};

	@Override
	public void updateVolume(int volume)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateConnectState(boolean isConnect)
	{
		// TODO Auto-generated method stub
		
	}
}
