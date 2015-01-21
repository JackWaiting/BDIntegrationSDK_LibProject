package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.widget.AdapterView.OnItemClickListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;

public class MyMusicFrag extends SimpleMusicFrag implements OnItemClickListener,SimpleMusicPlayListener{
	@Override
	protected int getLayoutId() {
		return R.layout.frag_my_music;
	}

	@Override
	public PlayType getPlayType() {
		return PlayType.Local;
	}

}
