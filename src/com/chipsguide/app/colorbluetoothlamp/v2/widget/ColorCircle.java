package com.chipsguide.app.colorbluetoothlamp.v2.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.util.AttributeSet;
import android.view.View;

public class ColorCircle extends View {
	private static final int DEF_COLOR = Color.WHITE;
	private int color = DEF_COLOR;
	private Paint wheelPaint;
	private Paint strokePaint;

	public ColorCircle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
	}

	public ColorCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
	}

	public ColorCircle(Context context) {
		super(context);
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		
	}
	
	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
	}
	
	public void setColorRes(int colorRes) {
		color = getContext().getResources().getColor(colorRes);
		invalidate();
	}
	
	public void setColor(int color) {
		this.color = color;
		invalidate();
	}
}
