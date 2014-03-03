package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Variable;

public class VariableDefinition {
	private String name;

	public VariableDefinition(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public Variable toVariable() {
		return new Variable(this.name);
	}
}
