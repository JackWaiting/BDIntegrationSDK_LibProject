package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.graphics.Color;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.BaseAdapter;
import android.widget.ListView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.BluetoothConnectionActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.SleepAssistantActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.TimeLightActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.VersionUpdateActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.ViewHolder;

public class NavFrag extends BaseFragment {
	private OnNavItemClickListener mNavItemClickListener;
	private String [] menuItemTitles;
	
	public interface OnNavItemClickListener{
		void onItemClick(int position, String title);
	}
	
	@Override
	protected void initBase() {
		menuItemTitles = getResources().getStringArray(R.array.menu_items);
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_nav_layout;
	}

	@Override
	protected void initView() {
		final ListView navLv = (ListView) findViewById(R.id.nav_list);
		final NavListAdapter adapter = new NavListAdapter();
		navLv.setAdapter(adapter);
		navLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				adapter.setSelected(position);
				onNavagationItemClick(position);
			}
		});
	}
	
	private void onNavagationItemClick(int position) {
		switch(position){
		case 0:
			startActivity(BluetoothConnectionActivity.class);
			break;
		case 1:
			startActivity(TimeLightActivity.class);
			break;
		case 2:
			startActivity(SleepAssistantActivity.class);
			break;
		case 3:
			startActivity(VersionUpdateActivity.class);
			break;
		}
		if(mNavItemClickListener != null){
			mNavItemClickListener.onItemClick(position, menuItemTitles[position]);
		}
	}

	@Override
	protected void initData() {

	}
	
	public void setOnItemClickListener(OnNavItemClickListener listener){
		mNavItemClickListener = listener;
	}
	
	private class NavListAdapter extends BaseAdapter{
		private View seletedView;
		private int selectedPosi;

		@Override
		public int getCount() {
			return menuItemTitles.length;
		}

		@Override
		public Object getItem(int positon) {
			return positon;
		}

		@Override
		public long getItemId(int positon) {
			return 0;
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
		public View getView(int position, View convertView, ViewGroup parent) {
			if(convertView == null){
				convertView = LayoutInflater.from(getActivity()).inflate(R.layout.nav_list_item, parent, false);
			}
			
			TextView titleTv = ViewHolder.get(convertView, R.id.item_title_tv);
			titleTv.setText(menuItemTitles[position]);
			
			if(selectedPosi == position){
				//convertView.setBackgroundResource(R.drawable.list_longpressed_holo);
				seletedView = convertView;
			}else{
				convertView.setBackgroundColor(Color.TRANSPARENT);
			}
			
			return convertView;
		}
	}

}
