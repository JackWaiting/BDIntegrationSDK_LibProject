package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.adapter;

import android.content.Context;
import android.view.View;
import android.view.ViewGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.adapter.IAlbumListAdapter.OnItemActionButtonClickListener;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean.Album;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.AlbumListItemView;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view.IAlbumListItemView;

public class AlbumListAdapter extends IAlbumListAdapter implements OnItemActionButtonClickListener{
	private Context mContext;
	
	public AlbumListAdapter(Context context){
		mContext = context;
		super.setOnActionButtonClickListener(this);
	}
	
	@Override
	public IAlbumListItemView getView(int position, View convertView, ViewGroup parent) {
		IAlbumListItemView itemView =  super.getView(position, convertView, parent);
		final Album album = getItem(position);
		itemView.render(album);
		return itemView;
	}

	@Override
	public IAlbumListItemView getItemView() {
		return new AlbumListItemView(mContext);
	}
	
	private OnItemActionButtonClickListener itemActionBtnClickListener;
	@Override
	public void setOnActionButtonClickListener(
			OnItemActionButtonClickListener listener) {
		itemActionBtnClickListener = listener;
	}

	@Override
	public void onItemActionButtonClickListener(int position, View view) {
		final Album album = getItem(position);
		if(!album.isStore()){
			album.setStore(true);
			//albumDao.insertOrUpdateStore(album);
		}else{
			//albumDao.setStore(getItem(position).getId(),false);
		}
		if(itemActionBtnClickListener != null){
			itemActionBtnClickListener.onItemActionButtonClickListener(position, view);
		}
	}
}
