package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Variable;

public class VariableReferenceDefinition {
	private String name;

	public VariableReferenceDefinition(String name) {
		this.name = name;
	}

	public String getName() {
		return this.name;
	}

	public Variable toVariable(ActivityDefinition parent, DefinitionValidator validator) {
		Variable variable = parent != null ? parent.getVariable(this.name) : null;
		if (variable == null)
			validator.addError("can not find variable \"" + this.name + "\"");
		return variable;
	}
}
