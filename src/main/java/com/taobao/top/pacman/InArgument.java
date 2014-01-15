package com.taobao.top.pacman;

import com.taobao.top.pacman.expressions.FunctionValue;
import com.taobao.top.pacman.expressions.Literal;
import com.taobao.top.pacman.expressions.VariableValue;

public class InArgument extends Argument {
	private ActivityWithResult expression;

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
		this.expression = expression;
	}

	public ActivityWithResult getExpression() {
		return this.expression;
	}

	public Object get(ActivityContext context) {
		return context.get(this);
	}

	public void set(ActivityContext context, Object value) {
		context.set(this, value);
	}
}
