package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.StringFormatUtil;

public class SimpleMusicItem extends FrameLayout {
	private View stateIv;
	private TextView artistTv, songNameTv, durationTv;
	
	public SimpleMusicItem(Context context){
		super(context);
		LayoutInflater.from(context).inflate(R.layout.simple_music_list_item, this);
		stateIv = (View) findViewById(R.id.iv_state);
		songNameTv = (TextView) findViewById(R.id.song_name_tv);
		artistTv = (TextView) findViewById(R.id.artist_tv);
		durationTv = (TextView) findViewById(R.id.duration_tv);
		durationTv.setVisibility(View.GONE);
	}
	
	public void render(int index, final Music music, boolean blzDeviceMusic) {
		songNameTv.setText(music.getName());
		if(blzDeviceMusic){
			if(TextUtils.isEmpty(music.getArtist())){
				artistTv.setVisibility(View.GONE);
			}else{
				artistTv.setText(music.getArtist());
			}
			durationTv.setVisibility(View.GONE);
		}else{
			String artist = music.getArtist();
			if("<unknown>".equals(artist)){
				artist = "未知歌手";
			}
			artistTv.setText(artist);
			durationTv.setText(StringFormatUtil.formatDuration(music.getDuration()));
		}
	}
	
	public void setSelected(boolean playing) {
		stateIv.setVisibility(View.VISIBLE);
	}
	
	public void disSelected() {
		stateIv.setVisibility(View.INVISIBLE);
	}
}
