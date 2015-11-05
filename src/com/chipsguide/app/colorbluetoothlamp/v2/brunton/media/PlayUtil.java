package com.chipsguide.app.colorbluetoothlamp.v2.brunton.media;

import com.actions.ibluz.manager.BluzManagerData.LoopMode;
import com.chipsguide.app.colorbluetoothlamp.v2.brunton.R;
import com.platomix.lib.playerengine.api.PlaybackMode;

public class PlayUtil {
	private static int [] playModeImgs = {R.drawable.selector_btn_order,
			R.drawable.selector_btn_shuffle,
			R.drawable.selector_btn_repeate};
	private static int currentIndex;
	
	
	public static void setCurrentModeIndex(int index){
		currentIndex = index;
	}
	
	public static void setCurrentMode(PlaybackMode mode){
		currentIndex = getModeIndexInModeImgs(mode);
	}
	
	public static int getCurrentModeIndex() {
		return currentIndex;
	}
	
	public static int getCurrentModeRes() {
		return playModeImgs[currentIndex % playModeImgs.length];
	}
	
	/**
	 * 根据播放模式，获取相应播放模式的图片在数组中的位置
	 * @param mode
	 * @return
	 */
	private static int getModeIndexInModeImgs(PlaybackMode mode) {
		if(mode == null){
			return 0;
		}
		int index = 0;
		switch(mode){
		case SHUFFLE:
			index = 1;
			break;
		case ALL:
			index = 0;
			break;
		case SINGLE_REPEAT:
			index = 2;
			break;
		}
		return index;
	}
	/**
	 * 根据播放模式，获取图片id
	 * @param mode
	 * @return
	 */
	public static int getModeImgRes(PlaybackMode mode){
		currentIndex = getModeIndexInModeImgs(mode);
		return playModeImgs[currentIndex];
	}
	/**
	 * 下一个资源id
	 * @return
	 */
	public static int nextModeRes(){
		return playModeImgs[++currentIndex % playModeImgs.length];
	}
	/**
	 * 根据资源id，获取播放模式
	 * @param res
	 * @return
	 */
	public static PlaybackMode getModeWithRes(int res){
		int index = 0;
		for(int i = 0 ; i < playModeImgs.length ; i++){
			if(res == playModeImgs[i]){
				index = i;
			}
		}
		return getModeWithIndex(index);
	}
	/**
	 * 根据资源数组的位置，获取播放模式
	 * @param index
	 * @return
	 */
	public static PlaybackMode getModeWithIndex(int index){
		PlaybackMode mode = null;
		switch(index){
		case 0:
			mode = PlaybackMode.ALL;
			break;
		case 1:
			mode = PlaybackMode.SHUFFLE;
			break;
		case 2:
			mode = PlaybackMode.SINGLE_REPEAT;
			break;
		}
		return mode;
	}
	
	public static int getCurrentModeTextRes(){
		int res = R.string.order;
		switch(currentIndex % playModeImgs.length){
		case 0:
			res = R.string.order;
			break;
		case 1:
			res = R.string.shuffle;
			break;
		case 2:
			res = R.string.single_reapet;
			break;
		}
		return res;

	}
	/**
	 * 将本地播放模式转换为蓝牙播放模式
	 * @param mode
	 * @return
	 */
	public static int convertMode(PlaybackMode mode) {
		int loopMode = LoopMode.ALL;
		switch (mode) {
		case ALL:
			loopMode = LoopMode.ALL;
			break;
		case SHUFFLE:
			loopMode = LoopMode.SHUFFLE;
			break;
		case SINGLE_REPEAT:
			loopMode = LoopMode.SINGLE;
			break;
		}
		return loopMode;
	}
	/**
	 * 将蓝牙播放模式转换为本地
	 * @param loopMode
	 * @return
	 */
	public static PlaybackMode convertMode(int loopMode){
		PlaybackMode mode = PlaybackMode.ALL;
		switch (loopMode) {
		case LoopMode.ALL:
			break;
		case LoopMode.SHUFFLE:
			mode = PlaybackMode.SHUFFLE;
			break;
		case LoopMode.SINGLE:
			mode = PlaybackMode.SINGLE_REPEAT;
			break;
		}
		return mode;
	}
}
