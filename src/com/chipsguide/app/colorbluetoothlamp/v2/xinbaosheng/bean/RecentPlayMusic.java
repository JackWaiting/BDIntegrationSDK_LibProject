package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean;

public class RecentPlayMusic extends Music {
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private Music music;
	/**
	 * 最近播放的时间
	 */
	private long play_date;
	private int play_type;
	
	public RecentPlayMusic(){
	}
	
	public RecentPlayMusic(Music music){
		setMusic(music);
	}
	
	public void setMusic(Music music) {
		this.music = music;
		this.setId(music.getId());
		this.setAgename(music.getAgename());
		this.setAlbumCoverpath(music.getAlbumCoverpath());
		this.setAlbumId(music.getAlbumId());
		this.setClassid(music.getClassid());
		this.setClassname(music.getClassname());
		this.setLocalPath(music.getLocalPath());
		this.setName(music.getName());
		this.setName_en(music.getName_en());
		this.setPath(music.getPath());
		this.setPicpath_l(music.getPicpath_l());
		this.setPicpath_m(music.getPicpath_m());
		this.setPicpath_s(music.getPicpath_s());
		this.setSongwordpath(music.getSongwordpath());
		this.setSize(music.getSize());
		this.setTitle(music.getTitle());
		this.setDuration(music.getDuration());
	}
	
	public Music getMusic() {
		return music;
	}
	
	public void setPlay_date(long play_date) {
		this.play_date = play_date;
	}
	
	public long getPlay_date() {
		return play_date;
	}
	
	public void setPlay_type(int play_type) {
		this.play_type = play_type;
	}
	
	public int getPlay_type() {
		return play_type;
	}
}
