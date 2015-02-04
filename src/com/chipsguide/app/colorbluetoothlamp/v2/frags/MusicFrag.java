package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.view.View;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;

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
	
	public static MusicFrag newInstance(boolean tf) {
		MusicFrag frag = new MusicFrag();
		Bundle bundle = new Bundle();
		bundle.putBoolean(TF, tf);
		frag.setArguments(bundle);
		return frag;
	}

	@Override
	protected void initBase() {
		playListener = new MyPlayListener(this);
		manager = PlayerManager.getInstance(getActivity()
				.getApplicationContext());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_music;
	}

	@Override
	protected void initView() {
		tf = getArguments().getBoolean(TF);
		List<Fragment> fragments = new ArrayList<Fragment>();
		findViewById(R.id.rb_tf_card_music).setVisibility(tf ? View.VISIBLE : View.GONE);
		fragments.add(new MyMusicFrag());
		if(tf){
			fragments.add(new TFCardMusicFrag());
		}
		fragments.add(new CloudMusicFrag());
		adapter = new MyPagerAdapter(getChildFragmentManager());
		adapter.setList(fragments);

		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setAdapter(adapter);
		viewPager.setOnPageChangeListener(this);
		topNavRg = (RadioGroup) findViewById(R.id.rg_top_nav);
		topNavRg.setOnCheckedChangeListener(this);
		topNavRg.check(R.id.rb_my_music);
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
		onMusicChange(null, null);
	}
	
	public void setCurrentItem(int item) {
		viewPager.setCurrentItem(item);
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
					frag.updateUI(frag.manager.isPlaying());
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
		int checkedId = R.id.rb_my_music;
		switch (item) {
		case 0:
			break;
		case 1:
			checkedId = R.id.rb_tf_card_music;
			if (!tf) {
				checkedId = R.id.rb_cloud_music;
			}
			break;
		case 2:
			checkedId = R.id.rb_cloud_music;
			break;
		default:
			break;
		}
		topNavRg.check(checkedId);
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int item = 0;
		switch (checkedId) {
		case R.id.rb_my_music:
			break;
		case R.id.rb_tf_card_music:
			item = 1;
			break;
		case R.id.rb_cloud_music:
			item = 2;
			if (!tf) {
				item = 1;
			}
			break;
		default:
			break;
		}
		viewPager.setCurrentItem(item);
	}

}
