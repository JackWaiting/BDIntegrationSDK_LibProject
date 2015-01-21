package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import java.util.List;

import android.view.View;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.adapter.SimpleMusicListAdapter;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.listener.SimpleMusicPlayListener;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.MusicCallback;
import com.chipsguide.app.colorbluetoothlamp.v2.media.PlayerManager.PlayType;

public abstract class SimpleMusicFrag extends BaseFragment implements OnItemClickListener,SimpleMusicPlayListener{
	private SimpleMusicListAdapter adapter;
	private PlayerManager playerManager;
	private int prePosition = -1;
	private boolean userClick;
	private ListView musicListLv;
	/**
	 * 获取与此Fragment对应的播放类型
	 * @return
	 */
	public abstract PlayType getPlayType();
	
	@Override
	protected void initBase() {
		playerManager = PlayerManager.getInstance(getActivity().getApplicationContext());
		adapter = new SimpleMusicListAdapter(getActivity());
	}

	@Override
	protected void initView() {
		musicListLv = (ListView) findViewById(R.id.lv_musiclist);
		musicListLv.setOnItemClickListener(this);
		musicListLv.setAdapter(adapter);
	}

	@Override
	protected void initData() {
		if(getPlayType() == PlayType.Local){
			playerManager.loadLocalMusic(new MusicCallback() {
				@Override
				public void onLoadMusic(List<Music> musics, int prePosition) {
					adapter.setMusicList(musics);
				}
			}, false);
		}else if(getPlayType() == PlayType.Bluz){
			playerManager.loadBluetoothDeviceMusic(null, new MusicCallback() {
				@Override
				public void onLoadMusic(List<Music> musics, int prePosition) {
					adapter.setMusicList(musics);
				}
			});
		}
	}

	@Override
	public void onItemClick(AdapterView<?> parent, View view, int position,
			long id) {
		if(position == prePosition){
			playerManager.toggle();
		}else{
			userClick = true;
			prePosition = position;
			playerManager.skipTo(position);
			adapter.setSelected(position);
		}
	}

	@Override
	public void onMusicProgress(long duration, long currentDuration, int percent) {
		
	}

	@Override
	public void onMusicPlayStateChange(boolean playing) {
	}

	@Override
	public void onMusicChange() {
		if(adapter == null){
			return;
		}
		if(getPlayType() != PlayerManager.getPlayType()){
			adapter.setSelected(-1);
		}else{
			int position = playerManager.getCurrentPosition();
			adapter.setSelected(position);
			if(!userClick && musicListLv != null){
				musicListLv.setSelection(position);
			}
			userClick = false;
		}
	}

}
