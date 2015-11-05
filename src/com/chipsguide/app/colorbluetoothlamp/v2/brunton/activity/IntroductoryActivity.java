package com.chipsguide.app.colorbluetoothlamp.v2.brunton.activity;

import java.util.ArrayList;
import java.util.List;

import android.annotation.SuppressLint;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.utils.PreferenceUtil;

public class IntroductoryActivity extends BaseActivity{
	private List<View> viewList = new ArrayList<View>();
	private int [] images = {R.drawable.image_bg_p1, R.drawable.image_bg_p2, R.drawable.image_bg_p3, R.drawable.image_bg_p4};
	
	@Override
	public void onClick(View v) {
		if(v.getId() == R.id.btn_immediate){
			PreferenceUtil.getIntance(this).setFirstLaunch(false);
			startActivity(MainActivity.class);
			finish();
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_introductory;
	}

	@Override
	public void initBase() {
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		CustomApplication.addActivity(this);
	}

	@Override
	public void initUI() {
		ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager_introductory);
		initPagerView();
		viewPager.setAdapter(new IntroductoryAdapter());
	}
	
	@SuppressLint("InflateParams") 
	private void initPagerView() {
		int size = images.length;
		for(int i = 0 ; i < size ; i++){
			View page = LayoutInflater.from(this).inflate(R.layout.layout_introductory_item, null);
			viewList.add(page);
			ImageView iv = (ImageView) page.findViewById(R.id.iv_introductory);
			iv.setImageResource(images[i]);
			if(i == (size - 1)){
				View view = page.findViewById(R.id.btn_immediate);
				view.setOnClickListener(this);
				view.setVisibility(View.VISIBLE);
			}
		}
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}

	private class IntroductoryAdapter extends PagerAdapter{

		@Override
		public int getCount() {
			return viewList.size();
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}
		
		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			container.removeView((View) object);
		}
		
		@Override
		public Object instantiateItem(ViewGroup container, int position) {
			container.addView(viewList.get(position));
			return viewList.get(position);
		}
	}
	
	@Override
	public void updateConnectState()
	{
	}

	@Override
	public void updateAlarm(int state)
	{
		if(CustomApplication.getActivity() == this)
		{
			if(state == 1)
			{
				createAlarmToast();
			}else if(state == 0)
			{
				dismissAlarmDialog();
			}else
			{
				dismissAlarmDialog();
			}
		}
	}
}
