package com.chipsguide.app.colorbluetoothlamp.v2.brunton.been;

import java.util.List;

public class SearchBoby {

	private Status status;
	private SearchList lists;
	private List<Music> list;
	private int count;
	private int limit;
	private int page;
	private int countPage;
	
	public Status getStatus() {
		return status;
	}
	public void setStatus(Status status) {
		this.status = status;
	}
	public SearchList getLists() {
		return lists;
	}
	public void setLists(SearchList lists) {
		this.lists = lists;
	}
	public List<Music> getList() {
		return list;
	}
	public void setList(List<Music> list) {
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
	public int getCountPage() {
		return countPage;
	}
	public void setCountPage(int countPage) {
		this.countPage = countPage;
	}
	
	
}
