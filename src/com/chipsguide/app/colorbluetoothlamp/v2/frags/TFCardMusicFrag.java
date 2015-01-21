package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;

public class TFCardMusicFrag extends SimpleMusicFrag implements SimpleMusicPlayListener{

	@Override
	public PlayType getPlayType() {
		return PlayType.Bluz;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_tf_card_music;
	}

}
