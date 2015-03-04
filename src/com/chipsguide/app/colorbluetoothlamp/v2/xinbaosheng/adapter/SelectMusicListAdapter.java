package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.adapter;

import android.content.Context;

import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.IMusicItemView;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.SelectMusicItemView;
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
