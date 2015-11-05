package com.chipsguide.app.colorbluetoothlamp.v2.brunton.been;

import java.util.List;

public class AlbumLists {


	private List<Album> list;
	private int count;
	private int page;
	private int pageCount;
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

	public int getLimit() {
		return limit;
	}
	public void setLimit(int limit) {
		this.limit = limit;
	}
	public int getPage() {
		return page;
	}
	public void setPage(int page) {
		this.page = page;
	}
	public int getPageCount() {
		return pageCount;
	}
	public void setPageCount(int pageCount) {
		this.pageCount = pageCount;
	}
}
