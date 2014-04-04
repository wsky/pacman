package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;

public class ReferenceActivityDefinition extends ActivityDefinition {
	private ActivityDefinition activity;

	public ReferenceActivityDefinition(String displayName) {
		super(displayName);
	}

	@Override
	protected void addActivity(ActivityDefinition activity) {
		this.activity = activity;
		super.addActivity(activity);
	}

	@Override
	protected Activity internalToActivity(DefinitionValidator validator) {
		return this.activity != null ? this.activity.toActivity(validator) : null;
	}
}
