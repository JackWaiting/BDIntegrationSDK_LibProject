package com.chipsguide.app.colorbluetoothlamp.v2.bean;

import java.util.List;

public class AlbumBoby {

	private Status status;
	private AlbumLists lists;
	private List<Album> list;
	private int count;
	private int countPage;
	private int page;
	private int limit;
	
	public List<Album> getList() {
		return list;
	}
	public void setList(List<Album> list) {
		this.list = list;
	}
	public int getCount() {
		return count;
	}
	public void setCount(int count) {
		this.count = count;
	}
	public int getCountPage() {
		return countPage;
	}
	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}
	
	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}

	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public AlbumLists getLists() {
		return lists;
	}
	public void setLists(AlbumLists lists) {
		this.lists = lists;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	
	
	
}
