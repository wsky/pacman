package com.taobao.top.pacman;

public abstract class LocationReference {
	private int id;

	protected int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected abstract String getName();
}
