/*
 * Copyright 2013 Piotr Adamus
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */

package com.chipsguide.app.colorbluetoothlamp.v2.widget;

import android.annotation.SuppressLint;
import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Bitmap.Config;
import android.graphics.Canvas;
import android.graphics.Color;
import android.graphics.ComposeShader;
import android.graphics.Matrix;
import android.graphics.Paint;
import android.graphics.Paint.Style;
import android.graphics.Path;
import android.graphics.PorterDuff;
import android.graphics.RadialGradient;
import android.graphics.RectF;
import android.graphics.Shader.TileMode;
import android.graphics.SweepGradient;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.view.MotionEvent;
import android.view.View;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.MyLogger;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PixelUtil;

public class ColorPicker extends View {
	MyLogger flog = MyLogger.fLog();
	/**
	 * Customizable display parameters (in percents)
	 */
	private final int paramOuterPadding = 3; // 弧形的外边距占控件的百分比
	private final int paramInnerPadding = 7; // 弧形内边距占控件的百分比
	private final int paramValueSliderWidth = 2; // width of the value slider 滑动条
	private static final int MAX = 165; //底部进度条最大值

	private Paint colorWheelPaint;//绘制色盘
	private Paint valueSliderPaint;//绘制拖动条
	private Paint bottommSliderPaint;

	private Paint colorViewPaint;//绘制颜色

	private Paint colorPointerPaint;//绘制颜色指针
	private RectF colorPointerCoords;//绘制颜色指针坐标

	private Paint valuePointerPaint;//绘制指针的值

	private RectF outerWheelRect;//外弧形矩形
	private RectF innerWheelRect;//内弧形矩形

	private Path valueSliderPath;//拖动条
	private Path bottomSliderPath;

	private Bitmap colorWheelBitmap;//色盘位图

	private int valueSliderWidth;//拖动条长
	private int innerPadding;//边距
	private int outerPadding;

	private int outerWheelRadius;//半径
	private int innerWheelRadius;
	private int colorWheelRadius;
	private int padding;

	private Matrix gradientRotationMatrix;//渐变旋转矩阵

	private Drawable mThumb, secondThumb, sun, moon;
	private int mThumbXPos, mThumbYPos;
	private int secondThumbXPos, secondThumbYPos;
	private float thumbRadius;//滑动的半径
	
	/** Currently selected color */
	private float[] colorHSV = new float[] { 0f, 0f, 1f };

	public ColorPicker(Context context, AttributeSet attrs, int defStyle) {
		super(context, attrs, defStyle);
		init();
	}

	public ColorPicker(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	public ColorPicker(Context context) {
		super(context);
		init();
	}

	private int offset; //弧形两侧的图片距离弧形底部的距离
	private void init() {
		offset = PixelUtil.dp2px(25, getContext());
		
		colorPointerPaint = new Paint();
		colorPointerPaint.setStyle(Style.FILL);
		colorPointerPaint.setARGB(200, 255, 255, 255);

		valuePointerPaint = new Paint();
		valuePointerPaint.setStyle(Style.STROKE);
		valuePointerPaint.setStrokeWidth(2f);

		colorWheelPaint = new Paint();
		colorWheelPaint.setAntiAlias(true);
		colorWheelPaint.setDither(true);

		valueSliderPaint = new Paint();
		valueSliderPaint.setAntiAlias(true);
		valueSliderPaint.setDither(true);
		
		bottommSliderPaint = new Paint();
		bottommSliderPaint.setAntiAlias(true);
		bottommSliderPaint.setDither(true);
		bottommSliderPaint.setColor(Color.RED);

		colorViewPaint = new Paint();
		colorViewPaint.setAntiAlias(true);

		valueSliderPath = new Path();
		bottomSliderPath = new Path();

		outerWheelRect = new RectF();
		innerWheelRect = new RectF();

		colorPointerCoords = new RectF();

		mThumb = getResources().getDrawable(R.drawable.thumb);
		int thumbHalfheight = (int) mThumb.getIntrinsicHeight() / 2;
		int thumbHalfWidth = (int) mThumb.getIntrinsicWidth() / 2;
		mThumb.setBounds(-thumbHalfWidth, -thumbHalfheight, thumbHalfWidth,
				thumbHalfheight);
		secondThumb = mThumb;

//		sun = getResources().getDrawable(R.drawable.ic_bright);
//		int sunHalfHeight = (int) sun.getIntrinsicHeight() / 2;
//		int sunHalfWidth = (int) sun.getIntrinsicWidth() / 2;
//		sun.setBounds(-sunHalfWidth, -sunHalfHeight, sunHalfWidth,
//				sunHalfHeight);
//
//		moon = getResources().getDrawable(R.drawable.ic_dark);
//		int moonHalfHeight = (int) moon.getIntrinsicHeight() / 2;
//		int moonHalfWidth = (int) moon.getIntrinsicWidth() / 2;
//		moon.setBounds(-moonHalfWidth, -moonHalfHeight, moonHalfWidth,
//				moonHalfHeight);

		padding = thumbHalfWidth;
	}

	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		int widthSize = MeasureSpec.getSize(widthMeasureSpec);
		int heightSize = MeasureSpec.getSize(heightMeasureSpec);
		int size = Math.min(widthSize, heightSize);
		setMeasuredDimension(size, size);
		radius();
	}

	@Override
	protected void onDraw(Canvas canvas) {
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;

		// drawing color wheel

		canvas.drawBitmap(colorWheelBitmap, centerX - colorWheelRadius, centerY
				- colorWheelRadius, null);

		// drawing color view

		// colorViewPaint.setColor(Color.HSVToColor(colorHSV));
		// canvas.drawPath(colorViewPath, colorViewPaint);

		// drawing value slider
		canvas.drawPath(valueSliderPath, valueSliderPaint);
		if(isSecondProgressVisible){
			canvas.drawPath(bottomSliderPath, bottommSliderPaint);
		}

		// drawing color wheel pointer

		float hueAngle = (float) Math.toRadians(colorHSV[0]);
		int colorPointX = (int) (-Math.cos(hueAngle) * colorHSV[1] * colorWheelRadius)
				+ centerX;
		int colorPointY = (int) (-Math.sin(hueAngle) * colorHSV[1] * colorWheelRadius)
				+ centerY;

		float pointerRadius = 0.1f * colorWheelRadius;
		int pointerX = (int) (colorPointX - pointerRadius / 2);
		int pointerY = (int) (colorPointY - pointerRadius / 2);

		colorPointerCoords.set(pointerX, pointerY, pointerX + pointerRadius,
				pointerY + pointerRadius);
		canvas.drawOval(colorPointerCoords, colorPointerPaint);

		drawDrawable(canvas);

		if (mColorChangelistener != null) {
			int color = Color.HSVToColor(colorHSV);
			//int alpha = Color.alpha(color);
			int red = Color.red(color);
			int green = Color.green(color);
			int blue = Color.blue(color);
			mColorChangelistener.onColorChange(red, green, blue);
		}
	}

	private void drawDrawable(Canvas canvas) {
//		canvas.save();
//		canvas.translate(getWidth() / 2 + thumbRadius, getHeight() / 2 + offset);
//		sun.draw(canvas);
//		canvas.restore();

//		canvas.save();
//		canvas.translate(getWidth() / 2 - thumbRadius, getHeight() / 2 + offset);
//		moon.draw(canvas);
//		canvas.restore();

		canvas.save();
		canvas.translate(getWidth() / 2 - mThumbXPos, getHeight() / 2
				- mThumbYPos);
		mThumb.draw(canvas);
		canvas.restore();
		
		if(isSecondProgressVisible){
			canvas.save();
			canvas.translate(getWidth() / 2 + secondThumbXPos, getHeight() / 2
					+ secondThumbYPos);
			secondThumb.draw(canvas);
			canvas.restore();
		}
	}

	private void updateThumbPosition() {
		double thumbAngle = (colorHSV[2]) * Math.PI;
		double tipAngleX = Math.cos(thumbAngle) * thumbRadius;
		double tipAngleY = Math.sin(thumbAngle) * thumbRadius;
		mThumbXPos = (int) tipAngleX;
		mThumbYPos = (int) tipAngleY;
	}
	
	private void updateSecondThumbPosition(int x, int y){
		float centerX = getWidth() / 2;
		double arc = Math.atan2(Math.abs(getHeight() / 2 - y), Math.abs(getWidth() / 2 - x));
		if(x <= centerX){
			arc = Math.PI - arc;
		}
		updateSecondThumbPosition(arc, true);
	}
	
	private void updateSecondThumbPosition(double radians, boolean fromUser){
		radians = Math.min(Math.toRadians(165f), radians);
		radians = Math.max(Math.toRadians(15f), radians);
		secondArcRadians = radians;
		
		double tipAngleX = Math.cos(radians) * thumbRadius;
		double tipAngleY = Math.sin(radians) * thumbRadius;
		secondThumbXPos = (int) tipAngleX;
		secondThumbYPos = (int) tipAngleY;
		if(mSecondArcChangeListener != null){
			mSecondArcChangeListener.onArcChanged(this, getSecondProgress(), fromUser);
		}
		postInvalidate();
	}

	private static final int SECOND_ARC_START_ANGLE = 15;
	private static final int SECOND_ARC_SWEEP_ANGLE = 150;
	@Override
	protected void onSizeChanged(int width, int height, int oldw, int oldh) {
		int centerX = width / 2;
		int centerY = height / 2;
		innerPadding = (int) (paramInnerPadding * width / 100);
		outerPadding = (int) (paramOuterPadding * width / 100);
		valueSliderWidth = (int) (paramValueSliderWidth * width / 100);

		outerWheelRadius = centerX - outerPadding - padding;
		innerWheelRadius = outerWheelRadius - valueSliderWidth;
		colorWheelRadius = innerWheelRadius - innerPadding;

		outerWheelRect.set(centerX - outerWheelRadius, centerY
				- outerWheelRadius, centerX + outerWheelRadius, centerY
				+ outerWheelRadius);
		innerWheelRect.set(centerX - innerWheelRadius, centerY
				- innerWheelRadius, centerX + innerWheelRadius, centerY
				+ innerWheelRadius);

		colorWheelBitmap = createColorWheelBitmap(colorWheelRadius * 2,
				colorWheelRadius * 2);

		gradientRotationMatrix = new Matrix();
		gradientRotationMatrix.preRotate(180, centerX, centerY);

		valueSliderPath.reset();
		valueSliderPath.arcTo(outerWheelRect, 180, 180);
		valueSliderPath.arcTo(innerWheelRect, 0, -180);
		
		bottomSliderPath.reset();
		bottomSliderPath.arcTo(outerWheelRect, SECOND_ARC_START_ANGLE, SECOND_ARC_SWEEP_ANGLE);
		bottomSliderPath.arcTo(innerWheelRect, SECOND_ARC_START_ANGLE + SECOND_ARC_SWEEP_ANGLE, -SECOND_ARC_SWEEP_ANGLE);

		minValidateTouchArcRadius = innerWheelRadius - padding;
		maxValidateTouchArcRadius = outerWheelRadius + padding;
		yMaxTouchValidateRange = (centerY + padding);
		secondYMaxTouchValidateRange = (centerY + (int)(Math.sin(SECOND_ARC_START_ANGLE)*thumbRadius));
		
		float[] hsv = new float[] { colorHSV[0], colorHSV[1], 1f };
		sweepGradient = new SweepGradient(centerX, centerY,
				new int[] { Color.BLACK, Color.HSVToColor(hsv), Color.WHITE },
				null);
		sweepGradient.setLocalMatrix(gradientRotationMatrix);
		valueSliderPaint.setShader(sweepGradient);
		
		
		Matrix bottomGradientMatrix = new Matrix();
		bottomGradientMatrix.preRotate(270, centerX, centerY);
		SweepGradient bottomSweepGradient = new SweepGradient(centerX, centerY,
				new int[] {Color.WHITE , Color.parseColor("#fcbe7b")},
				null);
		bottomSweepGradient.setLocalMatrix(bottomGradientMatrix);
		bottommSliderPaint.setShader(bottomSweepGradient);
		
		radius();
	}

	private void radius()
	{
		thumbRadius = (outerWheelRadius - (outerWheelRadius - innerWheelRadius) / 2);
		mThumbXPos = (int) (thumbRadius * Math.cos(Math.toRadians(180)));
		mThumbYPos = (int) (thumbRadius * Math.sin(Math.toRadians(180)));
		
		updateSecondThumbPosition(0, false);
	}

	private Bitmap createColorWheelBitmap(int width, int height) {

		Bitmap bitmap = Bitmap.createBitmap(width, height, Config.ARGB_8888);

		int colorCount = 12;
		int colorAngleStep = 360 / 12;
		int colors[] = new int[colorCount + 1];
		float hsv[] = new float[] { 0f, 1f, 1f };
		for (int i = 0; i < colors.length; i++) {
			hsv[0] = (i * colorAngleStep + 180) % 360;
			colors[i] = Color.HSVToColor(hsv);
		}
		colors[colorCount] = colors[0];

		SweepGradient sweepGradient = new SweepGradient(width / 2, height / 2,
				colors, null);
		RadialGradient radialGradient = new RadialGradient(width / 2,
				height / 2, colorWheelRadius, 0xFFFFFFFF, 0x00FFFFFF,
				TileMode.CLAMP);
		ComposeShader composeShader = new ComposeShader(sweepGradient,
				radialGradient, PorterDuff.Mode.SRC_OVER);

		colorWheelPaint.setShader(composeShader);

		Canvas canvas = new Canvas(bitmap);
		canvas.drawCircle(width / 2, height / 2, colorWheelRadius,
				colorWheelPaint);

		return bitmap;

	}
	

	boolean downOnWheel = false;
	boolean downOnArc = false;
	boolean downOnSecondArc = false;
	@SuppressLint("ClickableViewAccessibility")
	@Override
	public boolean onTouchEvent(MotionEvent event) {
		int action = event.getAction();
		int x = (int) event.getX();
		int y = (int) event.getY();
		switch (action) {
		case MotionEvent.ACTION_DOWN:
			if(isTouchArc(x, y)){
				downOnArc = true;
				updateArc(x, y);
				return true;
			}else if(isTouchColorWheel(x, y)){
				downOnWheel = true;
				updateWheelColor(x, y);
				return true;
			}else if(isTouchSecondArc(x, y)){
				downOnSecondArc = true;
				updateSecondThumbPosition(x, y);
				return true;
			}
			break;
		case MotionEvent.ACTION_MOVE:
			if (downOnWheel && isTouchColorWheel(x, y)) {
				updateWheelColor(x, y);
				return true;
			} else if (downOnArc && y <= yMaxTouchValidateRange) {
				updateArc(x, y);
				return true;
			} else if(downOnSecondArc && y >= secondYMaxTouchValidateRange){
				updateSecondThumbPosition(x, y);
				return true;
			}

			break;
		case MotionEvent.ACTION_UP:
			if(downOnArc || downOnWheel){
				downOnArc = false;
				downOnWheel = false;
				if (mColorChangelistener != null) {
					int color = Color.HSVToColor(colorHSV);
					//int alpha = Color.alpha(color);
					int red = (color & 0xff0000) >> 16;
					int green = (color & 0x00ff00) >> 8;
					int blue = (color & 0x0000ff);
					mColorChangelistener.onColorChangeEnd(red, green, blue);
				}
			}else if(downOnSecondArc){
				downOnSecondArc = false;
				if(mSecondArcChangeListener != null){
					mSecondArcChangeListener.onStopTrackingTouch(this);
				}
			}
			
			break;
		}
		return super.onTouchEvent(event);
	}
	
	/**
	 * 更新色盘
	 * @param x 点击x坐标
	 * @param y 点击y坐标
	 */
	private void updateWheelColor(int x, int y) {
		int cx = x - getWidth() / 2;
		int cy = y - getHeight() / 2;
		double d = Math.hypot(cx, cy);
		colorHSV[0] = (float) (Math.toDegrees(Math.atan2(cy, cx)) + 180f);
		colorHSV[1] = Math.max(0f,
				Math.min(1f, (float) (d / colorWheelRadius)));
		invalidate();
	}
	/**
	 * 更新进度
	 * @param x 点击x坐标
	 * @param y 点击y坐标
	 */
	private void updateArc(int x, int y) {
		int cx = x - getWidth() / 2;
		int cy = y - getHeight() / 2;
		cy = Math.min(0, cy);
		cy = Math.abs(cy);
		colorHSV[2] = (float) Math.max(0,
				Math.min(1, 1 - Math.atan2(cy, cx) / Math.PI));
		updateThumbPosition();
		invalidate();
	}

	private int minValidateTouchArcRadius; // 最小有效点击半径
	private int maxValidateTouchArcRadius; // 最大有效点击半径
	private int yMaxTouchValidateRange; // y轴上点击的有效范围
	private boolean isTouchArc(int x, int y) {
		double d = getTouchRadius(x, y);
		if (y <= yMaxTouchValidateRange && d >= minValidateTouchArcRadius
				&& d <= maxValidateTouchArcRadius) {
			return true;
		}
		return false;
	}
	
	private int secondYMaxTouchValidateRange; // y轴上点击的有效范围
	private boolean isTouchSecondArc(int x, int y) {
		if(isSecondProgressVisible){
			double d = getTouchRadius(x, y);
			if (y >=  secondYMaxTouchValidateRange && d >= minValidateTouchArcRadius
					&& d <= maxValidateTouchArcRadius) {
				return true;
			}
		}
		return false;
	}
	
	private boolean isTouchColorWheel(int x , int y) {
		double d = getTouchRadius(x, y);
		if (d < colorWheelRadius) {
			return true;
		}
		return false;
	}

	private double getTouchRadius(int x, int y) {
		int cx = x - getWidth() / 2;
		int cy = y - getHeight() / 2;
		return Math.hypot(cx, cy);
	}

	public void setColor(int color) {
		Color.colorToHSV(color, colorHSV);
		updateThumbPosition();
		invalidate();
	}

	public int getColor() {
		return Color.HSVToColor(colorHSV);
	}
	/**
	 * 设置亮度0-1
	 * @param brightness
	 */
	public void setBrightness(float brightness,float max) {
		float[] colorHSV = new float[] { 0f, 0f, 1f };
		colorHSV[2] = brightness / max;
		int color = Color.HSVToColor(colorHSV);
		setColor(color);
	}

	private int mMax = MAX;
	/**
	 * 设置底部进度条的最大值
	 */
	public void setMaxProgress(int max) {
		mMax = max;
	}
	
	private double secondArcRadians;
	/**
	 * 更新底部进度条
	 */
	public void setSecondProgress(int progress) {
		progress = Math.min(progress, mMax);
		progress = Math.max(progress, 0);
		float ratio = (float)progress / mMax;
		float degree = ratio * SECOND_ARC_SWEEP_ANGLE + SECOND_ARC_START_ANGLE;
		updateSecondThumbPosition(Math.toRadians(degree) , false);
	}
	
	public int getSecondProgress() {
		double degree = Math.toDegrees(secondArcRadians);
		double ratio = (degree - SECOND_ARC_START_ANGLE) / SECOND_ARC_SWEEP_ANGLE;
		return (int)(ratio * mMax + 0.5f);
	}
	
	private boolean isSecondProgressVisible;
	/**
	 * 设置底部进度条可见性
	 */
	public void setSecondProgressVisibility(boolean visible){
		isSecondProgressVisible = visible;
		invalidate();
	}
	
	@Override
	protected Parcelable onSaveInstanceState() {
		Bundle state = new Bundle();
		state.putFloatArray("color", colorHSV);
		state.putParcelable("super", super.onSaveInstanceState());
		return state;
	}

	@Override
	protected void onRestoreInstanceState(Parcelable state) {
		if (state instanceof Bundle) {
			Bundle bundle = (Bundle) state;
			colorHSV = bundle.getFloatArray("color");
			super.onRestoreInstanceState(bundle.getParcelable("super"));
		} else {
			super.onRestoreInstanceState(state);
		}
	}
	
	SweepGradient sweepGradient;
	@Override
	public void invalidate() {
		int centerX = getWidth() / 2;
		int centerY = getHeight() / 2;
		float[] hsv = new float[] { colorHSV[0], colorHSV[1], 1f };
		sweepGradient = new SweepGradient(centerX, centerY,
				new int[] { Color.BLACK, Color.HSVToColor(hsv), Color.WHITE },
				null);
		
		sweepGradient.setLocalMatrix(gradientRotationMatrix);
		valueSliderPaint.setShader(sweepGradient);
		super.invalidate();
	}
	
	private OnColorChangeListener mColorChangelistener;

	public void setOnColorChangeListener(OnColorChangeListener listener) {
		mColorChangelistener = listener;
	}
	
	private OnSecondArcChangeListener mSecondArcChangeListener;
	/**
	 * 设置底部进度条进度监听
	 * @param listener
	 */
	public void setOnSecondArcListener(OnSecondArcChangeListener listener) {
		mSecondArcChangeListener = listener;
	}

	public interface OnColorChangeListener {
		/**
		 * 颜色改变
		 * @param red
		 * @param green
		 * @param blue
		 */
		void onColorChange(int red, int green, int blue);

		/**
		 * 颜色改变结束回调
		 * @param red
		 * @param green
		 * @param blue
		 */
		void onColorChangeEnd(int red, int green, int blue);
	}
	
	/**
	 * 下边的进度条进度改变监听
	 * @author chiemy
	 *
	 */
	public interface OnSecondArcChangeListener{
		/**
		 * 停止调节
		 * @param picker
		 */
		void onStopTrackingTouch(ColorPicker picker);
		/**
		 * 进度变化
		 * @param picker
		 * @param progress 进度
		 * @param fromUser 是否为用户点击引起的变化
		 */
		void onArcChanged(ColorPicker picker, int progress, boolean fromUser);
	}

}
