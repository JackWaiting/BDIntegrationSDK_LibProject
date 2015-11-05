package com.chipsguide.app.colorbluetoothlamp.v2.brunton.adapter;

import android.content.Context;
import android.graphics.Color;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.view.SidebarNavItemView;

public class SidebarNavListAdapter extends BaseAdapter {
	private Context context;
	private String [] texts;
	private SidebarNavItemView seletedView;
	private int selectedPosi = -1;
	
	public SidebarNavListAdapter(Context context, String [] texts){
		this.context = context;
		this.texts = texts;
	}
	
	public void setSelected(int position){
		if(selectedPosi != position){
			if(seletedView != null){
				seletedView.setBackgroundColor(Color.TRANSPARENT);
			}
		}
		selectedPosi = position;
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return texts.length;
	}

	@Override
	public String getItem(int position) {
		return texts[position];
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		SidebarNavItemView itemView = null;
		if(convertView == null){
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, parent.getHeight() / 5);
			itemView = new SidebarNavItemView(context);
			itemView.setLayoutParams(params);
		}else{
			itemView = (SidebarNavItemView) convertView;
		}
		if(selectedPosi == position){
			//convertView.setBackgroundResource(R.drawable.list_longpressed_holo);
			seletedView = itemView;
			itemView.setSelect(true);
		}else{
			itemView.setSelect(false);
			itemView.setBackgroundColor(Color.TRANSPARENT);
		}
		
		int type = position == 0 ? 0 : ((position == getCount() - 1) ? 2 : 1);
		itemView.render(texts[position], type);
		return itemView;
	}

}
