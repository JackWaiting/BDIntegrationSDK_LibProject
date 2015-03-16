package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.bean.Music;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.WrapImageLoader;

public class NetMusicItemView extends FrameLayout {
	private TextView songNameTv;
	private TextView artistTv;
	private ImageView imageIv, playStateIv;
	private WrapImageLoader imageLoader;
	private String imageUrl = "";
	private OnClickListener playStateIvClickListener;
	
	public NetMusicItemView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(R.layout.music_list_item, this);
		imageLoader = WrapImageLoader.getInstance(context);
		initView();
	}

	private void initView() {
		songNameTv = (TextView) findViewById(R.id.tv_music_name);
		artistTv = (TextView) findViewById(R.id.tv_music_class);
		imageIv = (ImageView) findViewById(R.id.image_pic);
		playStateIv = (ImageView) findViewById(R.id.iv_play_state);
		playStateIv.setTag(R.id.tag_is_playing, false);
	}

	public void render(Music music, int position) {
		playStateIv.setTag(position);
		songNameTv.setText(music.getName());
		artistTv.setText(music.getArtist());
		String newImageUrl = music.getPicpath_m();
		if (!imageUrl.equals(newImageUrl)) {
			imageLoader.displayImage(newImageUrl, imageIv, 1, null);
			imageUrl = newImageUrl;
		}
	}
	
	public void setOnPlayButtonClickListener(OnClickListener listener) {
		this.playStateIvClickListener = listener;
		playStateIv.setOnClickListener(listener);
	}
	
	public OnClickListener getOnPlayButtonClickListener() {
		return playStateIvClickListener;
	}

	public void setSelected(boolean playing) {
		playStateIv.setTag(R.id.tag_is_playing, playing);
		int drawable = R.drawable.selector_list_pause_btn;
		if(playing){
			drawable = R.drawable.selector_list_play_btn;
		}
		playStateIv.setImageResource(drawable);
	}

	public void disSelected() {
		playStateIv.setImageResource(R.drawable.selector_list_pause_btn);
	}
}
