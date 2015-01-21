package com.chipsguide.app.colorbluetoothlamp.v2.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.view.NetMusicItemView;

public class NetMusicListAdapter extends IMusicListAdapter{
	private Context mContext;
	private List<Music> musics = new ArrayList<Music>();
	private int currentSelectedPosi = -1;
	private NetMusicItemView preSelectedItem;
	private boolean playing;
	
	public NetMusicListAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public void setMusicList(List<Music> musics) {
		this.musics = musics;
		this.notifyDataSetChanged();
	}
	
	@Override
	public List<Music> getMusicList() {
		return musics;
	}
	
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		NetMusicItemView item;
		if(convertView == null){
			item = new NetMusicItemView(mContext);
		}else{
			item = (NetMusicItemView) convertView;
		}
		Music music = getItem(position);
		item.render(music);
		if(currentSelectedPosi == position){
			preSelectedItem = item;
			item.setSelected(playing);
		}else{
			item.disSelected();
		}
		return item;
	}

	@Override
	public int getCount() {
		return musics.size();
	}

	@Override
	public Music getItem(int position) {
		return musics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public void setSelected(int position) {
		if(preSelectedItem != null){
			preSelectedItem.disSelected();
		}
		currentSelectedPosi = position;
		this.notifyDataSetChanged();
	}
	
}
