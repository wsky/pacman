package com.taobao.top.pacman.expressions;

import com.taobao.top.pacman.*;

public class VariableValue extends CodeActivityWithResult {
	private Variable variable;

	public VariableValue(Variable variable) {
		this.variable = variable;
	}

	@Override
	protected Object execute(CodeActivityContext context) {
		return this.variable.get(context);
	}

	// TODO check variable specified

}
