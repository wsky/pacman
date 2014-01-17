package com.taobao.top.pacman;

import com.taobao.top.pacman.expressions.FunctionValue;
import com.taobao.top.pacman.expressions.Literal;

public class Variable extends LocationReference {
	private String name;
	private ActivityWithResult expression;

	public Variable() {
	}

	public Variable(String name) {
		this();
		this.name = name;
	}

	public Variable(String name, Object constValue) {
		this(name);
		this.expression = new Literal(constValue);
	}

	public Variable(Function<ActivityContext, Object> expression) {
		this(null, expression);
	}

	public Variable(String name, Function<ActivityContext, Object> expression) {
		this(name);
		this.expression = new FunctionValue(expression);
	}

	@Override
	protected String getName() {
		return this.name;
	}

	protected ActivityWithResult getExpression() {
		return this.expression;
	}

	public Object get(ActivityContext context) {
		return context.getValue(this);
	}

	public void set(ActivityContext context, Object value) {
		context.setValue(this, value);
	}
}
