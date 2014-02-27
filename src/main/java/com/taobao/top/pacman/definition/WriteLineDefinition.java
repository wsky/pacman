package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;

public class WriteLineDefinition extends ActivityDefinition {
	public WriteLineDefinition(ActivityDefinition parent) {
		super(parent);
	}

	public WriteLineDefinition text(Object constValue) {
		return this;
	}

	@Override
	protected Activity toActivity() {
		return null;
	}
}
