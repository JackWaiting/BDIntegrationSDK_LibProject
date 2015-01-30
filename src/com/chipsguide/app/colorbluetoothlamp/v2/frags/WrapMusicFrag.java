package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class WrapMusicFrag extends BaseFragment implements
		OnBluetoothDeviceConnectionStateChangedListener {
	private BluetoothDeviceManagerProxy btDeviceManProxy;
	private boolean plugTFCard;

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
		MusicFrag frag = MusicFrag.newInstance(plugTFCard);
		getChildFragmentManager().beginTransaction().replace(R.id.content_layout, frag).commit();
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
				
			} else {
				boolean plug = intent.getBooleanExtra(
						BluetoothDeviceManagerProxy.EXTRA_PLUG_IN, false);
				if (BluetoothDeviceManagerProxy.ACTION_TF_CARD_PLUG_CHANGED
						.equals(action)) {
					refreshMusicFrag();
					if (plug) {
						showToast(R.string.tf_card_has_plugin);
					}else{
						showToast(R.string.tf_card_has_plugout);
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
		switch (state) {
		case BluetoothDeviceManager.ConnectionState.CONNECTED:
			if(btDeviceManProxy.isPlugTFCard()){
				refreshMusicFrag();
			}
			break;
		case BluetoothDeviceManager.ConnectionState.DISCONNECTED:
		case BluetoothDeviceManager.ConnectionState.SPP_DISCONNECTED:
			if(plugTFCard){
				refreshMusicFrag();
			}
			break;
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
