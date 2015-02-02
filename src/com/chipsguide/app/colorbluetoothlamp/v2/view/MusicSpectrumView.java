package com.chipsguide.app.colorbluetoothlamp.v2.view;

import android.content.Context;
import android.media.audiofx.Visualizer;
import android.view.LayoutInflater;
import android.widget.FrameLayout;

import com.chipsguide.app.colorbluetoothlamp.v2.R;
import com.chipsguide.app.colorbluetoothlamp.v2.widget.VisualizerView;

public class MusicSpectrumView extends FrameLayout {
	private Visualizer mVisualizer;
	private VisualizerView mVisualizerView;
	private int sessionId;
	
	public MusicSpectrumView(Context context) {
		super(context);
		init();
	}

	public void setAudioSessionId(int sessionId) {
		this.sessionId = sessionId;
		if(sessionId == -1 ){
			return;
		}
		setupVisualizerFxAndUI();
	}
	
	private void init() {
		LayoutInflater.from(getContext()).inflate(R.layout.layout_spectrum,
				this);
		mVisualizerView = (VisualizerView) findViewById(R.id.visualizerView);
	}

	private void setupVisualizerFxAndUI() {
		final int maxCR = Visualizer.getMaxCaptureRate();
		if(mVisualizer != null){
			mVisualizer.setEnabled(false);
			mVisualizer.release();
		}
		mVisualizer = new Visualizer(sessionId);
		mVisualizer.setEnabled(false);
		mVisualizer.setCaptureSize(32);
		mVisualizer.setDataCaptureListener(
				new Visualizer.OnDataCaptureListener() {
					public void onWaveFormDataCapture(Visualizer visualizer,
							byte[] bytes, int samplingRate) {
						mVisualizerView.updateVisualizer(bytes);
					}

					public void onFftDataCapture(Visualizer visualizer,
							byte[] fft, int samplingRate) {
						mVisualizerView.updateVisualizer(fft);
					}
				}, maxCR / 2, false, true);
		mVisualizer.setEnabled(true);  
	}
	
	public void setUpdate(boolean update) {
		mVisualizerView.setUpdate(update);
	}
	
	@Override
	protected void onDetachedFromWindow() {
		super.onDetachedFromWindow();
		if(mVisualizer != null){
			mVisualizer.setEnabled(false);
			mVisualizer.release();
		}
	}
}
