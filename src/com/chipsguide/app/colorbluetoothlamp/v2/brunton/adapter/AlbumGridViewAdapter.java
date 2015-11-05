package com.chipsguide.app.colorbluetoothlamp.v2.brunton.adapter;

import java.util.ArrayList;
import java.util.List;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.AbsListView.LayoutParams;
import android.widget.BaseAdapter;
import android.widget.ImageView;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.been.Column;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.utils.PixelUtil;

public class AlbumGridViewAdapter extends BaseAdapter {
	private static final int DEF_NUM = 9;
	
	private Context mContext;
	private List<Column> mList = new ArrayList<Column>();

	private boolean isHide = true;
	private int mLimited = 0;
	private String[] ximalaya_codes;

	public AlbumGridViewAdapter(Context mContext) {
		this.mContext = mContext;
		ximalaya_codes = mContext.getResources().getStringArray(R.array.ximalaya_code);
	}
	
	public void setList(List<Column> list) {
		this.mList.clear();
		int size = list.size();
		int len = ximalaya_codes.length;
		
		for(int j = 0 ; j < len ; j++){ //按特定顺序排列
			Column c = null;
			for (int i = 0; i < size; i++) {
				c = list.get(i);
				String code = c.getCode().toLowerCase();
				if(TextUtils.equals(ximalaya_codes[j], code)){
					this.mList.add(c);
					break;
				}
			}
		}
		if(mList.size() < size){
			list.removeAll(mList);
			mList.addAll(list);
		}
		this.mLimited = Math.min(DEF_NUM, mList.size());
		this.notifyDataSetChanged();
	}
	
	@Override
	public int getCount() {
		return mLimited;
	}

	@Override
	public Column getItem(int position) {
		return mList.get(position);
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
			int paddingSum = PixelUtil.dp2px(20, mContext);
			LayoutParams params = new LayoutParams(LayoutParams.MATCH_PARENT, parent.getHeight()/3 - paddingSum);
			convertView.setLayoutParams(params);
			hodler.iv_image = (ImageView) convertView
					.findViewById(R.id.icon_iv);

			convertView.setTag(hodler);
		} else {
			hodler = (ViewHodler) convertView.getTag();
		}
		

		if (position == mLimited-1) {
			if (isHide) {
				hodler.iv_image.setImageResource(R.drawable.btn_album12_selector);
			} else {
				hodler.iv_image.setImageResource(R.drawable.btn_album21_selector);
			}
			convertView.setOnClickListener(listener);
		}else{
			convertView.setClickable(false);
			Column column = getItem(position);
			convertView.setTag(R.id.attach_data, column);
			String code = column.getCode();
			if(!TextUtils.isEmpty(code)){
				int resId = mContext.getResources().getIdentifier(code.toLowerCase(),"drawable", mContext.getPackageName());
				if(resId != 0){
					hodler.iv_image.setImageResource(resId);
				}else{
					hodler.iv_image.setImageResource(R.drawable.ximalaya_qita);
				}
			}
		}
		return convertView;
	}
	
	private OnClickListener listener = new OnClickListener() {
		@Override
		public void onClick(View arg0) {
			//listerner.onLoadMore(isHide);
			if(isHide){
				isHide = false;
				mLimited = mList.size() + 1;
			}else{
				isHide = true;
				mLimited = Math.min(DEF_NUM, mList.size());
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
