package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;

public abstract class ActivityDefinition {
	private ActivityDefinition parent;

	public ActivityDefinition(ActivityDefinition parent) {
		this.parent = parent;
	}

	public ActivityDefinition end() {
		return this.parent;
	}

	protected abstract Activity toActivity();
}
