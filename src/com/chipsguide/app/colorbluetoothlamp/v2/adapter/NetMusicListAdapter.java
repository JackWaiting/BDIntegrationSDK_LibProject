package com.chipsguide.app.colorbluetoothlamp.v2.adapter;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import android.content.Context;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.view.NetMusicItemView;

public class NetMusicListAdapter extends IMusicListAdapter{
	private Context mContext;
	private List<Music> musics = new ArrayList<Music>();
	private int currentSelectedPosi = -1;
	private NetMusicItemView preSelectedItem;
	private boolean playing;
	private Map<String, Integer> urls = new HashMap<String, Integer>();
	
	public NetMusicListAdapter(Context context) {
		this.mContext = context;
	}
	
	@Override
	public void setMusicList(List<Music> musics) {
		this.musics = musics;
		int size = musics.size();
		for(int i = 0 ; i < size ; i++){
			Music m = musics.get(i);
			urls.put(m.getPath(), i);
		}
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
		item.render(music, position);
		if(item.getOnPlayButtonClickListener() == null){
			item.setOnPlayButtonClickListener(new OnClickListener() {
				@Override
				public void onClick(View v) {
					int position = (Integer) v.getTag();
					boolean playing = (Boolean) v.getTag(R.id.tag_is_playing);
					if(mListener != null){
						mListener.onItemPlayButtonClick(v, position, playing);
					}
				}
			});
		}
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
		if(position > getCount() - 1){
			return null;
		}
		return musics.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	public void setSelected(int position, boolean playing) {
		this.playing = playing;
		setSelected(position);
	}
	
	public int getSelected() {
		return currentSelectedPosi;
	}
	
	public void setSelected(String url, boolean playing) {
		int position = -1;
		if(urls.get(url) != null){
			position = urls.get(url);
		}
		setSelected(position, playing);
	}
	
	private OnItemPlayButtonClickListener mListener;
	public void setOnItemPlayButtonClickListener(OnItemPlayButtonClickListener listener) {
		mListener = listener;
	}
	
	public interface OnItemPlayButtonClickListener{
		/**
		 * 
		 * @param view
		 * @param position
		 * @param isPrePlaying 之前是否为播放状态
		 */
		void onItemPlayButtonClick(View view, int position, boolean isPrePlaying);
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
