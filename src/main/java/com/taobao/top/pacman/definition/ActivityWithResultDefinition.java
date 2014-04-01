package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.ActivityWithResult;
import com.taobao.top.pacman.VariableValue;

public class ActivityWithResultDefinition {
	private VariableReferenceDefinition variable;
	private InlinedFunctionDefinition function;

	public ActivityWithResultDefinition(VariableReferenceDefinition variable) {
		this.variable = variable;
	}

	public ActivityWithResultDefinition(InlinedFunctionDefinition function) {
		this.function = function;
	}

	public ActivityWithResult toActivity(ActivityDefinition parent, DefinitionValidator validator) {
		if (this.function != null)
			return this.function.toFunction(parent, validator);

		if (this.variable != null)
			return new VariableValue(this.variable.toVariable(parent, validator));

		return null;

	}
}
