package com.taobao.top.pacman.expressions;

import com.taobao.top.pacman.*;

public class VariableReference extends CodeActivityWithResult {
	private Variable variable;

	public VariableReference(Variable variable) {
		this.variable = variable;
	}

	@Override
	protected Object execute(CodeActivityContext context) {
		return context.getLocation(this.variable);
	}

	// TODO check variable's metadata was cached
}
