package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;

public class SequenceDefinition extends ActivityContainerDefinition {
	public SequenceDefinition(ActivityContainerDefinition parent) {
		super(parent);
	}

	public SequenceDefinition var(String variable) {
		return this;
	}

	@Override
	protected Activity toActivity() {
		return null;
	}
}
