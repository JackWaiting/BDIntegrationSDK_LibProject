package com.chipsguide.app.colorbluetoothlamp.v2.xinbaosheng.bean;

import java.util.List;

public class ColumnBoby {

	private Status status;
	private List<Column> list;
	public List<Column> getList() {
		return list;
	}

	public void setList(List<Column> list) {
		this.list = list;
	}

	public Status getStatus() {
		return status;
	}

	public void setStatus(Status status) {
		this.status = status;
	}

	@Override
	public String toString() {
		return "ColumnBoby [status=" + status + ", list=" + list + "]";
	}

}
