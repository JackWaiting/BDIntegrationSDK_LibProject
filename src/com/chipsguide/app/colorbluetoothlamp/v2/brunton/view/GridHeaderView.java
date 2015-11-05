package com.chipsguide.app.colorbluetoothlamp.v2.brunton.view;

import android.content.Context;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.LinearLayout;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;

public class GridHeaderView extends LinearLayout  {
	private Context mContext;

	public GridHeaderView(Context context, AttributeSet attrs) {
		super(context, attrs);
		mContext = context;
		init();
	}

	public GridHeaderView(Context context) {
		super(context);
		mContext = context;
		init();
	}

	private void init() {
		LayoutInflater.from(mContext).inflate(R.layout.gridview_header,
				this);
	}
}
