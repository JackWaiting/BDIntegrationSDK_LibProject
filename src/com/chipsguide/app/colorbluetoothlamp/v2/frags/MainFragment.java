package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class MainFragment extends BaseFragment implements OnCheckedChangeListener, OnPageChangeListener{
	private ViewPager viewPager;
	private RadioGroup bottomNavRg;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private String [] pageTitle;
	private OnMainPageChangeListener onMainPageChangeListener;
	
	public interface OnMainPageChangeListener{
		void onMainPageChanged(int position, String title);
	}
	
	@Override
	public void onAttach(Activity activity) {
		super.onAttach(activity);
		if(activity instanceof OnMainPageChangeListener){
			onMainPageChangeListener = (OnMainPageChangeListener) activity;
		}
	}
	
	@Override
	protected void initBase() {
		pageTitle = getActivity().getResources().getStringArray(R.array.main_page_titles);
		fragments.add(new ColorLampFrag());
		fragments.add(new MusicFrag());
		fragments.add(new ShakeFrag());
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_main;
	}

	@Override
	protected void initView() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOnPageChangeListener(this);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
		bottomNavRg = (RadioGroup) findViewById(R.id.rg_nav_bottom);
		bottomNavRg.setOnCheckedChangeListener(this);
		bottomNavRg.check(R.id.rb_light);
	}

	@Override
	protected void initData() {
	}
	
	private class MyPagerAdapter extends FragmentPagerAdapter{

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
		}

		@Override
		public Fragment getItem(int position) {
			return fragments.get(position);
		}

		@Override
		public int getCount() {
			return fragments.size();
		}
		
	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		int item = 0;
		switch(checkedId){
		case R.id.rb_light:
			break;
		case R.id.rb_music:
			item = 1;
			break;
		case R.id.rb_shake:
			item = 2;
			break;
		}
		viewPager.setCurrentItem(item, false);
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int position) {
		if(onMainPageChangeListener != null){
			onMainPageChangeListener.onMainPageChanged(position, pageTitle[position]);
		}
	}

}
