package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PixelUtil;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
import com.platomix.lib.update.util.AppInfoUtil;

public class MusicFrag extends BaseFragment implements OnPageChangeListener,
		OnCheckedChangeListener {
	private static final String TF = "tf";
	private ViewPager viewPager;
	private RadioGroup topNavRg;
	private PlayerManager manager;
	private boolean hasUpdate;
	private MyPlayListener playListener;

	private MyPagerAdapter adapter;
	private boolean tf;
	private boolean zh;

	public static MusicFrag newInstance(boolean tf) {
		MusicFrag frag = new MusicFrag();
		Bundle bundle = new Bundle();
		bundle.putBoolean(TF, tf);
		frag.setArguments(bundle);
		return frag;
	}

	@Override
	protected void initBase() {
		AppInfoUtil appUtil = AppInfoUtil.getInstance(getActivity()
				.getApplicationContext());
		zh = appUtil.isZh();
		playListener = new MyPlayListener(this);
		manager = PlayerManager.getInstance(getActivity()
				.getApplicationContext());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_music;
	}

	private List<Fragment> fragments;
	@Override
	protected void initView() {
		tf = getArguments().getBoolean(TF);
		fragments = new ArrayList<Fragment>();
		fragments.add(new MyMusicFrag());
		if (tf) {
			fragments.add(new TFCardMusicFrag());
		}
		if (zh) {
			fragments.add(new CloudMusicFrag());
		}

		adapter = new MyPagerAdapter(getChildFragmentManager());
		adapter.setList(fragments);

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		topNavRg = (RadioGroup) findViewById(R.id.rg_top_nav);
		initTopNav();
		for(int i = 0;i<topNavRg.getChildCount();i++)
		{
			topNavRg.getChildAt(i).setOnClickListener(new OnClickListener()
			{
				
				@Override
				public void onClick(View v)
				{
					 int item = v.getId();
					selectPage(item);
				}
			});
		}
		
		topNavRg.setOnCheckedChangeListener(this);
	}

	private void initTopNav() {
		int size = fragments.size();
		int textRes[] = {R.string.my_music, R.string.tf_card_music, R.string.cloud_music};
		int rbDrawables[] = new int[size];
		if(size == 1){
			rbDrawables[0] = R.drawable.nav_rb_bg_singl;
		}else if(size == 2){
			rbDrawables[0] = R.drawable.nav_rb_bg_left;
			rbDrawables[1] = R.drawable.nav_rb_bg_right;
			if(zh){
				textRes[1] = R.string.cloud_music;
			}
		}else if(size == 3){
			rbDrawables[0] = R.drawable.nav_rb_bg_left;
			rbDrawables[1] = R.drawable.nav_rb_bg_mid;
			rbDrawables[2] = R.drawable.nav_rb_bg_right;
		}
		int screenSize = getResources().getDisplayMetrics().widthPixels;
		int width = (int)(screenSize / Math.max(size + 0.5, 3));
		int padding = PixelUtil.dp2px(5, getActivity());
		for (int i = 0; i < size; i++) {
			RadioButton tempButton = (RadioButton) LayoutInflater.from(getActivity()).inflate(R.layout.music_nav_rb, topNavRg, false);
			tempButton.setBackgroundResource(rbDrawables[i]);
			tempButton.setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
			tempButton.setPadding(0, padding, 0, padding);
			tempButton.setText(textRes[i]);
			tempButton.setId(i);
			LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(width, LinearLayout.LayoutParams.WRAP_CONTENT);
			topNavRg.addView(tempButton, params);
		}
		topNavRg.check(0);
	}

	@Override
	protected void initData() {
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {
		private List<Fragment> fragments = new ArrayList<Fragment>();

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		public void setList(List<Fragment> list) {
			if (list == null) {
				return;
			}
			fragments = list;
			notifyDataSetChanged();
		}

		public List<Fragment> getFragments() {
			return fragments;
		}

		@Override
		public Fragment getItem(int arg0) {
			return fragments.get(arg0);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}

	}

	public void updateUI(boolean isPlaying) {
		if (manager == null) {
			return;
		}
		onMusicPlayingStateChange(isPlaying);
		onMusicChange(null, null);
	}

	public void setCurrentItem(int item) {
		if(topNavRg != null)
		{
			topNavRg.check(item);
		}
		if(viewPager != null)
		{
			viewPager.setCurrentItem(item);
		}
	}

	private static class MyPlayListener extends PlayListener {
		private WeakReference<MusicFrag> ref;

		private MyPlayListener(MusicFrag frag) {
			ref = new WeakReference<MusicFrag>(frag);
		}

		@Override
		public void onMusicStart(String musicSymbol) {
			MusicFrag frag = ref.get();
			if (frag != null) {
				frag.updateUI(true);
			}
		}

		@Override
		public void onMusicProgress(String musicSymbol, long duration,
				long currentDuration, int percent) {
			MusicFrag frag = ref.get();
			if (frag != null) {
				if (!frag.hasUpdate) {
					frag.hasUpdate = true;
					frag.onMusicChange(null, null);
				}
			}
		}

		@Override
		public void onMusicPause(String musicSymbol) {
			MusicFrag frag = ref.get();
			if (frag != null) {
				frag.updateUI(false);
			}
		}

		@Override
		public void onMusicError(String musicSymbol, int what, int extra) {
		}

		@Override
		public void onMusicChange(String musicSymbol) {
		}

		@Override
		public void onMusicBuffering(String musicSymbol, int percent) {
		}

		@Override
		public void onPlayTypeChange(PlayType oldType, PlayType newType) {
			MusicFrag frag = ref.get();
			if (frag != null) {
				frag.onMusicChange(oldType, newType);
			}
		}
	}

	private void onMusicPlayingStateChange(boolean playing) {
		int size = adapter.getFragments().size();
		for (int i = 0; i < size; i++) {
			Fragment frag = adapter.getFragments().get(i);
			if (frag instanceof SimpleMusicPlayListener) {
				SimpleMusicPlayListener listener = (SimpleMusicPlayListener) frag;
				listener.onMusicPlayStateChange(playing);
			}
		}
	}

	private void onMusicChange(PlayType oldType, PlayType newType) {
		int size = adapter.getFragments().size();
		for (int i = 0; i < size; i++) {
			Fragment frag = adapter.getFragments().get(i);
			if (frag instanceof SimpleMusicPlayListener) {
				SimpleMusicPlayListener listener = (SimpleMusicPlayListener) frag;
				listener.onMusicChange();
			}
		}
	}

	@Override
	public void onPause() {
		super.onPause();
		hasUpdate = false;
	}

	@Override
	public void onResume() {
		super.onResume();
		hasUpdate = false;
		// 调转到播放界面会使监听失效，所以要在onResume()中重新设置
		manager.setPlayListener(playListener, null, true);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int item) {
		topNavRg.check(item);
		selectPage(item);
	}

	private void selectPage(int item)
	{
		switch (item) {
		case 0:
			BluetoothDeviceManagerProxy.changeToA2DPMode();
			break;
		case 1:
			if( CustomApplication.getMode() != BluetoothDeviceManager.Mode.CARD)
			{
				bluzProxy
				.getBluetoothDeviceMusicManager(BluetoothDeviceManager.Mode.CARD);
			}
			break;
		case 2:
			BluetoothDeviceManagerProxy.changeToA2DPMode();
			break;
		}
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int item = 0;
		switch (checkedId) {
		case 0:
			CustomApplication.pageItem = item;
			break;
		case 1:
			item = 1;
			CustomApplication.pageItem = item;
			break;
		case 2:
			item = 2;
			if (!tf) {
				item = 1;
			}
			CustomApplication.pageItem = item;
			break;
		}
		topNavRg.check(item);
		viewPager.setCurrentItem(item);

	}

}
