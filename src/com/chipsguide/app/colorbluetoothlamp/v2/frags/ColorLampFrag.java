package com.chipsguide.app.colorbluetoothlamp.v2.frags;

import android.graphics.Color;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorPicker.OnColorChangeListener;

public class ColorLampFrag extends BaseFragment implements OnColorChangeListener{

	@Override
	protected void initBase() {
	}

	@Override
	protected int getLayoutId() {
		return R.layout.frag_color_lamp;
	}

	@Override
	protected void initView() {
		ColorPicker colorPicker = (ColorPicker) findViewById(R.id.colorPicker);
		colorPicker.setOnColorChangeListener(this);
	}

	@Override
	protected void initData() {
	}

	@Override
	public void onColorChange(int alpha, int red, int green, int blue) {
		int color = Color.argb(alpha, red, green, blue);
		findViewById(R.id.layout).setBackgroundColor(color);
	}

	@Override
	public void onColorChangeEnd(int alpha, int red, int green, int blue) {
	}

}
