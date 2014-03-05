package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.Variable;

public class OutArgumentDefinition {
	private String toVariable;

	public OutArgumentDefinition() {
	}

	public OutArgumentDefinition ToVariable(String name) {
		this.toVariable = name;
		return this;
	}

	public OutArgument toArgument(ActivityDefinition parent, DefinitionValidator validator) {
		if(this.toVariable==null)
			return new OutArgument();
		
		Variable variable = parent.getVariable(this.toVariable);
		if (variable != null)
			return new OutArgument(variable);

		validator.addError("can not find variable " + this.toVariable);
		return null;
	}
}
