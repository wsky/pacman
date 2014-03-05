package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.Variable;

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

	public InArgument toArgument(ActivityDefinition parent, DefinitionValidator validator) {
		if (this.fromVariable == null)
			return new InArgument(this.constValue);

		Variable variable = parent.getVariable(this.fromVariable);
		if (variable != null)
			return new InArgument(variable);

		validator.addError("can not find variable " + this.fromVariable);
		return null;
	}
}
