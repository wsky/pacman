package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;

public class SingleActivityContainerDefinition<T extends ActivityDefinition> extends ActivityContainerDefinition {
	private ActivityDefinition activity;

	public SingleActivityContainerDefinition(String displayName) {
		super(displayName);
	}

	public SingleActivityContainerDefinition(String displayName, ActivityDefinition parent) {
		super(displayName, parent);
	}

	public T Activity(ActivityDefinition activity) {
		this.addActivity(activity);
		return this.End();
	}

	@SuppressWarnings("unchecked")
	@Override
	public T End() {
		return (T) super.End();
	}

	@Override
	protected Activity internalToActivity(DefinitionValidator validator) {
		return this.activity != null ? this.activity.toActivity(validator) : null;
	}

	@Override
	protected void addActivity(ActivityDefinition activity) {
		this.activity = activity;
		super.addActivity(activity);
	}
}
