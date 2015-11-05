package com.chipsguide.app.colorbluetoothlamp.v2.brunton.been;

import java.io.Serializable;

/**
 * 专辑
 * @author HeYM
 *
 */
public class Album implements Serializable{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String id;
	private String name;
	private String name_en;
	private String title;
	private String classid;
	private String agename;
	private String musicCount;
	private String type;
	private String coverpath_l;
	private String coverpath_m;
	private String coverpath_s;
	private String plays_count;
	private boolean isStore;
	private boolean isDownload;
	
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
	public String getAgename() {
		return agename;
	}
	public void setAgename(String agename) {
		this.agename = agename;
	}
	
	public String getMusicCount() {
		return musicCount;
	}
	public void setMusicCount(String musicCount) {
		this.musicCount = musicCount;
	}
	
	public String getType() {
		return type;
	}
	public void setType(String type) {
		this.type = type;
	}
	public String getPlays_count() {
		return plays_count;
	}
	public void setPlays_count(String plays_count) {
		this.plays_count = plays_count;
	}
	public String getCoverpath_l() {
		return coverpath_l;
	}
	public void setCoverpath_l(String coverpath_l) {
		this.coverpath_l = coverpath_l;
	}
	public String getCoverpath_m() {
		return coverpath_m;
	}
	public void setCoverpath_m(String coverpath_m) {
		this.coverpath_m = coverpath_m;
	}
	public String getCoverpath_s() {
		return coverpath_s;
	}
	public void setCoverpath_s(String coverpath_s) {
		this.coverpath_s = coverpath_s;
	}
	
	public boolean isDownload() {
		return isDownload;
	}
	
	public boolean isStore() {
		return isStore;
	}
	
	public void setDownload(boolean isDownload) {
		this.isDownload = isDownload;
	}
	
	public void setStore(boolean isStore) {
		this.isStore = isStore;
	}
}
