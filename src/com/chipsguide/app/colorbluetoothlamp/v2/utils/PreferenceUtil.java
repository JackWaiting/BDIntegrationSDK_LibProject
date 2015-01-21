package com.chipsguide.app.colorbluetoothlamp.v2.utils;


import android.content.Context;
import android.content.SharedPreferences;

public class PreferenceUtil {

	private static String PLAY_MODE = "play_mode";
	private static String FM_FREQUENCY = "fm_frequency";
	private static String PHONE_MUSIC_POSITION = "phone_music_position";
	private static String PHONE_MUSIC_CURRENT_DURATION = "phone_music_current_duration";
	private static SharedPreferences sp;
	private static PreferenceUtil settingPrefences;

	private PreferenceUtil(Context context) {
		sp = context.getSharedPreferences("config", Context.MODE_PRIVATE);
	}

	public static PreferenceUtil getIntance(Context context) {
		if (null == settingPrefences) {
			settingPrefences = new PreferenceUtil(context);
		}
		return settingPrefences;
	}

	public void savePlayMode(int mode) {
		sp.edit().putInt(PLAY_MODE, mode).commit();
	}
	
	public int getPlayMode() {
		return sp.getInt(PLAY_MODE,0);
	}
	
	public void saveFmFrequency(float frequency){
		sp.edit().putFloat(FM_FREQUENCY, frequency).commit();
	}
	
	public float getFmFrequency(){
		return sp.getFloat(FM_FREQUENCY,87.5f);
	}
	/**
	 * 手机音乐播放的位置
	 * @param position
	 */
	public void savePhoneMusicPosition(int position){
		sp.edit().putInt(PHONE_MUSIC_POSITION, Math.max(0, position)).commit();
	}
	/*
	 * 获取上次播放位置
	 */
	public int getPhoneMusicPosition() {
		return sp.getInt(PHONE_MUSIC_POSITION, -1);
	}
	/**
	 * 保存歌曲的播放时间
	 * @param duration
	 */
	public void savePhoneMusicCurrentDuration(int duration){
		sp.edit().putInt(PHONE_MUSIC_CURRENT_DURATION, Math.max(0, duration)).commit();
	}
	
	public int getPhoneMusicCurrentDuration() {
		return sp.getInt(PHONE_MUSIC_CURRENT_DURATION, 0);
	}
}
