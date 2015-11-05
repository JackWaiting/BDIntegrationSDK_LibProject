package com.chipsguide.app.colorbluetoothlamp.v2.brunton.adapter;

import java.util.List;

import android.widget.BaseAdapter;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.been.Music;

public abstract class IMusicListAdapter extends BaseAdapter {
	protected boolean playing;
	
	public abstract void setMusicList(List<Music> list);
	
	public abstract List<Music> getMusicList();
	
	public abstract void setSelected(int position);
	
	public void setPlaying(boolean playing){
		this.playing = playing;
	}
}
