package com.chipsguide.app.colorbluetoothlamp.v2.widget;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.Rect;
import android.util.AttributeSet;
import android.view.View;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.utils.PixelUtil;

public class VisualizerView extends View {
	private byte[] mBytes;
	private float[] mPoints;
	private Rect mRect = new Rect();

	private Paint mForePaint = new Paint();
	private int mSpectrumNum = -1;
	private int itemWidth;
	private int itemSpace;

	public VisualizerView(Context context) {
		super(context);
		init();
	}

	public VisualizerView(Context context, AttributeSet attrs, int defStyleAttr) {
		super(context, attrs, defStyleAttr);
		init();
	}

	public VisualizerView(Context context, AttributeSet attrs) {
		super(context, attrs);
		init();
	}

	private void init() {
		itemWidth = PixelUtil.dp2px(5, getContext());
		itemSpace = PixelUtil.dp2px(4, getContext());
		mForePaint.setStrokeWidth(itemWidth);
		mForePaint.setAntiAlias(true);
		mForePaint.setColor(getResources().getColor(R.color.color_blue));
	}
	
	private boolean update = true;
	public void setUpdate(boolean update) {
		this.update = update;
	}

	public void updateVisualizer(byte[] fft) {
		if (!update) {
			return;
		}

		byte[] model = new byte[fft.length / 2 + 1];

		model[0] = (byte) Math.abs(fft[0]);
		for (int i = 2, j = 1; j < mSpectrumNum;) {
			model[j] = (byte) Math.hypot(fft[i], fft[i + 1]);
			i += 2;
			j++;
		}
		mBytes = model;
		invalidate();
	}
	
	@Override
	protected void onMeasure(int widthMeasureSpec, int heightMeasureSpec) {
		super.onMeasure(widthMeasureSpec, heightMeasureSpec);
		mSpectrumNum = getWidth() / (itemWidth + itemSpace);
		mRect.set(0, 0, getWidth(), getHeight()/2);
		mBytes = new byte[mSpectrumNum];
	}

	@Override
	protected void onDraw(Canvas canvas) {
		super.onDraw(canvas);

		if (mBytes == null) {
			return;
		}

		if (mPoints == null || mPoints.length < mBytes.length * 4) {
			mPoints = new float[mBytes.length * 4];
		}

		if(mSpectrumNum < 0){
			mSpectrumNum = getWidth() / (itemWidth + itemSpace);
		}
		// 绘制频谱
		final int height = mRect.height();

		for (int i = 0; i < mSpectrumNum; i++) {
			if (mBytes[i] < 0) {
				mBytes[i] = 127;
			}

			final int xi = (itemSpace) * (i + 1) + itemWidth * i;
			
			int delta = 4*mBytes[i] + 5;
			delta = Math.min(delta, height);
			mPoints[i * 4] = xi;
			mPoints[i * 4 + 1] = height + delta;

			mPoints[i * 4 + 2] = xi;
			mPoints[i * 4 + 3] = height - delta;
		}

		canvas.drawLines(mPoints, mForePaint);
	}
	
}
