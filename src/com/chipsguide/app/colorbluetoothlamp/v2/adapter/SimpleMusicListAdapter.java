package com.chipsguide.app.colorbluetoothlamp.v2.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.view.IMusicItemView;
import com.chipsguide.app.colorbluetoothlamp.v2.view.SimpleMusicItem;
/**
 * 手机音乐，设备音乐列表适配器
 * @author chiemy
 *
 */
public class SimpleMusicListAdapter extends IMusicListAdapter {
	protected Context mContext;
	private List<Music> mList = new ArrayList<Music>();
	private int currentSelectedPosi = -1;
	private IMusicItemView preSelectedItem;
	private boolean playing;
	private boolean bluetoothDeviceMusic;
	
	public SimpleMusicListAdapter(Context context){
		this.mContext = context;
	}
	/**
	 * 是否是蓝牙设备音乐（蓝牙设备音乐，无法获得音乐时长，所以item布局中不显示时长）
	 * @param bluetoothDeviceMusic
	 */
	public void setBluetoothDeviceMusic(boolean bluetoothDeviceMusic) {
		this.bluetoothDeviceMusic = bluetoothDeviceMusic;
	}
	
	@Override
	public List<Music> getMusicList() {
		return mList;
	}
	
	@Override
	public void setMusicList(List<Music> list) {
		if(list != null){
			mList.clear();
			mList.addAll(list);
		}
		this.notifyDataSetChanged();
	}
	
	public void clear() {
		mList.clear();
		this.notifyDataSetChanged();
	}
	
	@Override
	public void setSelected(int position){
		if(preSelectedItem != null){
			preSelectedItem.disSelected();
		}
		currentSelectedPosi = position;
		this.notifyDataSetChanged();
	}
	
	public void setSelected(int position, boolean playing){
		this.playing = playing;
		setSelected(position);
	}
	
	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Music getItem(int location) {
		return mList.get(location);
	}

	@Override
	public long getItemId(int location) {
		return location;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		IMusicItemView item = null;
		if(convertView == null){
			item = getItemView();
		}else{
			item = (IMusicItemView) convertView;
		}
		item.render(position, getItem(position), bluetoothDeviceMusic);
		if(currentSelectedPosi == position){
			preSelectedItem = item;
			item.setSelected(playing);
		}else{
			item.disSelected();
		}
		return item;
	}
	
	protected IMusicItemView getItemView() {
		return new SimpleMusicItem(mContext);
	}
	
}
