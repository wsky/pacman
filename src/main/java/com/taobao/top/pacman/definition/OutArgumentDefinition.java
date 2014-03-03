package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.OutArgument;

public class OutArgumentDefinition {
	private String toVariable;

	public OutArgumentDefinition() {
	}

	public OutArgumentDefinition toVariable(String name) {
		this.toVariable = name;
		return this;
	}

	public OutArgument toArgument(ActivityDefinition parent) {
		return this.toVariable != null ?
				new OutArgument(parent.getVariable(this.toVariable)) :
				new OutArgument();

	}
}
