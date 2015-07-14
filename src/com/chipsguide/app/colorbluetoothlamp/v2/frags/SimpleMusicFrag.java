package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.List;

import android.content.Context;
import android.content.Intent;
import android.view.View;
import android.widget.AbsListView;
import android.widget.AbsListView.OnScrollListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.activity.MusicPlayerActivity;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.IMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.SelectMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.SimpleMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.MusicCallback;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.view.Footer4List;

public abstract class SimpleMusicFrag extends BaseFragment implements
		OnItemClickListener, SimpleMusicPlayListener ,MusicCallback{
	protected IMusicListAdapter adapter;
	protected PlayerManager playerManager;
	private int prePosition = -1;
	private boolean userClick;
	protected ListView musicListLv;
	private Footer4List footer;
	protected String filterTag, formatStr, formatStrSuccess;
	private OnItemSelectedListener mlistener;

	public interface OnItemSelectedListener {
		void onItemSelected(SimpleMusicFrag frag, Music music, int position);
	}

	/**
	 * 获取与此Fragment对应的播放类型
	 * 
	 * @return
	 */
	public abstract PlayType getPlayType();

	public IMusicListAdapter getAdapter() {
		return adapter;
	}

	public void setAdapter(IMusicListAdapter adapter) {
		this.adapter = adapter;
	}

	public void setOnItemSelectedListener(OnItemSelectedListener listener) {
		mlistener = listener;
	}

	/**
	 * 根据此标识找到对应的歌曲
	 * 
	 * @param tag
	 */
	public void setFilterTag(String tag) {
		filterTag = tag;
	}

	/**
	 * 过滤条件
	 */
	public String getFilter(Music music) {
		return music.getName();
	}

	public void selectedByTag(List<Music> musics, String tag) {
		if (tag == null) {
			return;
		}
		adapter.setSelected(-1);
		int size = musics.size();
		for (int i = 0; i < size; i++) {
			Music music = musics.get(i);
			if (getFilter(music).equals(tag)) {
				adapter.setSelected(i);
			}
		}
	}

	@Override
	protected void initBase() {
		formatStr = getResources().getString(R.string.loading_tf_music);
		formatStrSuccess = getResources().getString(
				R.string.loading_tf_music_success);
		Context context = getActivity().getApplicationContext();
		playerManager = PlayerManager.getInstance(context);
	}

	@Override
	protected void initView() {
		footer = new Footer4List(getActivity());
		musicListLv = (ListView) findViewById(R.id.lv_musiclist);
		if (getPlayType() == PlayType.Bluz) {
			musicListLv.setOnScrollListener(scrollListener);
		}
		musicListLv.setOnItemClickListener(this);
		adapter = getAdapter();
		if (adapter == null) {
			adapter = new SimpleMusicListAdapter(getActivity());
		}
		musicListLv.addFooterView(footer);
		musicListLv.setAdapter(adapter);
		musicListLv.removeFooterView(footer);
	}
	
	private boolean add;
	protected void removeFooterView() {
		add = false;
		musicListLv.removeFooterView(footer);
	}
	
	protected void addFooterView() {
		if(!add){
			add = true;
			if(musicListLv != null)
			{
				musicListLv.addFooterView(footer);
			}
		}
	}

	@Override
	protected void initData() {
		if (getPlayType() == PlayType.Local) {
			onLoadPlayList();
		} 
	}
	/**
	 * 在此方法内进行歌曲列表的加载
	 */
	public abstract void onLoadPlayList();
	
	@Override
	public void onLoadMusic(List<Music> musics, int prePosition) {
		adapter.setMusicList(musics);
		adapter.setSelected(prePosition);
		removeFooterView();
		selectedByTag(musics, filterTag);
	}

	@Override
	public void onLoading(int loaded, int total) {
	}

	@Override
	public void onLoadStart() {
	}

	@Override
	public void onLoadCancel(int loaded, int total, List<Music> loadedMusics) {
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		userClick = true;
		if (position == prePosition) {
			// playerManager.toggle();
		} else {
			prePosition = position;
			adapter.setSelected(position);
		}
		if (mlistener != null) {
			mlistener.onItemSelected(SimpleMusicFrag.this, adapter
					.getMusicList().get(position), position);
		}
		if (adapter instanceof SelectMusicListAdapter) {
			return;
		}
		playerManager.setMusicList(adapter.getMusicList(), position,
				getPlayType());
		Intent intent = new Intent(getActivity(), MusicPlayerActivity.class);
		intent.putExtra(MusicPlayerActivity.EXTRA_MODE_TO_BE,
				getPlayType() == PlayType.Local ? 0 : 1);
		startActivity(MusicPlayerActivity.class);
	}

	private int lastItem;
	/**
	 * ListView滚动监听，实现加载更多
	 */
	private OnScrollListener scrollListener = new OnScrollListener() {
		@Override
		public void onScrollStateChanged(AbsListView view, int scrollState) {
			if (scrollState == OnScrollListener.SCROLL_STATE_IDLE) {
				if (OnScrollListener.SCROLL_STATE_IDLE == scrollState
						&& adapter != null
						&& lastItem == adapter.getCount() + 1) {
					onLoadPlayList();
				}
			}
		}

		@Override
		public void onScroll(AbsListView view, int firstVisibleItem,
				int visibleItemCount, int totalItemCount) {
			lastItem = firstVisibleItem + visibleItemCount;
		}
	};
	
	@Override
	public void onMusicProgress(long duration, long currentDuration, int percent) {

	}

	@Override
	public void onMusicPlayStateChange(boolean playing) {
		if (adapter == null) {
			adapter = getAdapter();
		}
		adapter.setPlaying(playing);
	}

	@Override
	public void onMusicChange() {
		if (adapter == null) {
			return;
		}
		if (getPlayType() != PlayerManager.getPlayType()) {
			prePosition = -1;
			adapter.setSelected(prePosition);
		} else {
			prePosition = playerManager.getCurrentPosition();
			adapter.setSelected(prePosition);
			if (prePosition < adapter.getMusicList().size() && !userClick && musicListLv != null) {
				musicListLv.post(new Runnable() {
					@Override
					public void run() {
						musicListLv.setSelection(prePosition);
					}
				});
			}
			userClick = false;
		}
	}

	@Override
	public void onResume() {
		super.onResume();
		if (getPlayType() != PlayerManager.getPlayType()) {
			prePosition = -1;
			adapter.setSelected(prePosition);
		}
		adapter.setPlaying(playerManager.isPlaying());
	}

}
