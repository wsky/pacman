package com.taobao.top.pacman;

public abstract class Argument {
	private String name;
	private RuntimeArgument runtimeArgument;
	private ActivityWithResult expression;

	protected String getName() {
		return this.name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected RuntimeArgument getRuntimeArgument() {
		return this.runtimeArgument;
	}

	protected void setRuntimeArgument(RuntimeArgument runtimeArgument) {
		this.runtimeArgument = runtimeArgument;
	}

	protected ActivityWithResult getExpression() {
		return expression;
	}

	protected void setExpression(ActivityWithResult expression) {
		this.expression = expression;
	}

	public Object get(ActivityContext context) {
		return context.get(this);
	}

	public void set(ActivityContext context, Object value) {
		context.set(this, value);
	}
}
