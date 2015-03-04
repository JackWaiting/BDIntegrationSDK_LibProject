package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.frags;

import android.content.Context;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.adapter.SimpleMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.media.PlayerManager.PlayType;

public class TFCardMusicFrag extends SimpleMusicFrag implements SimpleMusicPlayListener{
	public static final String TAG = "bluz";

	public static TFCardMusicFrag getInstance(Context context, String tag, SimpleMusicListAdapter adapter, OnItemSelectedListener listener){
		TFCardMusicFrag frag = new TFCardMusicFrag();
		frag.setFilterTag(tag);
		frag.setAdapter(adapter);
		frag.setOnItemSelectedListener(listener);
		return frag;
	}
	
	@Override
	public PlayType getPlayType() {
		return PlayType.Bluz;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_tf_card_music;
	}

}
