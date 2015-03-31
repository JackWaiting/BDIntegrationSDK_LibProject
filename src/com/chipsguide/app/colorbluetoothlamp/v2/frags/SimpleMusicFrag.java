package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.content.Intent;
import android.util.Log;
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
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.OnDeviceMusicManagerReadyListener;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.MusicCallback;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.view.CustomDialog;
import com.chipsguide.app.colorbluetoothlamp.v2.view.Footer4List;
import com.chipsguide.lib.bluetooth.interfaces.templets.IBluetoothDeviceMusicManager;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;

public abstract class SimpleMusicFrag extends BaseFragment implements
		OnItemClickListener, SimpleMusicPlayListener {
	protected IMusicListAdapter adapter;
	private PlayerManager playerManager;
	private int prePosition = -1;
	private boolean userClick;
	private ListView musicListLv;
	private BluetoothDeviceManagerProxy bluzDeviceManProxy;
	private Footer4List footer;
	protected String filterTag, formatStr, formatStrSuccess;
	private OnItemSelectedListener mlistener;
	private boolean loadFinished;

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
			if (getFilter(music).equals(filterTag)) {
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
		bluzDeviceManProxy = BluetoothDeviceManagerProxy.getInstance(context);
		playerManager = PlayerManager.getInstance(context);
	}

	@Override
	protected void initView() {
		footer = new Footer4List(getActivity());
		musicListLv = (ListView) findViewById(R.id.lv_musiclist);
		if (getPlayType() == PlayType.Bluz) {
			musicListLv.setOnScrollListener(scrollListener);
		}
		musicListLv.addFooterView(footer);
		musicListLv.setOnItemClickListener(this);
		adapter = getAdapter();
		if (adapter == null) {
			adapter = new SimpleMusicListAdapter(getActivity());
		}
		musicListLv.setAdapter(adapter);
	}

	@Override
	protected void initData() {
		if (getPlayType() == PlayType.Local) {
			playerManager.loadLocalMusic(new MusicCallback() {
				@Override
				public void onLoadCancel(int loaded, int total,
						List<Music> loadedMusics) {
				}

				@Override
				public void onLoadStart() {
				}

				@Override
				public void onLoading(int loaded, int total) {
				}

				@Override
				public void onLoadMusic(List<Music> musics, int prePosition) {
					adapter.setMusicList(musics);
					adapter.setSelected(prePosition);
					musicListLv.removeFooterView(footer);
					selectedByTag(musics, filterTag);
				}
			}, false);
		} else if (getPlayType() == PlayType.Bluz) {
		}
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

	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && bluzDeviceManProxy != null
				&& getPlayType() == PlayType.Bluz) {
			if (bluzDeviceManProxy.isInMusicManagerMode() && loadFinished) {
				return;
			}
			adapter.setMusicList(new ArrayList<Music>());
			//showLoadingMusicDialog();
			bluzDeviceManProxy
					.setOnBluetoothDeviceMuisicReadyListener(deviceMusicManagerReadyListener);
			bluzDeviceManProxy
					.getBluetoothDeviceMusicManager(BluetoothDeviceManager.Mode.CARD);
		} else if (!isVisibleToUser && musicListLv != null) {
			//musicListLv.removeFooterView(footer);
		}
	}

	private CustomDialog dialog;

	private void showLoadingMusicDialog() {
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
		dialog = new CustomDialog(getActivity(), R.style.Dialog_Fullscreen_dim);
		dialog.setOnDismissListener(new OnDismissListener() {
			@Override
			public void onDismiss(DialogInterface dialog) {
			}
		});
		String str = String.format(formatStr, 0);
		dialog.setMessage(str);
		dialog.show();
	}

	
	private IBluetoothDeviceMusicManager bluzMusicmanager;
	private OnDeviceMusicManagerReadyListener deviceMusicManagerReadyListener = new OnDeviceMusicManagerReadyListener() {
		@Override
		public void onMusicManagerReadyFailed(int mode) {
			Log.e("SimpleMusicFrag", "onMusicManagerReadyFailed");
		}

		@Override
		public void onMusicManagerReady(IBluetoothDeviceMusicManager manager,
				int mode) {
			bluzMusicmanager = manager;
			//loadAllBlzMusic(manager);
			loadPageBlzMusic();
		}
	};
	/**
	 * 加载全部
	 * @param manager
	 */
	private void loadAllBlzMusic(IBluetoothDeviceMusicManager manager) {
		playerManager.loadBluetoothDeviceMusic(manager, new MusicCallback() {

			public void onLoadStart() {
			}

			@Override
			public void onLoading(int loaded, int total) {
				String str = String.format(formatStr, (int) ((float) loaded
						/ total * 100));
				dialog.updateMessage(str);
			}

			@Override
			public void onLoadMusic(List<Music> musics, int prePosition) {
				adapter.setMusicList(musics);
				selectedByTag(musics, filterTag);
				loadFinished = true;
				int size = musics.size();
				String str = String.format(formatStrSuccess, size, size);
				dialog.updateMessage(str);
				dialog.dismiss(true, 3000);
			}

			public void onLoadCancel(int loaded, int total,
					java.util.List<Music> loadedMusics) {

			}

		}, getActivity());
	}

	private List<Music> bluzMusics = new ArrayList<Music>();
	private int startPosi;
	private static final int EXPECT_SIZE = 10;
	/**
	 * 分页加载
	 */
	private void loadPageBlzMusic() {
		playerManager.loadBluetoothDeviceMusic(startPosi, EXPECT_SIZE, bluzMusicmanager, new MusicCallback() {

			public void onLoadStart() {
			}

			@Override
			public void onLoading(int loaded, int total) {
				
			}

			@Override
			public void onLoadMusic(List<Music> musics, int prePosition) {
				loadFinished = true;
				bluzMusics.addAll(musics);
				adapter.setMusicList(bluzMusics);
				adapter.setSelected(prePosition);
				startPosi = bluzMusics.size();
				if (startPosi >= bluzMusicmanager.getSongSize()) {
					musicListLv.removeFooterView(footer);
				}
				selectedByTag(musics, filterTag);
			}

			public void onLoadCancel(int loaded, int total,
					java.util.List<Music> loadedMusics) {
			}

		}, getActivity());
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
					loadPageBlzMusic();
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

	@Override
	public void onDestroy() {
		super.onDestroy();
		if (dialog != null) {
			dialog.dismiss();
			dialog = null;
		}
	}

}
