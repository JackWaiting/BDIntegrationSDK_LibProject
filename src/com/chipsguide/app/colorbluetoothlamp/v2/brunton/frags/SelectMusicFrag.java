package com.chipsguide.app.colorbluetoothlamp.v2.brunton.frags;

import android.content.Context;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.adapter.SelectMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.been.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.media.PlayerManager.PlayType;

public class SelectMusicFrag extends SimpleMusicFrag {
	public static final String TAG = "local";
	
	public static SelectMusicFrag getInstance(Context context, String tag, OnItemSelectedListener listener){
		SelectMusicFrag frag = new SelectMusicFrag();
		frag.setFilterTag(tag);
		frag.setAdapter(new SelectMusicListAdapter(context));
		frag.setOnItemSelectedListener(listener);
		return frag;
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
		playerManager.getLocalMusics(this);
	}

}
