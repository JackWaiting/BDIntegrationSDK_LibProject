package com.chipsguide.app.colorbluetoothlamp.v2.brunton.frags;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity.AboutActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity.BluetoothConnectionActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity.SleepAssistantActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity.TimeLightActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.adapter.SidebarNavListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.utils.WrapImageLoader;
//左边布局
public class NavFrag extends BaseFragment {
	private OnNavItemClickListener mNavItemClickListener;
	private String [] menuItemTitles;
	private LampManager mLampManager;
	private BluetoothDeviceManagerProxy mManagerProxy;
	
	public interface OnNavItemClickListener{
		void onItemClick(int position, String title);
	}
	
	@Override
	protected void initBase() {
		menuItemTitles = getResources().getStringArray(R.array.menu_items);
		mLampManager = LampManager.getInstance(getActivity());
		mManagerProxy = BluetoothDeviceManagerProxy.getInstance(getActivity());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_nav_layout;
	}

	@Override
	protected void initView() {
		final ListView navLv = (ListView) findViewById(R.id.nav_list);
		final SidebarNavListAdapter adapter = new SidebarNavListAdapter(getActivity(),menuItemTitles);
		navLv.setAdapter(adapter);
		navLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view, int position,
					long id) {
				//adapter.setSelected(position);
				onNavagationItemClick(position);
			}
		});
		findViewById(R.id.moon).setOnLongClickListener(new OnLongClickListener() {
			@Override
			public boolean onLongClick(View v) {
				WrapImageLoader.getInstance(getActivity()).clearCache();
				return false;
			}
		});
	}
	
	private void onNavagationItemClick(int position) {
		switch(position){
		case 0:
			startActivity(BluetoothConnectionActivity.class);
			break;
		case 1:
			if(!CustomApplication.isConnect)
			{
				showToast(R.string.conn_ble);
				return;
			}
			if(mLampManager.issupportOfflineAlarm())
			{
				mManagerProxy.changeToAlarm();
				CustomApplication.isClickAlarm = true;
			}else
			{
				startActivity(TimeLightActivity.class);
			}
			break;
		case 2:
			if(!CustomApplication.isConnect)
			{
				showToast(R.string.conn_ble);
				return;
			}
			startActivity(SleepAssistantActivity.class);
			break;
		case 3:
			startActivity(AboutActivity.class);
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
	
}
