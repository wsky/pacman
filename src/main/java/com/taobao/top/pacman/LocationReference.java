package com.taobao.top.pacman;

public abstract class LocationReference {
	private int id;
	private Class<?> type;

	protected int getId() {
		return id;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected Class<?> getType() {
		return type;
	}

	protected void setType(Class<?> type) {
		this.type = type;
	}

	protected abstract String getName();

	protected abstract Location getLocation(ActivityContext context);
}
