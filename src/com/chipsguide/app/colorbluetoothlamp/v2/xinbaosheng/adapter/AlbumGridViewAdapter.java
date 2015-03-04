package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean.Column;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.utils.PixelUtil;

public class AlbumGridViewAdapter extends BaseAdapter {
	private static final int DEF_NUM = 9;
	
	private Context mContext;
	private List<Column> list = new ArrayList<Column>();
	int[] images = { R.drawable.btn_album01_selector, R.drawable.btn_album02_selector, R.drawable.btn_album03_selector,
			R.drawable.btn_album04_selector, R.drawable.btn_album05_selector, R.drawable.btn_album06_selector,
			R.drawable.btn_album07_selector, R.drawable.btn_album08_selector, R.drawable.btn_album09_selector,
			R.drawable.btn_album10_selector, R.drawable.btn_album11_selector, R.drawable.btn_album13_selector,
			R.drawable.btn_album14_selector, R.drawable.btn_album15_selector, R.drawable.btn_album16_selector,
			R.drawable.btn_album17_selector, R.drawable.btn_album18_selector, R.drawable.btn_album19_selector,
			R.drawable.btn_album20_selector };

	private boolean isHide = true;
	private int mLimited = 0;
	private int headHeight;

	public AlbumGridViewAdapter(Context context) {
		this.mContext = context;
		headHeight = PixelUtil.dp2px(50, context);
	}
	
	public void setList(List<Column> list) {
		this.list = list;
		this.mLimited = Math.min(DEF_NUM, list.size());
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mLimited;
	}

	@Override
	public Object getItem(int position) {
		return list.get(position);
	}

	@Override
	public long getItemId(int position) {
		return position;
	}

	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		ViewHodler hodler = null;
		if (convertView == null) {
			hodler = new ViewHodler();
			convertView = LayoutInflater.from(mContext).inflate(
					R.layout.album_grid_view_item, parent,false);

			hodler.iv_image = (ImageView) convertView
					.findViewById(R.id.icon_iv);

			convertView.setTag(hodler);
		} else {
			hodler = (ViewHodler) convertView.getTag();
		}
		LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, (parent.getHeight() - headHeight)/ 3);
		convertView.setLayoutParams(params);

		hodler.iv_image.setImageResource(images[position % images.length]);

		if (position == mLimited - 1) {
			if (isHide) {
				hodler.iv_image.setImageResource(R.drawable.btn_album12_selector);
			} else {
				hodler.iv_image.setImageResource(R.drawable.btn_album21_selector);
			}
			convertView.setOnClickListener(listener);
		}else{
			convertView.setClickable(false);
		}
		return convertView;
	}
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//listerner.onLoadMore(isHide);
			if(isHide){
				isHide = false;
				mLimited = list.size() + 1;
			}else{
				isHide = true;
				mLimited = Math.min(DEF_NUM, list.size());
			}
			notifyDataSetChanged();
		}
	};

	static class ViewHodler {
		ImageView iv_image;
	}

	public boolean isHide() {
		return isHide;
	}

	public void setHide(boolean isHide) {
		this.isHide = isHide;
	}

}
