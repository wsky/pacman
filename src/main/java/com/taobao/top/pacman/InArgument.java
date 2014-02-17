package com.taobao.top.pacman;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.expressions.FunctionValue;
import com.taobao.top.pacman.expressions.Literal;
import com.taobao.top.pacman.expressions.VariableValue;

public class InArgument extends Argument {
	public InArgument() {
		this.setDirection(ArgumentDirection.In);
	}

	public InArgument(Object constValue) {
		this(new Literal(constValue));
	}

	public InArgument(Variable variable) {
		this(new VariableValue(variable));
	}

	public InArgument(Function<ActivityContext, Object> expression) {
		this(new FunctionValue(expression));
	}

	public InArgument(ActivityWithResult expression) {
		this();
		this.setExpression(expression);
	}
}
