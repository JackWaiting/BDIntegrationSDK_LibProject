package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.widget.RadioGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorCircle;

public class ColorSelectLayout extends RadioGroup {
	private int [] colorsRes;
	public interface OnColorCheckedChangeListener{
		void onColorChecked(int checkedColor, String colorStr);
	}
	
	public ColorSelectLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColorSelectLayout(Context context) {
		super(context);
	}
	
	public void setColorsRes(int [] colorsRes) {
		this.colorsRes = colorsRes;
		LayoutInflater inflater = LayoutInflater.from(getContext());
		int size = colorsRes.length;
		for (int i = 0; i < size; i++) {
			inflater.inflate(R.layout.color_layout, this);
			ColorCircle cc = (ColorCircle) findViewById(R.id.colorCircle);
			int mColor = getResources().getColor(colorsRes[i]);
			cc.setWheelColor(mColor);
			cc.setId(i);
		}
		check(0);
	}
	
	public void checkColor(int color) {
		int size = colorsRes.length;
		for (int i = 0; i < size; i++) {
			int mColor = getResources().getColor(colorsRes[i]);
			if(color == mColor){
				check(i);
				return;
			}
		}
		check(0);
	}
	
	public void setOnColorCheckedChangeListener(final OnColorCheckedChangeListener listener) {
		this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				if(colorsRes != null){
					int color = getResources().getColor(colorsRes[checkedId]);
					if(listener != null){
						listener.onColorChecked(color, colorToString(color));
					}
				}
			}
		});
	}
	
	private String colorToString(int color) {
		String alpha = Integer.toHexString(Color.alpha(color));
		String red = Integer.toHexString(Color.red(color));
		String green = Integer.toHexString(Color.green(color));
		String blue = Integer.toHexString(Color.blue(color));
		return "#" + alpha + red + green + blue;
	}
}
