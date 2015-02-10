package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.Random;

import android.content.Context;
import android.util.Log;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.animation.Animation;
import android.view.animation.AnimationUtils;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.ShakeSettingActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ShakeManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.ShakeManager.OnShakeListener;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class ShakeFrag extends BaseFragment implements OnClickListener, OnShakeListener{
	private ShakeManager shakeUtil;
	private boolean isVisibleToUser;
	private PlayerManager playerManager;
	private ImageView shakeIv;
	
	private BluetoothDeviceManager mBluetoothDeviceManager;
	private BluetoothDeviceColorLampManager mBluetoothDeviceColorLampManager;
	@Override
	protected void initBase() {
		Context context = getActivity().getApplicationContext();
		shakeUtil = ShakeManager.getInstance(getActivity());
		shakeUtil.setOnShakeListener(this);
		playerManager = PlayerManager.getInstance(context);
		mBluetoothDeviceManager = ((CustomApplication) getActivity()
				.getApplicationContext()).getBluetoothDeviceManager();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_shake;
	}

	@Override
	protected void initView() {
		findViewById(R.id.btn_shake_setting).setOnClickListener(this);
		shakeIv = (ImageView) findViewById(R.id.iv_shake);
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
		startShakeAnim();
		int id = PreferenceUtil.getIntance(getActivity()).getShakeOption();
		String text = "";
		switch(id){
		case R.id.rb_random_color:
			text = "随机颜色";
			//后面封装到colorLampFrag中去
			mBluetoothDeviceColorLampManager = mBluetoothDeviceManager.getBluetoothDeviceColorLampManager();
	        Random r = new Random();
	        int red = r.nextInt(255)+1;// 范围是[0+1,255)
	        int green = r.nextInt(255)+1;// 范围是[0+1,255)
	        int blue = r.nextInt(255)+1;// 范围是[0+1,255)
	        if(mBluetoothDeviceColorLampManager != null)
	        {
	        		mBluetoothDeviceColorLampManager.setColor(red,green,blue);
	        }
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
	
	private void startShakeAnim() {
		Animation anim = AnimationUtils.loadAnimation(getActivity(), R.anim.shake);
		shakeIv.clearAnimation();
		shakeIv.startAnimation(anim);
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
