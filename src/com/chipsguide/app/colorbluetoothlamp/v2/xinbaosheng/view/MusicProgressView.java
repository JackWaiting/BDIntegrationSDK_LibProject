package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.view;

import android.content.Context;
import android.text.TextUtils;
import android.view.LayoutInflater;
import android.view.animation.LinearInterpolator;
import android.widget.FrameLayout;
import android.widget.ImageView;
import android.widget.TextView;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.utils.StringFormatUtil;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.utils.WrapImageLoader;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.widget.SeekArc;
import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.widget.SeekArc.OnSeekArcChangeListener;
import com.nineoldandroids.animation.ValueAnimator;
import com.nineoldandroids.animation.ValueAnimator.AnimatorUpdateListener;
import com.nineoldandroids.view.ViewHelper;
import com.nostra13.universalimageloader.core.DisplayImageOptions;

public class MusicProgressView extends FrameLayout implements OnSeekArcChangeListener{
	private SeekArc progressSeekBar;
	private ImageView musicIv;
	private TextView musicDurationTv;
	private WrapImageLoader imageLoader;
	private DisplayImageOptions options;
	private static float currentDegree;
	private boolean onTrackingTouch;
	private long duration;

	public static abstract class SimpleSeekArcChangeListener implements OnSeekArcChangeListener{
		@Override
		public void onProgressChanged(SeekArc seekArc, int progress,
				boolean fromUser) {
		}

		@Override
		public void onStartTrackingTouch(SeekArc seekArc) {
		}

		@Override
		public void onStopTrackingTouch(SeekArc seekArc) {
		}
	}
	
	public MusicProgressView(Context context) {
		super(context);
		init();
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(animator != null){
			animator.removeAllUpdateListeners();
			animator.end();
			currentDegree = ViewHelper.getRotation(musicIv);
		}
	}

	private void init() {
		imageLoader = WrapImageLoader.getInstance(getContext());
		options = WrapImageLoader.buildDisplayImageOptions(R.drawable.img_record);
		LayoutInflater.from(getContext()).inflate(
				R.layout.layout_player_progress, this);
		progressSeekBar = (SeekArc) this.findViewById(R.id.seekArc);
		musicIv = (ImageView) findViewById(R.id.iv_music_img);
		musicDurationTv = (TextView) findViewById(R.id.tv_duration);
		progressSeekBar.setOnSeekArcChangeListener(this);
		ViewHelper.setRotation(musicIv, currentDegree);
	}
	
	public void setSeekable(boolean seek) {
		if(!seek){
			progressSeekBar.setThumbVisibility(false);
			progressSeekBar.setSeekable(false);
		}
	}

	public void updateProgress(long duration, long currentDuration, int percent) {
		this.duration = duration;
		if(!onTrackingTouch){
			String currentDurationStr = StringFormatUtil.formatDuration(currentDuration);
			String durationStr = StringFormatUtil.formatDuration(duration);
			musicDurationTv.setText(currentDurationStr + "/" + durationStr);
			progressSeekBar.setProgress((int)(1000 * percent / 1000f));
		}
	}

	public void updateMusicImage(String url) {
		if(TextUtils.isEmpty(url)){
			return;
		}
		imageLoader.displayImage(options, url, musicIv, 1, null);
	}
	
	public void playStateChange(boolean playing) {
		lpRotateAnim(playing);
	}
	
	private OnSeekArcChangeListener seekArcListener;
	public void setOnSeekArcChangeListener(OnSeekArcChangeListener listener) {
		seekArcListener = listener;
	}
	
	
	private ValueAnimator animator;
	private boolean animStart;
	/**
	 * 胶片旋转动画
	 * 
	 * @param start
	 */
	private void lpRotateAnim(boolean start) {
		if (start && !animStart) {
			animStart = true;
			if (animator != null) {
				animator.removeAllUpdateListeners();
			}
			animator = ValueAnimator.ofFloat(0, 360);
			animator.setDuration(10000);
			animator.setRepeatMode(ValueAnimator.RESTART);
			animator.setRepeatCount(ValueAnimator.INFINITE);
			animator.setInterpolator(new LinearInterpolator());
			animator.addUpdateListener(new AnimatorUpdateListener() {
				@Override
				public void onAnimationUpdate(ValueAnimator animator) {
					float degree = (Float) animator.getAnimatedValue();
					if (animator.isRunning()) {
						ViewHelper.setRotation(musicIv, degree
								+ currentDegree);
					}
				}
			});
			animator.start();
		} else {
			animStart = false;
			if (animator != null) {
				animator.removeAllUpdateListeners();
				animator.end();
				currentDegree = ViewHelper.getRotation(musicIv);
			}
		}
	}
	
	public boolean isRotatingAnim(){
		return animStart;
	}

	@Override
	public void onProgressChanged(SeekArc seekArc, int progress,
			boolean fromUser) {
		if(fromUser){
			String currentDurationStr = StringFormatUtil.formatDuration(duration*progress / seekArc.getMaxProgress());
			String durationStr = StringFormatUtil.formatDuration(duration);
			musicDurationTv.setText(currentDurationStr + "/" + durationStr);
		}
		if(seekArcListener != null){
			seekArcListener.onProgressChanged(seekArc, progress, fromUser);
		}
	}

	@Override
	public void onStartTrackingTouch(SeekArc seekArc) {
		onTrackingTouch = true;
		if(seekArcListener != null){
			seekArcListener.onStartTrackingTouch(seekArc);
		}
	}

	@Override
	public void onStopTrackingTouch(SeekArc seekArc) {
		onTrackingTouch = false;
		if(seekArcListener != null){
			seekArcListener.onStopTrackingTouch(seekArc);
		}
	}
}
