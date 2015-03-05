package com.chipsguide.app.colorbluetoothlamp.v2.widget;

import android.content.Context;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.graphics.drawable.GradientDrawable.Orientation;
import android.util.AttributeSet;
import android.widget.SeekBar;

public class LineGradientSeekbar extends SeekBar{

	public LineGradientSeekbar(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
	}

	public LineGradientSeekbar(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public LineGradientSeekbar(Context context) {
		super(context);
	}
	
	public void setColor(int color) {
		int [] colors = new int[] { Color.BLACK, color, Color.WHITE };
		GradientDrawable drawable = new GradientDrawable(Orientation.LEFT_RIGHT, colors);
		drawable.setGradientType(GradientDrawable.LINEAR_GRADIENT);
		drawable.setShape(GradientDrawable.LINE);
		drawable.setBounds(0, 0, getWidth(), getHeight());
		setProgressDrawable(drawable);
	}
	
	
}
