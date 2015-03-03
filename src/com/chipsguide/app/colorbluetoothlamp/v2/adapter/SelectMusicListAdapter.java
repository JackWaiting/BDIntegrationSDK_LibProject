package com.chipsguide.app.colorbluetoothlamp.v2.adapter;

import android.content.Context;

import com.chipsguide.app.colorbluetoothlamp.v2.view.IMusicItemView;
import com.chipsguide.app.colorbluetoothlamp.v2.view.SelectMusicItemView;
/**
 * 选择音乐适配器
 * @author chiemy
 *
 */
public class SelectMusicListAdapter extends SimpleMusicListAdapter {
	public SelectMusicListAdapter(Context context) {
		super(context);
	}

	@Override
	protected IMusicItemView getItemView() {
		return new SelectMusicItemView(mContext);
	}
	
}
