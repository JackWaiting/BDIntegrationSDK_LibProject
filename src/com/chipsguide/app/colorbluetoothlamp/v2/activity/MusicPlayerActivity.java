package com.chipsguide.app.colorbluetoothlamp.v2.activity;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import android.bluetooth.BluetoothDevice;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.support.v4.view.PagerAdapter;
import android.support.v4.view.ViewPager;
import android.text.TextUtils;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.CheckBox;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.SeekBar;
import android.widget.SeekBar.OnSeekBarChangeListener;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.SimpleMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.bluetooth.BluetoothDeviceManagerProxy.SimpleDeviceUiChangedListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.LampManager.LampListener;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PreferenceUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.StringFormatUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.view.MusicProgressView;
import com.chipsguide.app.colorbluetoothlamp.v2.view.MusicProgressView.SimpleSeekArcChangeListener;
import com.chipsguide.app.colorbluetoothlamp.v2.view.MusicSpectrumView;
import com.chipsguide.app.colorbluetoothlamp.v2.view.TitleView;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.CirclePageIndicator;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.SeekArc;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.SlidingLayer;
import com.chipsguide.lib.bluetooth.extend.devices.BluetoothDeviceColorLampManager;
import com.chipsguide.lib.bluetooth.interfaces.callbacks.OnBluetoothDeviceConnectionStateChangedListener;
import com.chipsguide.lib.bluetooth.managers.BluetoothDeviceManager;
import com.platomix.lib.playerengine.api.PlaybackMode;

public class MusicPlayerActivity extends BaseActivity implements OnBluetoothDeviceConnectionStateChangedListener,LampListener{
	public static final String EXTRA_MODE_TO_BE = "mode_to_be";
	private int modeTobe = BluetoothDeviceManager.Mode.A2DP; // 将要切换的模式
	private PlayerManager playerManager;
	private int currentModeRes;
	private SimpleMusicListAdapter mAdapter;
	private int currentPosition;
	private boolean userClick, update;
	private LampManager mLampManager;

	private TitleView titleView;
	private ImageView playBtn, playmodeBtn;
	private SlidingLayer playListLayer;
	private ListView playListLv;
	private MusicProgressView progressLayout;
	private MusicSpectrumView spectrumLayout;
	private TextView musicNameTv, artistTv, durationTv;
	private SeekBar volumeSeekBar;
	private CheckBox musicRhythmCb;//音乐律动选择框
	private BluetoothDeviceManagerProxy blzDeviceProxy;
	private static final int VOLUME_FACTOR = 1;
	private static final int MAX_VOLUME = 31;
//	private int currentVolume;

	private List<View> views = new ArrayList<View>();

	@Override
	public void initBase() {
		blzDeviceProxy = BluetoothDeviceManagerProxy.getInstance(getApplicationContext());
		blzDeviceProxy.addOnBluetoothDeviceConnectionStateChangedListener(this);
		modeTobe = getIntent().getIntExtra(EXTRA_MODE_TO_BE, modeTobe);
		playerManager = PlayerManager.getInstance(getApplicationContext());
		int index = PreferenceUtil.getIntance(getApplicationContext())
				.getPlayMode();
		PlayUtil.setCurrentModeIndex(index);
		PlaybackMode mode = PlayUtil.getModeWithIndex(index);
		currentModeRes = PlayUtil.getModeImgRes(mode);
		mLampManager = LampManager.getInstance(this);
		mLampManager.addOnBluetoothDeviceLampListener(this);

		mAdapter = new SimpleMusicListAdapter(this);
		registBroadcase();
	}

	@Override
	public void initUI() {
		initPlaylistLayer();
		initPagerView();
		titleView = (TitleView) findViewById(R.id.titleView);
		titleView.setOnClickListener(this);
		titleView.setRightBtnVisibility(false);
		musicNameTv = (TextView) findViewById(R.id.tv_music_name);
		artistTv = (TextView) findViewById(R.id.tv_artist);
		durationTv = (TextView) findViewById(R.id.tv_duration2);

		playBtn = (ImageView) findViewById(R.id.iv_play_state);
		playmodeBtn = (ImageView) findViewById(R.id.iv_play_mode);
		playmodeBtn.setImageResource(currentModeRes);

		volumeSeekBar = (SeekBar) findViewById(R.id.seekbar_vol);
		volumeSeekBar.setOnSeekBarChangeListener(new OnSeekBarChangeListener() {
			@Override
			public void onStopTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onStartTrackingTouch(SeekBar seekBar) {
			}
			@Override
			public void onProgressChanged(SeekBar seekBar, int progress,
					boolean fromUser) {
				if(fromUser){
					setVolume(progress/VOLUME_FACTOR);
				}
			}
		});
		
		musicRhythmCb = (CheckBox) findViewById(R.id.cb_music_rhythm);
		if(LampManager.THYHM == BluetoothDeviceColorLampManager.Effect.RHYTHM)
		{
			musicRhythmCb.setChecked(true);
		}
		musicRhythmCb.setOnClickListener(new OnClickListener()
		{
			
			@Override
			public void onClick(View v)
			{
				if(musicRhythmCb.isChecked())
				{
					mLampManager.setLampEffect(BluetoothDeviceColorLampManager.Effect.RHYTHM);
				}else
				{
					mLampManager.setLampEffect(BluetoothDeviceColorLampManager.Effect.NORMAL);
				}
			}
		});
		
		initVolume();
		initForType();
		updateUI(true);
	}

	private void initVolume() {
		if(!blzDeviceProxy.isConnected()){
			findViewById(R.id.volume_layout).setVisibility(View.INVISIBLE);
			return;
		}
		findViewById(R.id.volume_layout).setVisibility(View.VISIBLE);
		blzDeviceProxy.setDeviceUiChangedListener(new SimpleDeviceUiChangedListener(){
			@Override
			public void onVolumeChanged(boolean firstCallback, int volume, boolean on) {
//				currentVolume = volume;
				volumeSeekBar.setProgress(volume);
			}
		});
		volumeSeekBar.setMax(MAX_VOLUME * VOLUME_FACTOR);
		volumeSeekBar.setProgress(blzDeviceProxy.getCurrentVolume() * VOLUME_FACTOR);
	}
	
	
	private void setVolume(int volume) {
		blzDeviceProxy.adjustVolume(volume);
	}

	private void initPagerView() {
		progressLayout = new MusicProgressView(this);
		progressLayout
				.setOnSeekArcChangeListener(new SimpleSeekArcChangeListener() {
					@Override
					public void onStopTrackingTouch(SeekArc seekArc) {
						playerManager.seekTo((int) ((float) seekArc
								.getProgress() / 1000 * 1000));
					}
				});
		spectrumLayout = new MusicSpectrumView(this);
		spectrumLayout.setAudioSessionId(playerManager.getAudioSessionId());
		views.add(progressLayout);
		//views.add(spectrumLayout);

		ViewPager viewPager = (ViewPager) findViewById(R.id.viewPager);
		viewPager.setAdapter(new MyPagerAdapter());
		CirclePageIndicator indicator = (CirclePageIndicator) findViewById(R.id.pageIndicator);
		indicator.setViewPager(viewPager);
	}

	/**
	 * 播放列表界面
	 */
	private void initPlaylistLayer() {
		playListLayer = (SlidingLayer) findViewById(R.id.playlist_layer);
		playListLv = (ListView) findViewById(R.id.play_list);
		playListLv.setAdapter(mAdapter);
		mAdapter.setMusicList(playerManager.getMusicList());
		playListLv.setOnItemClickListener(new OnItemClickListener() {
			@Override
			public void onItemClick(AdapterView<?> parent, View view,
					int position, long id) {
				if (currentPosition == position) {
					playerManager.toggle();
					mAdapter.setSelected(position, !playerManager.isPlaying());
				} else {
					userClick = true;
					currentPosition = position;
					mAdapter.setSelected(currentPosition, true);
					playerManager.skipTo(position);
				}
			}
		});
	}

	/**
	 * 根据播放类型初始化界面
	 */
	private void initForType() {
		PlayType type = PlayerManager.getPlayType();
		if (type != null && type == PlayType.Net) {
		} else if (type != null && type == PlayType.Bluz) {
			progressLayout.setSeekable(false);
		} else if (type == null) {
			if (playerManager.loadRecentPlay()) {
				initForType();
			}
		}
	}

	/**
	 * 改变播放按钮图片
	 */
	private void changePlaybtn() {
		int resId;
		if (playerManager.isPlaying()) {
			resId = R.drawable.selector_btn_play;
		} else {
			resId = R.drawable.selector_btn_pause;
		}
		playBtn.setImageResource(resId);
	}

	/**
	 * 切换歌曲，开始/暂停时更新ui
	 */
	private void updateUI(boolean force) {
		if (update && !force) {
			return;
		}
		spectrumLayout.setAudioSessionId(playerManager.getAudioSessionId());
		update = true;
		Music currentMusic = playerManager.getCurrentMusic();
		currentPosition = playerManager.getCurrentPosition();
		changePlaybtn();
		if (currentMusic != null) {
			String title = currentMusic.getName();
			if (TextUtils.isEmpty(title)) {
				title = currentMusic.getName_en();
			}
			musicNameTv.setText(title);
			String artist = currentMusic.getArtist();
			if ("<unknown>".equals(artist)) {
				artist = "未知歌手";
			}
			if (!TextUtils.isEmpty(artist)) {
				artistTv.setText(artist);
			}
			titleView.setTitleText(title);
			String currentDurationStr = StringFormatUtil.formatDuration(0);
			String durationStr = StringFormatUtil.formatDuration(currentMusic
					.getDuration());
			durationTv.setText(currentDurationStr + "/" + durationStr);
			progressLayout.updateMusicImage(currentMusic.getPicpath_l());
			progressLayout.updateProgress(currentMusic.getDuration(), 0, 0);
			progressLayout.playStateChange(playerManager.isPlaying());
		}
		mAdapter.setSelected(currentPosition, true);
		// 不是用户点击，才滚动ListView
		if (!userClick) {
			playListLv.setSelection(currentPosition);
		}
	}

	@Override
	public void initData() {
	}

	@Override
	public void initListener() {
	}

	/**
	 * 播放器回调。此监听比Activity的生命周期要长，所以声明为静态内部类，防止内存泄露
	 */
	private static class MyPlayListener extends PlayListener {
		private WeakReference<MusicPlayerActivity> ref;

		public MyPlayListener(MusicPlayerActivity act) {
			ref = new WeakReference<MusicPlayerActivity>(act);
		}

		@Override
		public void onMusicStart(String musicSymbol) {
			MusicPlayerActivity act = ref.get();
			if (act != null) {
				act.mAdapter.setSelected(
						act.playerManager.getCurrentPosition(), true);
				act.playBtn.setImageResource(R.drawable.selector_btn_play);
				act.progressLayout.playStateChange(true);
			}
		}

		@Override
		public void onMusicPause(String musicSymbol) {
			MusicPlayerActivity act = ref.get();
			if (act != null) {
				act.mAdapter.setSelected(
						act.playerManager.getCurrentPosition(), false);
				act.playBtn.setImageResource(R.drawable.selector_btn_pause);
				act.progressLayout.playStateChange(false);
			}
		}

		@Override
		public void onMusicProgress(String musicSymbol, long duration,
				long currentDuration, int percent) {
			MusicPlayerActivity act = ref.get();
			if (act != null) {
				String currentDurationStr = StringFormatUtil
						.formatDuration(currentDuration);
				String durationStr = StringFormatUtil.formatDuration(duration);
				act.durationTv.setText(currentDurationStr + "/" + durationStr);
				act.progressLayout.updateProgress(duration, currentDuration,
						percent);
				if (!act.update) {
					act.updateUI(false);
				}
			}
		}

		@Override
		public void onMusicChange(String musicSymbol) {
			MusicPlayerActivity act = ref.get();
			if (act != null) {
				act.updateUI(true);
			}
		}

		@Override
		public void onMusicBuffering(String musicSymbol, int percent) {
			MusicPlayerActivity act = ref.get();
			if (act != null && percent == 0) {
				act.showToast(R.string.buffering);
			}
		}

		@Override
		public void onMusicError(String musicSymbol, int what, int extra) {
		}

		@Override
		public void onLoopModeChanged(int mode) {
			MusicPlayerActivity act = ref.get();
			if (act != null) {
				PlaybackMode playmode = PlayUtil.convertMode(mode);
				PlayUtil.setCurrentMode(playmode);
				int imgRes = PlayUtil.getModeImgRes(playmode);
				act.playmodeBtn.setImageResource(imgRes);
			}
		}

	}

	/**
	 * 改变播放模式
	 */
	public void changePlaybackMode() {
		int res = PlayUtil.nextModeRes();
		playmodeBtn.setImageResource(res);
		int textRes = PlayUtil.getCurrentModeTextRes();
		playerManager.changePlaymode(PlayUtil.getModeWithRes(res));
		PreferenceUtil.getIntance(getApplicationContext()).savePlayMode(
				PlayUtil.getCurrentModeIndex());
		
		titleView.setToastText(String.format(getString(R.string.switch_mode), getString(textRes)));
	}

	@Override
	public void onClick(View view) {
		switch (view.getId()) {
		case R.id.left_btn:
			finish();
			break;
		case R.id.iv_play_state:
			if (playerManager.isPlaying()) {
				playerManager.pause();
			} else {
				currentPosition = Math.max(currentPosition, 0);
				playerManager.skipTo(currentPosition);
			}
			break;
		case R.id.iv_next:
			playerManager.next();
			break;
		case R.id.iv_pre:
			playerManager.prev();
			break;
		case R.id.iv_play_mode:
			changePlaybackMode();
			break;
		case R.id.iv_playlist:
			showPlaylist();
			break;
		case R.id.hide_btn:
			playListLayer.closeLayer(true);
			break;
		}
	}

	private void showPlaylist() {
		mAdapter.setSelected(currentPosition, playerManager.isPlaying());
		if (playListLayer.isOpened()) {
			playListLayer.closeLayer(true);
		} else {
			playListLayer.openLayer(true);
		}
	}

	@Override
	public int getLayoutId() {
		return R.layout.activity_music_player;
	}

	@Override
	public void onBackPressed() {
		if (playListLayer.isOpened()) {
			playListLayer.closeLayer(true);
		} else {
			super.onBackPressed();
		}
	}

	@Override
	protected void onPause() {
		super.onPause();
		// update = false;
	}

	@Override
	protected void onResume() {
		super.onResume();
		playerManager.setPlayListener(new MyPlayListener(this),
				PlayerManager.getPlayType(), true);
	}

	private class MyPagerAdapter extends PagerAdapter {

		@Override
		public int getCount() {
			return views.size();
		}

		@Override
		public View instantiateItem(ViewGroup container, int position) {
			container.addView(views.get(position));
			return views.get(position);
		}

		@Override
		public boolean isViewFromObject(View arg0, Object arg1) {
			return arg0 == arg1;
		}

		@Override
		public void destroyItem(ViewGroup container, int position, Object object) {
			super.destroyItem(container, position, object);
		}
		
		
	}

	private boolean register;

	private void registBroadcase() {
		IntentFilter filter = new IntentFilter();
		filter.addAction(BluetoothDeviceManagerProxy.ACTION_TF_CARD_PLUG_CHANGED);
		filter.addAction(BluetoothDeviceManagerProxy.ACTION_MODE_CHANGE);
		registerReceiver(bluzBroadcaseReceiver, filter);
		register = true;
	}

	private BroadcastReceiver bluzBroadcaseReceiver = new BroadcastReceiver() {
		@Override
		public void onReceive(Context context, Intent intent) {
			String action = intent.getAction();
			if (BluetoothDeviceManagerProxy.ACTION_MODE_CHANGE.equals(action)) {
				int newMode = intent.getIntExtra(
						BluetoothDeviceManagerProxy.EXTRA_NEW_MODE, -1);
				int oldMode = intent.getIntExtra(
						BluetoothDeviceManagerProxy.EXTRA_OLD_MODE, -1);
				int a2dpMode = BluetoothDeviceManager.Mode.A2DP;
				int cardMode = BluetoothDeviceManager.Mode.CARD;
				if (modeTobe == newMode) {
					return;
				}
				if (newMode != a2dpMode && oldMode == a2dpMode && oldMode != -1) {// 之前的模式是A2DP，新模式不是A2DP则结束
					finish();
				} else if (newMode != cardMode && oldMode == cardMode) {// 之前的模式是卡模式，新模式不是卡模式则结束
					finish();
				}
			}
		}
	};

	//手机音量健不能调节固件音量
	/*@Override
	public boolean onKeyDown(int keyCode, KeyEvent event) {
		switch (keyCode) {
		case KeyEvent.KEYCODE_VOLUME_UP:
		case KeyEvent.KEYCODE_VOLUME_DOWN:
			if(!blzDeviceProxy.isConnected()){
				return super.onKeyDown(keyCode, event);
			}
			if(keyCode == KeyEvent.KEYCODE_VOLUME_UP){
				currentVolume = Math.min(MAX_VOLUME, ++currentVolume);
			}else{
				currentVolume = Math.max(0, --currentVolume);
			}
			//volumeSeekBar.setProgress(currentVolume * VOLUME_FACTOR);
			setVolume(currentVolume);
			return true;
		}
		return super.onKeyDown(keyCode, event);
	}*/

	@Override
	protected void onDestroy() {
		super.onDestroy();
		blzDeviceProxy.removeDeviceUiChangedListener();
		mLampManager.removeOnBluetoothDeviceLampListener(this);
		if (register) {
			unregisterReceiver(bluzBroadcaseReceiver);
		}
	}

	@Override
	public void onBluetoothDeviceConnectionStateChanged(BluetoothDevice arg0,
			int state) {
		initVolume();
	}
	
	@Override
	public void onLampRhythmChange(int rhythm)
	{
		// TODO 更新音乐律动
		if(rhythm == BluetoothDeviceColorLampManager.Effect.RHYTHM)
		{
			musicRhythmCb.setChecked(true);
		}else
		{
			musicRhythmCb.setChecked(false);
		}
	}

	@Override
	public void updateVolume()
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void updateConnectState(boolean isConnect)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLampStateInqiryBackChange(boolean colorState, boolean OnorOff)
	{
		// TODO Auto-generated method stub
		
	}

	@Override
	public void onLampStateFeedBackChange(boolean colorState, boolean OnorOff)
	{
		// TODO Auto-generated method stub
		
	}


	@Override
	public void onLampColor(int red, int green, int blue)
	{
		// TODO Auto-generated method stub
		
	}

}
