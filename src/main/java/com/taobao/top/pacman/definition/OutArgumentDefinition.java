package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.OutArgument;

public class OutArgumentDefinition {
	private VariableReferenceDefinition variable;

	public OutArgumentDefinition() {
	}

	public OutArgumentDefinition(VariableReferenceDefinition variable) {
		this.variable = variable;
	}

	public OutArgument toArgument(ActivityDefinition parent, DefinitionValidator validator) {
		return this.variable != null ?
				new OutArgument(this.variable.toVariable(parent, validator)) :
				new OutArgument();
	}
}
