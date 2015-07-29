package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.util.ArrayList;
import java.util.List;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentPagerAdapter;
import android.support.v4.view.ViewPager;
import android.support.v4.view.ViewPager.OnPageChangeListener;
import android.text.Editable;
import android.text.TextUtils;
import android.text.TextWatcher;
import android.view.KeyEvent;
import android.view.inputmethod.EditorInfo;
import android.widget.EditText;
import android.widget.RadioGroup;
import android.widget.RadioGroup.OnCheckedChangeListener;
import android.widget.TextView;
import android.widget.TextView.OnEditorActionListener;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.application.CustomApplication;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.AlbumListFragment;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NetMusicListFrag;

public class SearchActivity extends BaseActivity implements
		OnPageChangeListener, OnCheckedChangeListener {
	private List<Fragment> fragments = new ArrayList<Fragment>();
	private RadioGroup navRg;
	private ViewPager viewPager;
	private EditText searchEt;

	public interface OnSearchListener {
		void onSearchTextChanged(String keyWords);

		void onStartSearch(String keyWords);
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_search;
	}

	@Override
	public void initBase() {
		fragments.add(new AlbumListFragment());
		fragments.add(new NetMusicListFrag());
	}

	@Override
	public void initUI() {
		viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new MyPagerAdapter(getSupportFragmentManager()));
		viewPager.setOnPageChangeListener(this);
		navRg = (RadioGroup) findViewById(R.id.rg_top_nav);
		navRg.check(R.id.rb_album);
		navRg.setOnCheckedChangeListener(this);
		searchEt = (EditText) findViewById(R.id.et_search);
		searchEt.addTextChangedListener(new TextWatcher() {
			@Override
			public void onTextChanged(CharSequence s, int start, int before, int count) {
				int size = fragments.size();
				for(int i = 0 ; i < size ; i++){
					Fragment frag = fragments.get(i);
					if(frag instanceof OnSearchListener){
						((OnSearchListener) frag).onSearchTextChanged(s.toString());
					}
				}
			}
			@Override
			public void beforeTextChanged(CharSequence s, int start, int count,
					int after) {
			}
			@Override
			public void afterTextChanged(Editable s) {
			}
		});
		searchEt.setOnEditorActionListener(new OnEditorActionListener() {
			@Override
			public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
				if(actionId == EditorInfo.IME_ACTION_SEARCH){
					if(TextUtils.isEmpty(searchEt.getText().toString().trim())){
						showToast(R.string.keywords_cannot_be_null);
						return false;
					}
					hideInputMethod(v);
					int size = fragments.size();
					for(int i = 0 ; i < size ; i++){
						Fragment frag = fragments.get(i);
						if(frag instanceof OnSearchListener){
							((OnSearchListener) frag).onStartSearch(searchEt.getText().toString().trim());
						}
					}
				}
				return false;
			}
		});
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}
	
	@Override
	protected void onResume()
	{
		super.onResume();
		CustomApplication.addActivity(this);
	}

	private class MyPagerAdapter extends FragmentPagerAdapter {

		public MyPagerAdapter(FragmentManager fm) {
			super(fm);
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

	@Override
	public void onCheckedChanged(RadioGroup group, int checkedId) {
		switch (checkedId) {
		case R.id.rb_album:
			viewPager.setCurrentItem(0);
			break;
		case R.id.rb_music:
			viewPager.setCurrentItem(1);
			break;
		default:
			break;
		}
	}

	@Override
	public void onPageScrollStateChanged(int arg0) {
	}

	@Override
	public void onPageScrolled(int arg0, float arg1, int arg2) {
	}

	@Override
	public void onPageSelected(int arg0) {
		navRg.check(arg0 == 0 ? R.id.rb_album : R.id.rb_music);
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
