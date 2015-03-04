package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.frags;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class WrapMusicFrag extends BaseFragment implements
		OnBluetoothDeviceConnectionStateChangedListener {
	private BluetoothDeviceManagerProxy btDeviceManProxy;
	private boolean plugTFCard;
	private int currentConState;
	
	private MusicFrag musicFrag;

	@Override
	protected void initBase() {
		btDeviceManProxy = BluetoothDeviceManagerProxy
				.getInstance(getActivity().getApplicationContext());
		btDeviceManProxy
				.addOnBluetoothDeviceConnectionStateChangedListener(this);
		registBroadcase();
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_wrap_music;
	}

	@Override
	protected void initView() {
		refreshMusicFrag();
	}
	
	private void refreshMusicFrag() {
		plugTFCard = btDeviceManProxy.isPlugTFCard();
		musicFrag = MusicFrag.newInstance(plugTFCard);
		//在WrapMusicFrag界面不可见时，调用此方法会报错。改为commitAllowingStateLoss()不会报错，但界面不刷新
		getChildFragmentManager().beginTransaction().replace(R.id.content_layout, musicFrag).commitAllowingStateLoss();
	}
		
	private boolean register;
	private void registBroadcase() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDeviceManagerProxy.ACTION_TF_CARD_PLUG_CHANGED);
		filter.addAction(BluetoothDeviceManagerProxy.ACTION_MODE_CHANGE);
		getActivity().registerReceiver(bluzBroadcaseReceiver, filter);
		register = true;
	}
	
	private BroadcastReceiver bluzBroadcaseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDeviceManagerProxy.ACTION_MODE_CHANGE.equals(action)) {
				int newMode = intent.getIntExtra(BluetoothDeviceManagerProxy.EXTRA_NEW_MODE, -1);
				if(newMode == BluetoothDeviceManager.Mode.CARD){
					musicFrag.setCurrentItem(1);
				}
			} else {
				plugTFCard = intent.getBooleanExtra(
						BluetoothDeviceManagerProxy.EXTRA_PLUG_IN, false);
				boolean firstCallback = intent.getBooleanExtra(BluetoothDeviceManagerProxy.EXTRA_FIRST_CARD_PLUG, true);
				if (BluetoothDeviceManagerProxy.ACTION_TF_CARD_PLUG_CHANGED
						.equals(action)) {
					if (!firstCallback && plugTFCard) {
						showToast(R.string.tf_card_has_plugin);
					}else if(!firstCallback){
						showToast(R.string.tf_card_has_plugout);
					}
					if(!pause){
						refreshMusicFrag();
					}
				} 
			}
		}
	};

	@Override
	protected void initData() {
	}

	@Override
	public void onBluetoothDeviceConnectionStateChanged(BluetoothDevice device,
			int state) {
		currentConState = state;
		switch (state) {
		case BluetoothDeviceManager.ConnectionState.CONNECTED:
			//此界面处于后台时，调用refreshMusicFrag()方法会报错，所以此界面不可见时不刷新
			if(!pause && btDeviceManProxy.isPlugTFCard()){
				refreshMusicFrag();
			}
			break;
		case BluetoothDeviceManager.ConnectionState.DISCONNECTED:
		case BluetoothDeviceManager.ConnectionState.SPP_DISCONNECTED:
			//此界面处于后台时，调用refreshMusicFrag()方法会报错，所以此界面不可见时不刷新
			if(!pause && plugTFCard){
				refreshMusicFrag();
			}
			break;
		}
	}
	
	private boolean pause;
	private int preConnState;
	private boolean prePlugTF;
	
	@Override
	public void onPause() {
		super.onPause();
		pause = true;
		preConnState = currentConState;
		prePlugTF = plugTFCard;
	}
	
	@Override
	public void onResume() {
		super.onResume();
		pause = false;
		//onPause时的状态和当前状态不一致或插拔TF卡
		if(preConnState != currentConState || plugTFCard != prePlugTF){
			refreshMusicFrag();
		}
	}
	
	@Override
	public void onDestroy() {
		super.onDestroy();
		if (register) {
			getActivity().unregisterReceiver(bluzBroadcaseReceiver);
		}
		btDeviceManProxy.removeOnBluetoothDeviceConnectionStateChangedListener(this);
	}
}
