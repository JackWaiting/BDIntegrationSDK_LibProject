package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.content.Context;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.SimpleMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy.OnModeChangedListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public class MyMusicFrag extends SimpleMusicFrag implements OnModeChangedListener{
	public static final String TAG = "local";
	private BluetoothDeviceManagerProxy mManagerProxy;
	
	public static MyMusicFrag getInstance(Context context, String tag, SimpleMusicListAdapter adapter, OnItemSelectedListener listener){
		MyMusicFrag frag = new MyMusicFrag();
		frag.setFilterTag(tag);
		frag.setAdapter(adapter);
		frag.setOnItemSelectedListener(listener);
		return frag;
	}
	
	@Override
	public void onResume()
	{
		super.onResume();
		mManagerProxy = BluetoothDeviceManagerProxy.getInstance(getActivity());
		mManagerProxy.addOnModeChangedListener(this);
	}
	
	@Override
	protected int getLayoutId() {
		return R.layout.frag_my_music;
	}

	@Override
	public PlayType getPlayType() {
		return PlayType.Local;
	}
	
	@Override
	public String getFilter(Music music) {
		return music.getLocalPath();
	}

	@Override
	public void onLoadPlayList() {
		playerManager.loadLocalMusic(this, false);	
	}
	
	@Override
	public void onModeChanged(int newMode)
	{
		if (newMode == BluetoothDeviceManager.Mode.A2DP)
		{
			playerManager.loadLocalMusic(this, false);
		}
	}
}
