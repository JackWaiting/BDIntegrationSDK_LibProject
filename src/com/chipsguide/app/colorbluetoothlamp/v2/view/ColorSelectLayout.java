package com.chipsguide.app.colorbluetoothlamp.v2.view;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

import android.content.Context;
import android.graphics.Color;
import android.util.AttributeSet;
import android.view.LayoutInflater;
import android.view.View;
import android.widget.RadioGroup;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.ColorCircle;

public class ColorSelectLayout extends RadioGroup {
	private Integer[] colors;

	public interface OnColorCheckedChangeListener {
		void onColorChecked(int checkedColor, String colorStr);
	}

	public ColorSelectLayout(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColorSelectLayout(Context context) {
		super(context);
	}

	public void setColorsRes(int[] colorsRes) {
		this.removeAllViews();
		LayoutInflater inflater = LayoutInflater.from(getContext());
		int size = colorsRes.length;
		this.colors = new Integer[size];
		for (int i = 0; i < size; i++) {
			inflater.inflate(R.layout.color_layout, this);
			ColorCircle cc = (ColorCircle) findViewById(R.id.colorCircle);
			int mColor = getResources().getColor(colorsRes[i]);
			colors[i] = mColor;
			cc.setWheelColor(mColor);
			cc.setId(i);
		}
		check(0);
	}

	public void setColors(Integer[] colors) {
		this.removeAllViews();
		this.colors = colors;
		LayoutInflater inflater = LayoutInflater.from(getContext());
		int size = colors.length;
		for (int i = 0; i < size; i++) {
			inflater.inflate(R.layout.color_layout, this);
			ColorCircle cc = (ColorCircle) findViewById(R.id.colorCircle);
			int mColor = colors[i];
			cc.setWheelColor(mColor);
			cc.setId(i);
		}
		check(0);
	}

	public void addColor(int color) {
		LayoutInflater inflater = LayoutInflater.from(getContext());
		inflater.inflate(R.layout.color_layout, this);
		ColorCircle cc = (ColorCircle) findViewById(R.id.colorCircle);
		cc.setWheelColor(color);
		cc.setId(colors.length);
		
		List<Integer> list = new ArrayList<Integer>(Arrays.asList(colors));
		list.add(color);
		colors = new Integer[list.size()];
		list.toArray(colors);
	}
	
	public Integer[] getColors() {
		return colors;
	}
	
	public void removeColor(int color) {
		int size = colors.length;
		for (int i = 0; i < size; i++) {
			if(color == colors[i]){
				this.removeViewAt(i);
				break;
			}
		}
	}
	
	@Override
	public void removeViewAt(int index) {
		super.removeViewAt(index);
		if(index != -1){
			List<Integer> list = new ArrayList<Integer>(Arrays.asList(colors));
			list.remove(index);
			colors = new Integer[list.size()];
			list.toArray(colors);
		}
	}

	public void checkColor(int color) {
		int size = colors.length;
		for (int i = 0; i < size; i++) {
			int mColor = colors[i];
			if (color == mColor) {
				check(i);
				return;
			}
		}
		check(0);
	}

	public void setOnColorCheckedChangeListener(
			final OnColorCheckedChangeListener listener) {
		this.setOnCheckedChangeListener(new OnCheckedChangeListener() {
			@Override
			public void onCheckedChanged(RadioGroup group, int checkedId) {
				View child = findViewById(checkedId);
				int color = Color.TRANSPARENT;
				if(child instanceof ColorCircle){
					color = ((ColorCircle)child).getWheelColor();
				}
				if (listener != null) {
					listener.onColorChecked(color, colorToString(color));
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
