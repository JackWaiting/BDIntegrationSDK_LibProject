package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.ShakeSettingActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ShakeManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ShakeManager.OnShakeListener;

public class ShakeFrag extends BaseFragment implements OnClickListener, OnShakeListener{
	private ShakeManager shakeUtil;
	private boolean isVisibleToUser;
	private PlayerManager playerManager;
	@Override
	protected void initBase() {
		shakeUtil = ShakeManager.getInstance(getActivity());
		shakeUtil.setOnShakeListener(this);
		playerManager = PlayerManager.getInstance(getActivity().getApplicationContext());
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
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		this.isVisibleToUser = isVisibleToUser;
		if(isVisibleToUser){
			if(shakeUtil != null){
				shakeUtil.start();
			}
		}else{
			if(shakeUtil != null){
				shakeUtil.stop();
			}
		}
	}
	
	@Override
	public void onShake() {
		int id = PreferenceUtil.getIntance(getActivity()).getShakeOption();
		String text = "";
		switch(id){
		case R.id.rb_random_color:
			text = "随机颜色";
			break;
		case R.id.rb_light_toggle:
			text = "开关灯";
			break;
		case R.id.rb_player_toggle:
			text = "播放暂停";
			playerManager.toggle();
			break;
		case R.id.rb_next_song:
			text = "下一曲";
			if(playerManager.isPlaying()){
				playerManager.next();
			}
			break;
		}
		Log.d("ShakeFrag", "摇一摇>>" + text);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(isVisibleToUser){
			if(shakeUtil != null){
				shakeUtil.start();
			}
		}
	}
	
	@Override
	public void onPause() {
		super.onPause();
		if(shakeUtil != null){
			shakeUtil.stop();
		}
	}
}
