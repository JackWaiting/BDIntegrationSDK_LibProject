package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.lang.ref.WeakReference;

import android.os.Bundle;
import android.text.TextUtils;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Album;
import com.chipsguide.app.colorbluetoothlamp.v2.frags.NetMusicListFrag;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
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
	
	private PlayerManager manager;
	private NetMusicListFrag musicListFrag;
	
	@Override
	public int getLayoutId() {
		return R.layout.activity_music_list_layout;
	}

	@Override
	public void initBase() {
		manager = PlayerManager.getInstance(getApplicationContext());
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
		
		musicListFrag = new NetMusicListFrag();
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

	private void updateUI(boolean playing) {
		onMusicChange(null, null);
	}
	
	private void onMusicChange(PlayType oldType, PlayType newType) {
		if(musicListFrag instanceof SimpleMusicPlayListener){
			((SimpleMusicPlayListener) musicListFrag).onMusicChange();
		}
	}
	
	@Override
	protected void onResume() {
		super.onResume();
		hasUpdate = false;
		manager.setPlayListener(new MyPlayListener(this), PlayType.Net, false);
	}
	
	private boolean hasUpdate;
	private static class MyPlayListener extends PlayListener{
		private WeakReference<MusicListActivity> ref;
		private MyPlayListener(MusicListActivity act){
			ref = new WeakReference<MusicListActivity>(act);
		}

		@Override
		public void onMusicStart(String musicSymbol) {
			MusicListActivity act = ref.get();
			if(act != null){
				act.updateUI(true);
			}
		}
		
		@Override
		public void onMusicProgress(String musicSymbol, long duration,
				long currentDuration, int percent) {
			MusicListActivity act = ref.get();
			if(act != null){
				if(!act.hasUpdate){
					act.hasUpdate = true;
					act.updateUI(act.manager.isPlaying());
				}
			}
		}
		
		@Override
		public void onMusicPause(String musicSymbol) {
			MusicListActivity act = ref.get();
			if(act != null){
				act.updateUI(false);
			}
		}
		
		@Override
		public void onMusicError(String musicSymbol, int what ,int extra) {
		}
		
		@Override
		public void onMusicChange(String musicSymbol) {
		}
		
		@Override
		public void onMusicBuffering(String musicSymbol, int percent) {
		}
		
		@Override
		public void onPlayTypeChange(PlayType oldType, PlayType newType) {
			MusicListActivity act = ref.get();
			if(act != null){
				act.onMusicChange(oldType, newType);
			}
		}
	}
	
}
