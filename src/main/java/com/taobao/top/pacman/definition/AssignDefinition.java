package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;

public class AssignDefinition extends ActivityDefinition {
	public AssignDefinition(ActivityDefinition parent) {
		super(parent);
	}

	@Override
	protected Activity toActivity() {
		return null;
	}

}
