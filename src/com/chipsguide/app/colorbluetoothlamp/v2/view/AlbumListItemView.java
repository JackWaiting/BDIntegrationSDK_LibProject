package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.content.Context;
import android.view.View;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Album;

public class AlbumListItemView extends IAlbumListItemView {
	private boolean isStore;
	public AlbumListItemView(Context context) {
		super(context);
	}

	@Override
	public int getInflateLayout() {
		return R.layout.album_list_item;
	}
	
	@Override
	public void render(Album album) {
		super.render(album);
		setStore(album.isStore());
	}
	
	@Override
	public void setOnActionButtonClickListener(final OnClickListener listener) {
		super.setOnActionButtonClickListener(new OnClickListener() {
			@Override
			public void onClick(View v) {
				setStore(!isStore);
				v.setTag(!isStore);
				if(listener != null){
					listener.onClick(v);
				}
			}
		});
	}
	
	private void setStore(boolean store){
		isStore = store;
		//ImageView iv = (ImageView) findViewById(R.id.action_btn);
		if(!store){
			//iv.setImageResource(R.drawable.btn_add_collection_selector);
		}else{
			//iv.setImageResource(R.drawable.btn_celect_sel);
		}
	}
	

}
