package com.chipsguide.app.colorbluetoothlamp.v2.brunton.widget;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.drawable.ColorDrawable;
import android.util.AttributeSet;
import android.widget.RadioButton;

import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;

public class ColorCircle extends RadioButton{
	private static final int DEF_COLOR = Color.GRAY;
	private static final int DEF_RADIUS = 30;
	private static final int STROKE_WIDTH = 1;
	private float spaceRatio = 0.2f;
	private int radius = DEF_RADIUS;
	private Paint wheelPaint;
	private Paint strokePaint;
	private boolean checked;
	private int strokeColor = DEF_COLOR;
	private int wheelColor = DEF_COLOR;

	public ColorCircle(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		initTypedArray(context, attrs);
		init();
	}

	public ColorCircle(Context context, AttributeSet attrs) {
		super(context, attrs);
		initTypedArray(context, attrs);
		init();
	}

	private void initTypedArray(Context context, AttributeSet attrs) {
		TypedArray a = context.obtainStyledAttributes(attrs, R.styleable.ColorCircle);
		strokeColor = a.getColor(R.styleable.ColorCircle_stroke_color, DEF_COLOR);
		wheelColor = a.getColor(R.styleable.ColorCircle_wheelColor, DEF_COLOR);
		a.recycle();
	}
	
	public ColorCircle(Context context) {
		super(context);
		init();
	}

	private void init() {
		setButtonDrawable(new ColorDrawable(Color.TRANSPARENT));
		//需要设置背景，否则在某些机型上不显示
		setBackgroundColor(Color.TRANSPARENT);
		wheelPaint = new Paint();
		wheelPaint.setAntiAlias(true);
		wheelPaint.setColor(wheelColor);
		wheelPaint.setStyle(Style.FILL);

		strokePaint = new Paint();
		strokePaint.setAntiAlias(true);
		strokePaint.setStyle(Style.STROKE);
		strokePaint.setColor(strokeColor);
		strokePaint.setStrokeWidth(STROKE_WIDTH);
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		int widthMode = MeasureSpec.getMode(widthMeasureSpec);
		int heightMode = MeasureSpec.getMode(heightMeasureSpec);
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		widthSize = getDesiredWidth(widthSize, widthMode);
		heightSize = getDesiredHeight(heightSize, heightMode);
		setMeasuredDimension(widthSize, heightSize);
		radius = getDesiredRadius(widthSize, heightSize);
	}

	private int getDesiredWidth(int widthSize, int widthMode) {
		int result = widthSize;
		if (widthMode != MeasureSpec.EXACTLY) {
			result = 2 * radius + getPaddingLeft() + getPaddingRight();
			if (widthMode == MeasureSpec.AT_MOST) {
				result = Math.min(widthSize, result);
			}
		}
		return result;
	}

	private int getDesiredHeight(int heightSize, int heightMode) {
		int result = heightSize;
		if (heightMode != MeasureSpec.EXACTLY) {
			result = 2 * radius + getPaddingTop() + getPaddingBottom();
			if (heightMode == MeasureSpec.AT_MOST) {
				result = Math.min(heightSize, result);
			}
		}
		return result;
	}

	private int getDesiredRadius(int widthSize, int heightSize) {
		return Math.min(widthSize - getPaddingLeft() - getPaddingRight(),
				heightSize - getPaddingTop() - getPaddingBottom())
				/ 2
				- 2
				* STROKE_WIDTH;
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);
		int cx = getWidth() / 2;
		int cy = getHeight() / 2;
		
		int wheelRadius = radius - STROKE_WIDTH;
		if(checked){
			wheelRadius = (int) ((1 - spaceRatio) * radius);
		}
		canvas.drawCircle(cx, cy, wheelRadius, wheelPaint);
		canvas.drawCircle(cx, cy, wheelRadius, strokePaint);
		canvas.drawCircle(cx, cy, radius, strokePaint);
	}

	public void setRadius(int radius) {
		this.radius = radius;
		requestLayout();
	}

	public void setWheelColorRes(int colorRes) {
		int color = getContext().getResources().getColor(colorRes);
		setWheelColor(color);
	}

	public void setWheelColor(int color) {
		wheelColor = color;
		wheelPaint.setColor(color);
		invalidate();
	}
	
	public int getWheelColor() {
		return wheelColor;
	}

	public void setStrokeColoRes(int colorRes) {
		int color = getContext().getResources().getColor(colorRes);
		setStrokeColor(color);
	}

	public void setStrokeColor(int color) {
		strokeColor = color;
		strokePaint.setColor(color);
		invalidate();
	}

	@Override
	public void setChecked(boolean checked) {
		super.setChecked(checked);
		this.checked = checked;
		invalidate();
	}
}
