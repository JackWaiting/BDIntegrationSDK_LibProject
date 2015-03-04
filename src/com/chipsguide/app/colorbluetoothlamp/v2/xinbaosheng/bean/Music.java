package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean;

import java.io.Serializable;

public class Music implements Serializable{
	/**
	 * 
	 */
	private static final long serialVersionUID = -2562490743208447559L;
	private String id;
	private String name;
	private String name_en;
	private String agename;
	private String title;
	private String classid;
	private String path;
	private String songwordpath;
	private boolean isClicked;
	private String albumCoverpath;
	private long date;//记录播放时间
	private String classname;
	private String picpath_l;
	private String picpath_m;
	private String picpath_s;
	
	private long duration;
	private String localPath;
	private long size;
	
	private String albumId;
	
	
	public String getId() {
		return id;
	}

	public void setId(String id) {
		this.id = id;
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getName_en() {
		return name_en;
	}

	public void setName_en(String name_en) {
		this.name_en = name_en;
	}

	public String getAgename() {
		return agename;
	}

	public void setAgename(String agename) {
		this.agename = agename;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getClassid() {
		return classid;
	}

	public void setClassid(String classid) {
		this.classid = classid;
	}

	public String getPath() {
		return path;
	}

	public void setPath(String path) {
		this.path = path;
	}

	public String getSongwordpath() {
		return songwordpath;
	}

	public void setSongwordpath(String songwordpath) {
		this.songwordpath = songwordpath;
	}

	public boolean isClicked() {
		return isClicked;
	}

	public void setClicked(boolean isClicked) {
		this.isClicked = isClicked;
	}

	


	public String getAlbumCoverpath() {
		return albumCoverpath;
	}

	public void setAlbumCoverpath(String albumCoverpath) {
		this.albumCoverpath = albumCoverpath;
	}

	public long getDate() {
		return date;
	}

	public void setDate(long date) {
		this.date = date;
	}

	public String getClassname() {
		return classname;
	}

	public void setClassname(String classname) {
		this.classname = classname;
	}

	public String getPicpath_l() {
		return picpath_l;
	}

	public void setPicpath_l(String picpath_l) {
		this.picpath_l = picpath_l;
	}

	public String getPicpath_m() {
		return picpath_m;
	}

	public void setPicpath_m(String picpath_m) {
		this.picpath_m = picpath_m;
	}

	public String getPicpath_s() {
		return picpath_s;
	}

	public void setPicpath_s(String picpath_s) {
		this.picpath_s = picpath_s;
	}

	public static long getSerialversionuid() {
		return serialVersionUID;
	}

	@Override
	public String toString() {
		return "Music2 [id=" + id + ", name=" + name + ", name_en=" + name_en
				+ ", agename=" + agename + ", title=" + title + ", classid="
				+ classid + ", path=" + path + ", songwordpath=" + songwordpath
				+ ", isClicked=" + isClicked
				+ ", albumCoverpath=" + albumCoverpath + ", date=" + date
				+ ", classname=" + classname + ", picpath_l=" + picpath_l
				+ ", picpath_m=" + picpath_m + ", picpath_s=" + picpath_s + "]";
	}

	public long getDuration() {
		return duration;
	}
	
	public void setDuration(long duration) {
		this.duration = duration;
	}

	public String getArtist() {
		return getClassname();
	}

	public String getImage() {
		return getPicpath_l();
	}
	
	public String getLocalPath() {
		return localPath;
	}
	
	public void setLocalPath(String localPath) {
		this.localPath = localPath;
	}
	
	public long getSize() {
		return size;
	}
	
	public void setSize(long size) {
		this.size = size;
	}

	public void setAlbumId(String albumId) {
		this.albumId = albumId;
	}
	
	public String getAlbumId() {
		return albumId;
	}
	
}
