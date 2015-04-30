package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.content.Intent;
import android.os.Bundle;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.AlbumListFragment;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TitleView;

public class AlbumListActivity extends BaseActivity {
	public static final String EXTRA_ALBUM_NAME = "album_name";
	public static final String EXTRA_ALBUM_CODE = "album_code";
	private String albumName;
	private String albumCode;
	
	@Override
	public void initBase() {
		Intent intent = getIntent();
		albumCode = intent.getStringExtra(EXTRA_ALBUM_CODE);
		albumName = intent.getStringExtra(EXTRA_ALBUM_NAME);
	}

	@Override
	public void initUI() {
		TitleView titleView = (TitleView) findViewById(R.id.titleView);
		titleView.setTitleText(albumName);
		titleView.setOnClickListener(this);

		AlbumListFragment fragment = new AlbumListFragment();
		Bundle bundle = new Bundle();
		bundle.putString(AlbumListFragment.QUERY_TYPE, AlbumListFragment.QUERY_TYPE_ALBUM_CODE);
		bundle.putString(AlbumListFragment.EXTRA_DATA, albumCode);
		fragment.setArguments(bundle);
		
		getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, fragment).commit();
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_album_list_layout;
	}

	@Override
	public void updateVolume(int volume)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateConnectState(boolean isConnect)
	{
		// TODO Auto-generated method stub
		
	}
	
}
