package com.chipsguide.app.colorbluetoothlamp.v2.media;

import java.util.ArrayList;
import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.media.MediaPlayer;
import android.os.Handler;
import android.util.Log;
import android.widget.Toast;

import com.actions.ibluz.manager.BluzManagerData.PlayState;
import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.RecentPlayMusic;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;
import com.chipsguide.lib.bluetooth.entities.BluetoothDeviceMusicSongEntity;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceMusicLoopModeChangedListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceMusicPlayStateChangedListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceMusicSongChangedListener;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceMusicSongListListener;
import com.chipsguide.lib.bluetooth.interfaces.templets.IBluetoothDeviceMusicManager;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceCardMusicManager;
import com.platomix.platomixplayerlib.api.PlaybackMode;
import com.platomix.platomixplayerlib.api.Playlist;
import com.platomix.platomixplayerlib.core.AudioHelper;
import com.platomix.platomixplayerlib.core.PlayerListener;
import com.platomix.platomixplayerlib.core.local.LoadMusicCallback;
import com.platomix.platomixplayerlib.core.local.LocalPlayer;
import com.platomix.platomixplayerlib.core.local.PlaylistEntity;

/**
 * 播放器管理类
 * 
 * @author chiemy
 * 
 */
public class PlayerManager {
	public static final String TAG = "PlayerManager";
	private static PlayerManager instance;
	private List<Music> mMusicList;
	private int currentPosition = -1;
	private static PlayType mType;
	private PlayListener mPlayListener;
	private Context mContext;
	private LocalPlayer player;
	private IBluetoothDeviceMusicManager deviceMusicManager;

	private PreferenceUtil preferenceUtil;
	private int preLocalMusicListPosition; //上一次本地歌曲列表的位置
	private boolean hasRecover; //已经从上一次的位置播放过了
	/**
	 * 播放的是最近播放的歌曲
	 */
	public static boolean isPlayRecentMusic;

	private AudioHelper audioHelper;
	
	private PlayerManager(Context context) {
		mContext = context;
		preferenceUtil = PreferenceUtil.getIntance(mContext);
		preLocalMusicListPosition = preferenceUtil.getPhoneMusicPosition();
		audioHelper = new AudioHelper(mContext, null);
	}

	public static PlayerManager getInstance(Context context) {
		if (instance == null) {
			instance = new PlayerManager(context);
		}
		return instance;
	}
	
	/**
     * 请求音乐焦点
     * @return
     */
    private boolean requestFocus(Activity act) {
    	return audioHelper.requestFocus();
    }
	
	/**
	 * 设置歌曲列表及播放位置
	 * 
	 * @param musicList
	 *            音乐列表
	 * @param position
	 *            开始播放的位置，如果小于0则不自动播放音乐
	 * @param type
	 *            如果是本地音乐或网络音乐，则传入{@link PlayType#Local}或者{@link PlayType#Net}<br>
	 *            如果是蓝牙设备音乐则为{@link PlayType#Bluz}<br>
	 *            如果传入null，则使用上次的类型播放
	 */
	public void setMusicList(List<Music> musicList, int position, PlayType type) {
		if (musicList == null || musicList.isEmpty()) {
			return;
		}
		isPlayRecentMusic = false;
		// change = checkMusicListIfIsChanged(musicList, type);
		mMusicList = musicList;
		currentPosition = Math.max(0, position);
		if (type == null) {
			playWithCurrentEngine(true);
		} else {
			setPlayType(type);
			selectePlayEngine(true);
		}
	}

	/**
	 * 加载最近播放的歌曲
	 * 
	 * @return 是否加载成功，当前播放类型不为空不成功，最近播放列表为空不成功。
	 */
	public boolean loadRecentPlay() {
		if (mType == null) {
			List<RecentPlayMusic> recents = new ArrayList<RecentPlayMusic>();
//			recents = RecentPlayDao.getDao(mContext)
//					.selectAll();
			if (recents != null && !recents.isEmpty()) {
				List<Music> musics = new ArrayList<Music>();
				RecentPlayMusic recent = recents.get(0);
				musics.add(recent.getMusic());
				PlayType type = PlayType.Local;
				if (recent.getPlay_type() == PlayType.Net.value) {
					type = PlayType.Net;
				}
				mMusicList = musics;
				currentPosition = 0;
				setPlayType(type);
				selectePlayEngine(false);
				isPlayRecentMusic = true;
				return true;
			}
		}
		return false;
	}

	/**
	 * 音乐列表是否发生改变。如果新的播放类型和原播放类型不同，则认为列表改变；<br>
	 * 如果播放类型相同，但新列表的歌曲个数小于原列表的个数，则认为发生改变;<br>
	 * 如果上述条件都不满足，比较音乐列表的第一首和最后一首歌（此时，原列表的大小肯定大于新列表）的id，<br>
	 * 有一个不同，则认为改变
	 * 
	 * @param newList
	 * @param type
	 * @return
	 */
	private boolean checkMusicListIfIsChanged(List<Music> newList, PlayType type) {
		boolean change = false;
		if (newList == mMusicList && newList != null) {
			return false;
		}
		if (mType != null && mType != type) {
			change = true;
		} else {
			if (mMusicList != null) {
				if (newList.size() < mMusicList.size()) {
					change = true;
				} else {
					int num = Math.min(newList.size(), mMusicList.size());
					if (num > 0) {
						Music newFirst = newList.get(0);
						Music oldFirst = mMusicList.get(0);
						if (newFirst.getId().equals(oldFirst.getId())) {
							// 如果第一首相同，比较最后一首
							Music newLast = newList.get(num - 1);
							Music oldLast = mMusicList.get(num - 1);
							change = !newLast.getId().equals(oldLast.getId());
						}
					}
				}
			} else {
				change = true;
			}
		}
		return change;
	}

	/**
	 * 设置播放类型
	 * 
	 * @see PlayType
	 * @param type
	 */
	private void setPlayType(PlayType type) {
		if(mType == PlayType.Local && player != null){
			hasRecover = false;
			preferenceUtil.savePhoneMusicCurrentDuration(player.getCurrentPlayPercent());
		}
		if(mType != type && mPlayListener != null){
			mPlayListener.onPlayTypeChange(mType, type);
		}
		mType = type;
	}

	/**
	 * 根据播放类型选择播放引擎<br>
	 * 如果播放类型为{@link PlayType#Local}或{@link PlayType#Net}则使用LocalPlayer<br>
	 * 否则使用BluetoothDeviceCardMusicManager播放卡音乐
	 */
	private void selectePlayEngine(boolean play) {
		if (mType == PlayType.Local || mType == PlayType.Net) {
			if (deviceMusicManager != null) {
				deviceMusicManager
						.setOnBluetoothDeviceMusicLoopModeChangedListener(null);
				deviceMusicManager
						.setOnBluetoothDeviceMusicPlayStateChangedListener(null);
				deviceMusicManager
						.setOnBluetoothDeviceMusicSongChangedListener(null);
				deviceMusicManager = null;
			}
			if(play){
				BluetoothDeviceManagerProxy.changeToA2DPMode();
			}
			handler.removeCallbacks(progressRunnable);
			if (player == null) {
				player = LocalPlayer.getInstance(mContext);
				player.setListener(localPlayerListener);
			}
		} else {
			if (player != null) {
				player.stop();
				player = null;
			}
		}
		playWithCurrentEngine(play);
	}

	/**
	 * 开始播放
	 */
	private void playWithCurrentEngine(boolean play) {
		hasplay = play;
		if (player != null) {
			Playlist playList = initPlaylist();
			player.setPlaylist(playList);
			if (play) {
				player.skipTo(currentPosition);
			}else if(mPlayListener != null && currentPosition >= 0){
				mPlayListener.onMusicChange(mMusicList.get(currentPosition).getPath());
			}
			int m = preferenceUtil.getPlayMode();
			PlaybackMode mode = PlayUtil.getModeWithIndex(m);
			changePlaymode(mode);
		} else if (deviceMusicManager != null) {
			if (play) {
				deviceMusicManager.select(currentPosition + 1);
			}
		} else {
			Log.d(TAG,
					"Warning !!! Both players are null!! Have you called prepareBluz()??");
		}
	}

	/**
	 * 移除歌曲，适用于本地歌曲
	 * 
	 * @param musicList
	 *            移除歌曲的列表
	 * @param type
	 *            移除歌曲列表的类型
	 * @param id
	 *            移除歌曲的id
	 * @param position
	 *            移除歌曲的位置
	 */
	public void remove(List<Music> musicList, PlayType type, String id,
			int position) {
		// 判断与当前的播放列表是否为同一个
		boolean same = !checkMusicListIfIsChanged(musicList, type);
		Log.d(TAG, "remove. is the same list ? " + same);
		if (same) {
			// 在列表中查找该歌曲
			Music music = findMusicById(id);
			Music currentMusic = getCurrentMusic();
			if (music != null) {
				if (musicList != mMusicList) {
					musicList.remove(position);
					mMusicList.remove(music);
				} else {
					musicList.remove(position);
				}
				if (player != null) {
					Playlist playList = initPlaylist();
					player.setPlaylist(playList);
				}

				if (currentMusic != null) {
					if (currentMusic.getId().equals(music.getId())) {
						if (mMusicList.size() == 0) {
							destoryAll();
						} else {
							skipTo(position);
						}
					}
				}
			}
		} else {
			musicList.remove(position);
		}
	}

	/**
	 * 获取音乐列表
	 * 
	 * @return
	 */
	public List<Music> getMusicList() {
		return mMusicList;
	}

	public interface MusicCallback {
		void onLoadMusic(List<Music> musics, int prePosition);
	}

	/**
	 * 加载本地歌曲列表
	 * @param callback 歌曲列表回调
	 * @param autoPlay 加载完后是否自动播放，如果为true则从上次的位置开始播放
	 */
	public void loadLocalMusic(final MusicCallback callback, final boolean autoPlay) {
		setPlayType(PlayType.Local);
		LocalPlayer player = LocalPlayer.getInstance(mContext);
		player.getLocalPlaylist(2000, new LoadMusicCallback() {
			@Override
			public void onLoadMusic(List<PlaylistEntity> list) {
				mMusicList = new ArrayList<Music>();
				for (int i = 0; i < list.size(); i++) {
					PlaylistEntity entity = list.get(i);
					Music music = new Music();
					music.setId(entity.getUrl());
					music.setClassname(entity.getArtist());
					music.setDuration(entity.getDuration());
					music.setName(entity.getTitle());
					music.setLocalPath(entity.getUrl());
					music.setPath(entity.getUrl());
					mMusicList.add(music);
				}
				int position = preferenceUtil.getPhoneMusicPosition();
				currentPosition = Math.min(position, mMusicList.size() - 1);
				selectePlayEngine(autoPlay);
				callback.onLoadMusic(mMusicList, position);
			}
		});
	}
	
	/**
	 * 是否已经播放了歌曲
	 */
	private boolean hasplay;
	/**
	 * 从上次播放的位置开始播放，要先调用loadLocalMusic方法
	 */
	public boolean playPreMusic(){
		if(hasplay || mType != PlayType.Local){ //如果已经播放了歌曲，就不需要播放上次的歌曲了
			return false;
		}else{
			hasplay = true;
			int position = preferenceUtil.getPhoneMusicPosition();
			currentPosition = Math.max(0, position);
			player.skipTo(currentPosition);
			return true;
		}
	}

	private List<BluetoothDeviceMusicSongEntity> mPlistEntitys;
	private MusicCallback mCallback;
	private int tag;
	private WrapBluetoothDeviceMusicSongListListener deviceMusicSongListListener;
	/**
	 * 加载蓝牙音乐
	 * @param bltDeiviceMusicManager
	 * @param callback
	 */
	public void loadBluetoothDeviceMusic(
			final IBluetoothDeviceMusicManager bltDeiviceMusicManager,
			final MusicCallback callback, Activity act) {
		if(bltDeiviceMusicManager == null){
			return;
		}
		tag++;
		deviceMusicSongListListener = new WrapBluetoothDeviceMusicSongListListener(tag);
		deviceMusicListhandler.removeCallbacks(runnable);
		requestFocus(act);
		deviceMusicManager = bltDeiviceMusicManager;
		mCallback = callback;
		mPlistEntitys = new ArrayList<BluetoothDeviceMusicSongEntity>();
		getBluzMusicList();
	}
	/**
	 * 防止下标越界，分次获取
	 * @param bltDeiviceMusicManager
	 */
	private void getBluzMusicList() {
		int plistSize = mPlistEntitys.size();
		int musicManagerSongSize = deviceMusicManager.getSongSize();
		if (plistSize < musicManagerSongSize){
			int left = musicManagerSongSize - plistSize;
			deviceMusicManager.getSongList(plistSize + 1, Math.min(5, left) ,deviceMusicSongListListener);
		}else if(plistSize == musicManagerSongSize){
			int size = mPlistEntitys.size();
			List<Music> musics = new ArrayList<Music>();
			for (int i = 0; i < size; i++) {
				Music music = new Music();
				BluetoothDeviceMusicSongEntity entity = mPlistEntitys.get(i);
				music.setId(entity.getName());
				music.setName(entity.getName());
				music.setClassname(entity.getArtist());
				music.setPath(entity.getIndex() + entity.getName());
				musics.add(music);
			}
			prepareBluz(
					deviceMusicManager, musics);
			mCallback.onLoadMusic(musics, 0);
		}
	}
	
	private class WrapBluetoothDeviceMusicSongListListener implements OnBluetoothDeviceMusicSongListListener{
		private int tag;
		public WrapBluetoothDeviceMusicSongListListener(int tag){
			this.tag = tag;
		}
		
		@Override
		public void onBluetoothDeviceMusicSongList(
				List<BluetoothDeviceMusicSongEntity> songs) {
			if (PlayerManager.this.tag == this.tag) {
				mPlistEntitys.addAll(songs);
				deviceMusicListhandler.post(runnable);
			}
		}
	}
	
	private Handler deviceMusicListhandler = new Handler();
	private Runnable runnable = new Runnable() {
		@Override
		public void run() {
			getBluzMusicList();
		}
	};

	/**
	 * 获取当前播放位置
	 * 
	 * @return
	 */
	public int getCurrentPosition() {
		return currentPosition;
	}

	/**
	 * 获取当前播放的音乐
	 * 
	 * @return 如果未播放或列表为空则返回null
	 */
	public Music getCurrentMusic() {
		if (mMusicList == null) {
			return null;
		}
		if (currentPosition < 0 || currentPosition > mMusicList.size() - 1) {
			return null;
		}
		return mMusicList.get(currentPosition);
	}

	/**
	 * 获取播放类型
	 * 
	 * @return
	 */
	public static PlayType getPlayType() {
		return mType;
	}

	/**
	 * 根据标识查找音乐
	 * 
	 * @param symbol
	 * @return
	 */
	private Music findMusicBySymbol(String symbol) {
		if (mMusicList == null) {
			return null;
		}
		for (Music m : mMusicList) {
			if (m.getPath().equals(symbol)) {
				return m;
			}
		}
		return null;
	}

	/**
	 * 根据歌曲id查找
	 * 
	 * @param id
	 * @return
	 */
	private Music findMusicById(String id) {
		if (mMusicList == null) {
			return null;
		}
		for (Music m : mMusicList) {
			if (m.getId().equals(id)) {
				return m;
			}
		}
		return null;
	}

	private int findIndexBySymbol(String symbol) {
		Music music = findMusicBySymbol(symbol);
		if (music == null) {
			return 0;
		}
		return mMusicList.indexOf(music);
	}

	/**
	 * 开始播放
	 */
	public void play() {
		if (player != null) {
			player.skipTo(currentPosition);
			player.play();
		} else {
			if (deviceMusicManager != null) {
				deviceMusicManager.select(currentPosition + 1);
				deviceMusicManager.play();
			}
		}
	}

	/**
	 * 初始化播放列表
	 * 
	 * @return
	 */
	private Playlist initPlaylist() {
		if (mMusicList == null) {
			return null;
		}
		Playlist playList = new Playlist();
		for (Music m : mMusicList) {
			String path = m.getPath();
			playList.addTrackUri(path);
		}
		if (getCurrentPlaymode() != null) {
			playList.setPlaylistPlaybackMode(getCurrentPlaymode());
		}
		return playList;
	}

	/**
	 * 播放/暂停切换
	 */
	public void toggle() {
		if (player != null) {
			player.toggle();
		} else if (deviceMusicManager != null) {
			if (isPlaying()) {
				deviceMusicManager.pause();
			} else {
				deviceMusicManager.play();
			}
		}
	}

	/**
	 * 继续播放
	 */
	public void resume() {
		if (player != null) {
			player.resume();
		} else if (deviceMusicManager != null) {
			deviceMusicManager.play();
		}
	}

	/**
	 * 暂停播放
	 */
	public void pause() {
		if (player != null) {
			player.pause();
		} else if (deviceMusicManager != null) {
			deviceMusicManager.pause();
		}
	}

	/**
	 * 下一首
	 */
	public void next() {
		if (player != null) {
			player.next();
		} else if (deviceMusicManager != null) {
			deviceMusicManager.next();
		}
	}

	/**
	 * 上一首
	 */
	public void prev() {
		if (player != null) {
			player.prev();
		} else if (deviceMusicManager != null) {
			deviceMusicManager.previous();
		}
	}

	/**
	 * 跳转到音乐列表的某一首
	 * 
	 * @param index
	 */
	public void skipTo(int index) {
		if (player != null) {
			player.skipTo(index);
		} else if (deviceMusicManager != null) {
			deviceMusicManager.select(index + 1);
		}
	}

	/**
	 * 调整播放进度。蓝牙设备音乐播放时此方法无效
	 * 
	 * @param percent
	 */
	public void seekTo(int percent) {
		if (player != null) {
			player.seekTo(percent);
		}
	}

	/**
	 * 是否正在播放音乐
	 * 
	 * @return
	 */
	public boolean isPlaying() {
		if (player != null) {
			return player.isPlaying();
		} else if (deviceMusicManager != null) {
			return deviceMusicManager.getCurrentPlayState() == PlayState.PLAYING;
		}
		return false;
	}

	/**
	 * 改变播放模式
	 * 
	 * @param mode
	 */
	public void changePlaymode(PlaybackMode mode) {
		PreferenceUtil.getIntance(mContext).savePlayMode(
				PlayUtil.getCurrentModeIndex());
		if (player != null) {
			player.setPlaybackMode(mode);
			if(mPlayListener != null && mode != null){
				mPlayListener.onLoopModeChanged(PlayUtil.convertMode(mode));
			}
		} else if (deviceMusicManager != null) {
			deviceMusicManager.setLoopMode(PlayUtil.convertMode(mode));
		}else{
			if(mPlayListener != null && mode != null){
				mPlayListener.onLoopModeChanged(PlayUtil.convertMode(mode));
			}
		}
	}
	
	/**
	 * 获取当前的播放模式
	 */
	public PlaybackMode getCurrentPlaymode() {
		if (player != null) {
			return player.getPlaybackMode();
		} else if (deviceMusicManager != null) {
			int mode = deviceMusicManager.getCurrentLoopMode();
			return PlayUtil.convertMode(mode);
		}else{
			int index = PreferenceUtil.getIntance(mContext)
					.getPlayMode();
			PlayUtil.setCurrentModeIndex(index);
			return PlayUtil.getModeWithIndex(index);
		}
	}

	/**
	 * 设置监听
	 * 
	 * @param listener
	 * @param type
	 *            监听的播放类型，如果当前的播放类型不是此类型，则监听无效
	 */
	public void setPlayListener(PlayListener listener, PlayType type,
			boolean force) {
		if (type != mType && !force) {
			Log.d(TAG, ">>>>设置监听失败:" + mType);
			return;
		}
		if (isPlayRecentMusic && !force) {
			return;
		}
		mPlayListener = listener;
		if (mType == PlayType.Bluz && deviceMusicManager != null) {
			BluetoothDeviceMusicSongEntity entity = deviceMusicManager
					.getCurrentSong();
			int index = entity.getIndex() - 1;
			if (mPlayListener != null && index != currentPosition) {
				currentPosition = index;
				mPlayListener
						.onMusicStart(entity.getIndex() + entity.getName());
				mPlayListener.onMusicChange(entity.getIndex()
						+ entity.getName());
				mPlayListener.onLoopModeChanged(deviceMusicManager
						.getCurrentLoopMode());
			}
		}
	}

	private void setBluetoothDeviceCardMusicManager(
			IBluetoothDeviceMusicManager manager, boolean playNow) {
		if (manager == null) {
			return;
		}
		deviceMusicManager = manager;
		handler.removeCallbacks(progressRunnable);
		handler.post(progressRunnable);
		deviceMusicManager
				.setOnBluetoothDeviceMusicLoopModeChangedListener(loopModeChangedListner);
		deviceMusicManager
				.setOnBluetoothDeviceMusicPlayStateChangedListener(playStateChangedListener);
		deviceMusicManager
				.setOnBluetoothDeviceMusicSongChangedListener(songChangedListener);
		playWithCurrentEngine(playNow);
		setPlayListener(mPlayListener, PlayType.Bluz, false);
	}

	/**
	 * 在蓝牙卡播放前的准备。因为一旦进入卡播放模式，就会自动播放音乐，此方法主要是为了实现监听
	 * 
	 * @param manager
	 * @param list
	 */
	private void prepareBluz(IBluetoothDeviceMusicManager manager,
			List<Music> musics) {
		setPlayType(PlayType.Bluz);
		if (player != null) {
			player.stop();
			player = null;
		}
		isPlayRecentMusic = false;
		mMusicList = musics;
		setBluetoothDeviceCardMusicManager(manager, false);
	}

	/**
	 * 销毁蓝牙相关
	 */
	public void destroyBluz() {
		if (mType == PlayType.Bluz) {
			mType = null;
		}
	}

	private void showToast(int id) {
		Toast.makeText(mContext, id, Toast.LENGTH_SHORT).show();
	}
	
	/**
	 * 本地音乐和网络音乐的监听
	 */
	private PlayerListener localPlayerListener = new PlayerListener() {

		@Override
		public void onTrackStreamError(String url, int what, int extra) {
			switch (what) {
			case MediaPlayer.MEDIA_ERROR_UNKNOWN:
				break;
			case MediaPlayer.MEDIA_ERROR_SERVER_DIED:
				showToast(R.string.network_exception);
			}

			switch (extra) {
			case MediaPlayer.MEDIA_ERROR_IO:
				if (mType == PlayType.Local) {
					showToast(R.string.file_not_exist_or_damaged);
				} else {
					showToast(R.string.network_exception);
				}
				break;
			case MediaPlayer.MEDIA_ERROR_MALFORMED:
				break;
			case MediaPlayer.MEDIA_ERROR_UNSUPPORTED:
				showToast(R.string.media_error_unsupport);
				break;
			case MediaPlayer.MEDIA_ERROR_TIMED_OUT:
				showToast(R.string.media_error_timeout);
				break;
			case -2147483648:
				showToast(R.string.file_not_exist_or_damaged);
				break;
			}
			if (mPlayListener != null) {
				mPlayListener.onMusicError(url, what, extra);
			}
		}

		@Override
		public void onTrackStop(String url) {

		}

		@Override
		public boolean onTrackStart(String url) {
			hasplay = true;
			if(mType == PlayType.Local && preLocalMusicListPosition == currentPosition && !hasRecover){
				hasRecover = true;
				//player.seekTo(preferenceUtil.getPhoneMusicCurrentDuration());
			}
			if (mPlayListener != null) {
				mPlayListener.onMusicStart(url);
			}
			return false;
		}

		private String preUrl;

		@Override
		public void onTrackProgress(String url, int percent,
				int currentDuration, int duration) {
			if (!url.equals(preUrl)) {
				preUrl = url;
				getCurrentMusic().setDuration(duration);
				saveToRecentPlayDao(url);
			}
			if (mPlayListener != null) {
				mPlayListener.onMusicProgress(url, duration, currentDuration,
						percent);
			}
		}

		@Override
		public void onTrackPause(String url) {
			if (mPlayListener != null) {
				mPlayListener.onMusicPause(url);
			}
		}

		@Override
		public void onTrackChanged(String url) {
			currentPosition = findIndexBySymbol(url);
			if(mType == PlayType.Local){
				preferenceUtil.savePhoneMusicPosition(currentPosition);
			}
			if (mPlayListener != null) {
				mPlayListener.onMusicChange(url);
			}
		}

		@Override
		public void onTrackBuffering(String url, int percent) {
			if (mPlayListener != null) {
				mPlayListener.onMusicBuffering(url, percent);
			}
		}
	};

	/**
	 * 将播放的歌曲，保存到最近播放数据库
	 */
	private void saveToRecentPlayDao(final String symbol) {
		new Thread(new Runnable() {
			@Override
			public void run() {
				Music music = findMusicBySymbol(symbol);
				if (music != null) {
					RecentPlayMusic recentPlay = new RecentPlayMusic(music);
					recentPlay.setPlay_date(System.currentTimeMillis());
					recentPlay.setPlay_type(mType.value);
					//recentPlayDao.insertOrUpdate(recentPlay);
				}
			}
		}).start();
	}

	/**
	 * 蓝牙设备循环模式变化监听
	 */
	OnBluetoothDeviceMusicLoopModeChangedListener loopModeChangedListner = new OnBluetoothDeviceMusicLoopModeChangedListener() {

		@Override
		public void onBluetoothDeviceMusicLoopModeChanged(int loopMode) {
			if (mPlayListener != null) {
				mPlayListener.onLoopModeChanged(loopMode);
			}
		}
	};

	/**
	 * 蓝牙播放播放状态变化监听
	 */
	OnBluetoothDeviceMusicPlayStateChangedListener playStateChangedListener = new OnBluetoothDeviceMusicPlayStateChangedListener() {

		@Override
		public void onBluetoothDeviceMusicPlayStateChanged(int state) {
			if (mMusicList == null || mMusicList.isEmpty()
					|| currentPosition > mMusicList.size() - 1
					|| currentPosition < 0) {
				return;
			}
			Music music = mMusicList.get(currentPosition);
			switch (state) {
			case BluetoothDeviceCardMusicManager.PlayState.PLAYING:
				if (mPlayListener != null) {
					mPlayListener.onMusicStart(music.getPath());
				}
				break;
			case BluetoothDeviceCardMusicManager.PlayState.PAUSED:
				if (mPlayListener != null) {
					mPlayListener.onMusicPause(music.getPath());
				}
				break;
			case BluetoothDeviceCardMusicManager.PlayState.WAITING:

				break;
			}
		}
	};

	/**
	 * 用于蓝牙播放进度更新
	 */
	private Handler handler = new Handler();

	private Runnable progressRunnable = new Runnable() {
		@Override
		public void run() {
			if (deviceMusicManager != null) {
				BluetoothDeviceMusicSongEntity entity = deviceMusicManager
						.getCurrentSong();
				int duration = deviceMusicManager.getCurrentSongDuration();
				int currentDuration = deviceMusicManager
						.getCurrentSongCurrentPosition();
				int percent = (int) ((float) currentDuration / duration * 1000);
				if (mPlayListener != null) {
					mPlayListener.onMusicProgress(
							entity.getIndex() + entity.getName(), duration,
							currentDuration, percent);
				}
				handler.postDelayed(this, 500);
			}
		}
	};

	/**
	 * 蓝牙歌曲改变监听
	 */
	OnBluetoothDeviceMusicSongChangedListener songChangedListener = new OnBluetoothDeviceMusicSongChangedListener() {
		@Override
		public void onBluetoothDeviceMusicSongChanged(
				BluetoothDeviceMusicSongEntity entity) {
			currentPosition = findIndexBySymbol(entity.getIndex()
					+ entity.getName());
			if (mPlayListener != null) {
				mPlayListener
						.onMusicStart(entity.getIndex() + entity.getName());
				mPlayListener.onMusicChange(entity.getIndex()
						+ entity.getName());
			}
		}
	};
	
	public int getAudioSessionId() {
		if(player != null){
			return player.getAudioSessionId();
		}
		return -1;
	}

	/**
	 * 关闭播放器，释放相关资源
	 */
	public void destoryAll() {
		setPlayType(null);
		destroyLocalPlayer();
		destroyBluzPlayer();
		currentPosition = -1;
		mMusicList = null;
		instance = null;
		mContext = null;
	}
	
	public void destroyLocalPlayer(){
		if (player != null) {
			player.pause();
			player.stop();
			player = null;
		}
		if (mType != PlayType.Bluz) {
			mType = null;
		}
	}
	public void destroyBluzPlayer(){
		if (deviceMusicManager != null) {
			if (isPlaying()) {
				deviceMusicManager.pause();
			}
			deviceMusicManager = null;
		}
		deviceMusicListhandler.removeCallbacks(runnable);
		handler.removeCallbacks(progressRunnable);
		if (mType == PlayType.Bluz) {
			mType = null;
		}
	}

	/**
	 * 播放类型枚举
	 * 
	 * @author chiemy
	 */
	public enum PlayType {
		/**
		 * 本地播放
		 */
		Local(0),
		/**
		 * 网络
		 */
		Net(1),
		/**
		 * 蓝牙设备播放
		 */
		Bluz(2);
		public int value;

		PlayType(int value) {
			this.value = value;
		}
	}
	
}
