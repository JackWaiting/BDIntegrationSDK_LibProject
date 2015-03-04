package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.media;

import com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.media.PlayerManager.PlayType;

public abstract class PlayListener {
	public abstract void onMusicStart(String musicSymbol);
	public abstract void onMusicPause(String musicSymbol);
	public abstract void onMusicProgress(String musicSymbol, long duration, long currentDuration, int percent);
	public abstract void onMusicChange(String musicSymbol);
	public void onMusicBuffering(String musicSymbol, int percent){
	}
	public abstract void onMusicError(String musicSymbol,int what ,int errorCode);
	
	public void onLoopModeChanged(int mode){
	}
	
	public void onPlayTypeChange(PlayType oldType, PlayType newType) {
	}
}
