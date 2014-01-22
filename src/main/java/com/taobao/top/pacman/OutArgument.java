package com.taobao.top.pacman;

import com.taobao.top.pacman.expressions.VariableReference;

public class OutArgument extends Argument {
	public OutArgument() {
	}

	public OutArgument(Variable variable) {
		this.setExpression(new VariableReference(variable));
	}

	// ActivityWithResult<Location>
	// public OutArgument(ActivityWithResult expression) {}
}
