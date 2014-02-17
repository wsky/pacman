package com.taobao.top.pacman;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.expressions.VariableReference;

public class OutArgument extends Argument {
	public OutArgument() {
		this.setDirection(ArgumentDirection.Out);
	}

	public OutArgument(Variable variable) {
		this.setExpression(new VariableReference(variable));
	}

	// TODO impl more outArgument output reference

	// ActivityWithResult<Location>
	// public OutArgument(ActivityWithResult expression) {}
}
