package com.chipsguide.app.colorbluetoothlamp.v2.adapter;

import java.util.ArrayList;
import java.util.List;

import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.BaseAdapter;

import com.chipsguide.app.colorbluetoothlamp.v2.bean.Album;
import com.chipsguide.app.colorbluetoothlamp.v2.view.IAlbumListItemView;
/**
 * 专辑列表适配器
 * @author chiemy
 *
 */
public abstract class IAlbumListAdapter extends BaseAdapter {
	private List<Album> mList = new ArrayList<Album>();
	
	public void setList(List<Album> list) {
		if(list != null){
			this.mList = list;
			this.notifyDataSetChanged();
		}
	}
	
	public List<Album> getList() {
		return mList;
	}

	@Override
	public int getCount() {
		return mList.size();
	}

	@Override
	public Album getItem(int position) {
		return mList.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public IAlbumListItemView getView(final int position, View convertView, ViewGroup parent) {
		IAlbumListItemView itemView;
		if(convertView == null){
			itemView = getItemView();
		}else{
			itemView = (IAlbumListItemView) convertView;
		}
		itemView.render(getItem(position));
		itemView.setOnActionButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				if(actionButtonClickListener != null){
					actionButtonClickListener.onItemActionButtonClickListener(position,v);
				}
			}
		});
		return itemView;
	}
	
	public interface OnItemActionButtonClickListener{
		void onItemActionButtonClickListener(int position, View view);
	}
	
	public abstract IAlbumListItemView getItemView();

	private OnItemActionButtonClickListener actionButtonClickListener;
	public void setOnActionButtonClickListener(OnItemActionButtonClickListener listener){
		actionButtonClickListener = listener;
	}
	
}
