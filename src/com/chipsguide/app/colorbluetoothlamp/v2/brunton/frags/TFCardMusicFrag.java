package com.chipsguide.app.colorbluetoothlamp.v2.brunton.frags;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.content.DialogInterface;
import android.content.DialogInterface.OnDismissListener;
import android.util.Log;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.adapter.SimpleMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.been.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.bluetooth.BluetoothDeviceManagerProxy;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.bluetooth.OnDeviceMusicManagerReadyListener;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.media.PlayerManager.PlayType;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.view.CustomDialog;
import com.chipsguide.lib.bluetooth.interfaces.templets.IBluetoothDeviceMusicManager;

public class TFCardMusicFrag extends SimpleMusicFrag implements SimpleMusicPlayListener{
	public static final String TAG = "bluz";
	private BluetoothDeviceManagerProxy bluzDeviceManProxy;
	private boolean loadFinished;
	private boolean loadAll = false;

	public static TFCardMusicFrag getInstance(Context context, String tag, SimpleMusicListAdapter adapter, OnItemSelectedListener listener){
		TFCardMusicFrag frag = new TFCardMusicFrag();
		frag.setFilterTag(tag);
		frag.setAdapter(adapter);
		frag.setOnItemSelectedListener(listener);
		return frag;
	}
	@Override
	protected void initBase() {
		super.initBase();
		bluzDeviceManProxy = BluetoothDeviceManagerProxy.getInstance(getActivity().getApplicationContext());
	}
	
	@Override
	public PlayType getPlayType() {
		return PlayType.Bluz;
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_tf_card_music;
	}
	
	@Override
	public void setUserVisibleHint(boolean isVisibleToUser) {
		super.setUserVisibleHint(isVisibleToUser);
		if (isVisibleToUser && bluzDeviceManProxy != null) {
			if (bluzDeviceManProxy.isInMusicManagerMode() && loadFinished) {
				return;
			}
			if(!loadAll){
				addFooterView();
			}
			if(adapter !=null)
			{
				adapter.setMusicList(new ArrayList<Music>());
			}
			if(loadAll){
				showLoadingMusicDialog();
			}
			bluzDeviceManProxy
					.setOnBluetoothDeviceMuisicReadyListener(deviceMusicManagerReadyListener);
		}
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
			onLoadPlayList();
		}
	};

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
				playerManager.cancelLoadBluetoothDeviceMusic();
			}
		});
		String str = String.format(formatStr, 0);
		dialog.setMessage(str);
		dialog.show();
	}
	
	private List<Music> bluzMusics = new ArrayList<Music>();
	private int startPosi;
	private static final int EXPECT_SIZE = 20; //每次加载个数
	@Override
	public void onLoadPlayList() {
		int startPosi = this.startPosi;
		int exceptSize = EXPECT_SIZE;
		if(loadAll){
			startPosi = 0;
			exceptSize = bluzMusicmanager.getSongSize();
		}
		loadBlzMusic(startPosi, exceptSize);
	}
	
	@Override
	public void onLoading(int loaded, int total) {
		if(dialog != null){
			String str = String.format(formatStr, (int) ((float) loaded
					/ total * 100));
			dialog.updateMessage(str);
		}
	}
	
	@Override
	public void onLoadMusic(List<Music> musics, int prePosition) {
		loadFinished = true;
		bluzMusics.addAll(musics);
		adapter.setMusicList(bluzMusics);
		adapter.setSelected(prePosition);
		startPosi = bluzMusics.size();
		if (startPosi >= bluzMusicmanager.getSongSize()) {
			removeFooterView();
		}
		selectedByTag(bluzMusics, filterTag);
		
		if(loadAll && dialog != null && dialog.isShowing()){
			int size = musics.size();
			String str = String.format(formatStrSuccess, size, size);
			dialog.updateMessage(str);
			dialog.dismiss(true, 3000);
		}
	}
	
	
	/**
	 * 分页加载
	 */
	private void loadBlzMusic(int startPosi, int expectSize) {
		playerManager.loadBluetoothDeviceMusic(startPosi, expectSize, bluzMusicmanager, this, getActivity());
	}
	
}
