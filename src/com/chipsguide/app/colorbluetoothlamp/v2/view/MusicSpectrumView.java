package com.chipsguide.app.colorbluetoothlamp.v2.view;

import com.chipsguide.app.colorbluetoothlamp.v2.R;

import android.content.Context;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

public class MusicSpectrumView extends FrameLayout {
	public MusicSpectrumView(Context context) {
		super(context);
		init();
	}

	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_spectrum, this);
	}
}
