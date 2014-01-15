package com.taobao.top.pacman.expressions;

import com.taobao.top.pacman.*;

public class VariableValue extends NativeActivityWithResult {
	private Variable variable;

	public VariableValue(Variable variable) {
		this.variable = variable;
	}

	@Override
	protected Object Execute(NativeActivityContext context) {
		return this.variable.get(context);
	}

}
