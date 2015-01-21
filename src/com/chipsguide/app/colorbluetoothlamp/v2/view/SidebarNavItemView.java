package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

public class SidebarNavItemView extends FrameLayout {
	private ImageView lineUpIv, lineDownIv, dotIv;
	private TextView navTv;
	
	public SidebarNavItemView(Context context) {
		super(context);
		init();
	}
	
	public SidebarNavItemView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.nav_list_item, this);
		lineUpIv = (ImageView) findViewById(R.id.iv_line_up);
		lineDownIv = (ImageView) findViewById(R.id.iv_line_down);
		dotIv = (ImageView) findViewById(R.id.iv_dot);
		navTv = (TextView) findViewById(R.id.item_title_tv);
	}
	
	public void render(String text, int type) {
		int upRes = R.drawable.line_up;
		int downRes = R.drawable.line_down;
		switch(type){
		case 0: //第一个
			downRes = R.drawable.line_middle;
			break;
		case 1: //中间
			downRes = R.drawable.line_middle;
			upRes = R.drawable.line_middle;
			break;
		case 2: //最后
			upRes = R.drawable.line_middle;
			break;
		}
		lineUpIv.setImageResource(upRes);
		lineDownIv.setImageResource(downRes);
		navTv.setText(text);
	}
	
	public void setSelect(boolean select) {
		dotIv.setImageResource(select ? R.drawable.dot_sidebar_active : R.drawable.dot_sidebar_inactive);
	}
	
}
