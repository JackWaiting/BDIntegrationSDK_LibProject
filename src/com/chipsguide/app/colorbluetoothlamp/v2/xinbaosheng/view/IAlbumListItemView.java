package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean.Album;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.utils.WrapImageLoader;

/**
 * 专辑列表item视图
 * 
 * @author chiemy
 * 
 */
public abstract class IAlbumListItemView extends FrameLayout {
	protected TextView nameTv;
	protected TextView countTv;
	protected TextView updateTimeTv;
	protected ImageView albumPicIv;
	protected View actionBtn;
	private WrapImageLoader imageLoader;
	
	private String imageUrl = "";
	
	public IAlbumListItemView(Context context) {
		super(context);
		LayoutInflater.from(context).inflate(getInflateLayout(), this);
		imageLoader = WrapImageLoader.getInstance(context);
		init();
	}

	private void init() {
		nameTv = (TextView) findViewById(R.id.tv_name);
		countTv = (TextView) findViewById(R.id.tv_count);
		updateTimeTv = (TextView) findViewById(R.id.tv_plays);
		albumPicIv = (ImageView) findViewById(R.id.iv_image);
		actionBtn = findViewById(R.id.action_btn);
	}
	
	public void render(Album album){
		String name = album.getName();
		if(TextUtils.isEmpty(name)){
			name = album.getName_en();
		}
		nameTv.setText(name);
		countTv.setText("节目数：" + album.getMusicCount());
		renderPlayCount(album);
		if(!imageUrl.equals(album.getCoverpath_m())){
			imageLoader.displayImage(album.getCoverpath_m(), albumPicIv, 1, null);
		}
		imageUrl = album.getCoverpath_m();
	}
	
	private void renderPlayCount(Album album) {
		String playCount = album.getPlays_count();
		StringBuilder des = new StringBuilder("总播放量：");
		if (playCount != null && playCount.length() > 4) {
			des.append(playCount.substring(0, playCount.length() - 4) + "万");
		} else {
			des.append(playCount);
		}
		if(!TextUtils.isEmpty(playCount)){
			updateTimeTv.setText(des);
		}
	}
	
	/**
	 * 为id为action_btn的控件设置点击监听
	 * @param listener
	 */
	public void setOnActionButtonClickListener(OnClickListener listener) {
		actionBtn.setOnClickListener(listener);
	}

	/**
	 * 获取填充的布局文件，布局中必须包含如下控件<br>
	 * <ul>
	 * <li>id为tv_name的TextView,显示专辑名称</li>
	 * <li>id为tv_count的TextView,显示节目数</li>
	 * <li>id为tv_plays的TextView</li>
	 * <li>id为action_btn的View（不限制控件类型）</li>
	 * <li>id为iv_image的ImageView，显示专辑封面</li>
	 * </ul>
	 * 
	 * @return
	 */
	public abstract int getInflateLayout();
}
