package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.view.View;
import android.view.View.OnLongClickListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.BluetoothConnectionActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.SleepAssistantActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.TimeLightActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.SidebarNavListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.WrapImageLoader;
import com.platomix.lib.update.bean.VersionEntity;
import com.platomix.lib.update.core.UpdateAgent;
import com.platomix.lib.update.listener.OnCheckUpdateListener;

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
			startActivity(TimeLightActivity.class);
			break;
		case 2:
			startActivity(SleepAssistantActivity.class);
			break;
		case 3:
			//startActivity(VersionUpdateActivity.class);
			if(checkNetwork(true)){
				checeNewVersion();
				showToast(R.string.check_new_version);
			}
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
	

	private void checeNewVersion() {
		UpdateAgent.setOnCheckUpdateListener(checkUpdateListener);
		UpdateAgent.setNotifycationVisibility(true);
		UpdateAgent.checkUpdate(CustomApplication.APP_SIGN, getActivity());
	}

	private OnCheckUpdateListener checkUpdateListener = new OnCheckUpdateListener() {
		@Override
		public boolean onCheckResult(int status, boolean force,
				VersionEntity entity) {
			if (status != UpdateStatus.YES) {
				showToast(R.string.no_newversion);
			}
			return false;
		}
	};

	
}
