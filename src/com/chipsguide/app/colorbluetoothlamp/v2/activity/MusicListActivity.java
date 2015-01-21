package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import android.os.Bundle;
import android.text.TextUtils;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Album;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NetMusicListFrag;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TitleView;
/**
 * 网络音乐列表Activity
 * @author chiemy
 *
 */
public class MusicListActivity extends BaseActivity {
	public static final String EXTRA_ALBUM = "album";
	private Album mAlbum;
	private String title;
	
	@Override
	public int getLayoutId() {
		return R.layout.activity_music_list_layout;
	}

	@Override
	public void initBase() {
		mAlbum = (Album) getIntent().getSerializableExtra(EXTRA_ALBUM);
		title = mAlbum.getName();
		if(TextUtils.isEmpty(title)){
			title = mAlbum.getName_en();
		}
	}

	@Override
	public void initUI() {
		TitleView titleView = (TitleView) findViewById(R.id.titleView);
		titleView.setTitleText(title);
		titleView.setOnClickListener(this);
		
		NetMusicListFrag musicListFrag = new NetMusicListFrag();
		Bundle bundle = new Bundle();
		bundle.putString(NetMusicListFrag.EXTRA_QUERY_TYPE, NetMusicListFrag.QUERY_TYPE_BY_ALBUM);
		bundle.putSerializable(NetMusicListFrag.EXTRA_DATA, mAlbum);
		musicListFrag.setArguments(bundle);
		getSupportFragmentManager().beginTransaction().replace(R.id.content_layout, musicListFrag).commit();
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}

	
	
}
