package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

//右布局的Fragment
public class MainFragment extends BaseFragment implements
		OnCheckedChangeListener, OnPageChangeListener {
	private ViewPager viewPager;
	private RadioGroup bottomNavRg;
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private String[] pageTitle;
	private OnMainPageChangeListener onMainPageChangeListener;

	public interface OnMainPageChangeListener {
		void onMainPageChanged(int position, String title);
	}

	@Override
	public void onAttach(Activity activity)
	{
		super.onAttach(activity);
		if (activity instanceof OnMainPageChangeListener)
		{
			onMainPageChangeListener = (OnMainPageChangeListener) activity;
		}
	}

	@Override
	protected void initBase()
	{
		pageTitle = getActivity().getResources().getStringArray(
				R.array.main_page_titles);
		fragments.add(new ColorLampFrag());
		fragments.add(new WrapMusicFrag());
		fragments.add(new ShakeFrag());
		registBroadcase();
	}

	@Override
	protected int getLayoutId()
	{
		return R.layout.frag_main;
	}

	@Override
	protected void initView()
	{
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setOnPageChangeListener(this);
		viewPager.setOffscreenPageLimit(2);
		viewPager.setAdapter(new MyPagerAdapter(getChildFragmentManager()));
		bottomNavRg = (RadioGroup) findViewById(R.id.rg_nav_bottom);
		bottomNavRg.setOnCheckedChangeListener(this);
		bottomNavRg.check(R.id.rb_light);
	}

	@Override
	protected void initData()
	{
		setViewPage(CustomApplication.getMode());
	}

	public void setViewPage(int mode)
	{
		switch (mode)
		{
		case BluetoothDeviceManager.Mode.CARD:
		case BluetoothDeviceManager.Mode.USB:
		case BluetoothDeviceManager.Mode.A2DP:
			bottomNavRg.check(R.id.rb_music);
			break;
		case BluetoothDeviceManager.Mode.LINE_IN:
			break;
		}
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm)
		{
			super(fm);
		}

		@Override
		public Fragment getItem(int position)
		{
			return fragments.get(position);
		}

		@Override
		public int getCount()
		{
			return fragments.size();
		}

	}

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId)
	{
		int item = 0;
		switch (checkedId)
		{
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
	public void onPageScrollStateChanged(int arg0)
	{
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2)
	{
	}

	@Override
	public void onPageSelected(int position)
	{
		if (onMainPageChangeListener != null)
		{
			onMainPageChangeListener.onMainPageChanged(position,
					pageTitle[position]);
		}
	}

	private boolean register;

	private void registBroadcase()
	{
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDeviceManagerProxy.ACTION_TF_CARD_PLUG_CHANGED);
		filter.addAction(BluetoothDeviceManagerProxy.ACTION_MODE_CHANGE);
		getActivity().registerReceiver(bluzBroadcaseReceiver, filter);
		register = true;
	}

	private BroadcastReceiver bluzBroadcaseReceiver = new BroadcastReceiver()
	{
		@Override
		public void onReceive(Context context, Intent intent)
		{
			String action = intent.getAction();
			if (BluetoothDeviceManagerProxy.ACTION_MODE_CHANGE.equals(action))
			{
				int newMode = intent.getIntExtra(
						BluetoothDeviceManagerProxy.EXTRA_NEW_MODE, -1);
				if (newMode == BluetoothDeviceManager.Mode.CARD || newMode == BluetoothDeviceManager.Mode.A2DP)
				{
					viewPager.setCurrentItem(1, false);
					bottomNavRg.check(R.id.rb_music);
				} else if (newMode == BluetoothDeviceManager.Mode.LINE_IN)
				{
					viewPager.setCurrentItem(0, false);
					bottomNavRg.check(R.id.rb_light);
				}
			}
		}
	};

	@Override
	public void onDestroy()
	{
		super.onDestroy();
		if (register)
		{
			getActivity().unregisterReceiver(bluzBroadcaseReceiver);
		}
	}

}
