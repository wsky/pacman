package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.InArgument;

public class InArgumentDefinition {
	private Object constValue;
	private String fromVariable;

	public InArgumentDefinition() {
	}

	public InArgumentDefinition(Object constValue) {
		this.constValue = constValue;
	}

	public InArgumentDefinition FromVariable(String name) {
		this.fromVariable = name;
		return this;
	}

	public InArgument toArgument(ActivityDefinition parent) {
		return this.fromVariable != null ?
				new InArgument(parent.getVariable(this.fromVariable)) :
				new InArgument(this.constValue);
	}
}
