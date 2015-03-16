package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.ShakeSettingActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ShakeManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ShakeManager.OnShakeListener;

public class ShakeFrag extends BaseFragment implements OnClickListener, OnShakeListener{
	
	private LampManager mLampManager;
	private ShakeManager shakeUtil;
	private boolean isVisibleToUser;
	private PlayerManager playerManager;
	private ImageView shakeIv;
	private TextView currentSetTv;
	private int currentSetId;
	
	@Override
	protected void initBase() {
		Context context = getActivity().getApplicationContext();
		shakeUtil = ShakeManager.getInstance(getActivity());
		shakeUtil.setOnShakeListener(this);
		playerManager = PlayerManager.getInstance(context);
		mLampManager = LampManager.getInstance(getActivity());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_shake;
	}

	@Override
	protected void initView() {
		findViewById(R.id.btn_shake_setting).setOnClickListener(this);
		shakeIv = (ImageView) findViewById(R.id.iv_shake);
		currentSetTv = (TextView) findViewById(R.id.tv_current_set);
	}

	@Override
	protected void initData() {
		currentSetId = PreferenceUtil.getIntance(getActivity()).getShakeOption();
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
			currentSetId = PreferenceUtil.getIntance(getActivity()).getShakeOption();
			currentSetTv.setText(getTextFromId(currentSetId));
		}else{
			if(shakeUtil != null){
				shakeUtil.stop();
			}
		}
	}
	
	@Override
	public void onShake() {
		startShakeAnim();
		String text = getTextFromId(currentSetId);
		switch(currentSetId){
		case R.id.rb_random_color:
			mLampManager.randomColor();
			break;
		case R.id.rb_light_toggle:
			mLampManager.lampOff();
			break;
		case R.id.rb_player_toggle:
			playerManager.toggle();
			break;
		case R.id.rb_next_song:
			if(playerManager.isPlaying()){
				playerManager.next();
			}
			break;
		}
		Log.d("ShakeFrag", "摇一摇>>" + text);
	}
	
	private void startShakeAnim() {
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
		shakeIv.clearAnimation();
		shakeIv.startAnimation(anim);
	}
	
	private String getTextFromId(int id) {
		int resId = R.string.random_color;
		switch(id){
		case R.id.rb_light_toggle:
			resId = R.string.toggle_light;
			break;
		case R.id.rb_player_toggle:
			resId = R.string.play_pause;
			break;
		case R.id.rb_next_song:
			resId = R.string.next_song;
			break;
		}
		return getString(resId);
	}
	
	@Override
	public void onResume() {
		super.onResume();
		if(isVisibleToUser){
			if(shakeUtil != null){
				shakeUtil.start();
			}
			currentSetId = PreferenceUtil.getIntance(getActivity()).getShakeOption();
			currentSetTv.setText(getTextFromId(currentSetId));
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
