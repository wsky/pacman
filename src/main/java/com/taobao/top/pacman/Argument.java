package com.taobao.top.pacman;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public abstract class Argument {
	private String name;
	private Class<?> argumentType;
	private ArgumentDirection direction;
	private RuntimeArgument runtimeArgument;
	private ActivityWithResult expression;
	
	protected String getName() {
		return this.name;
	}
	
	protected void setName(String name) {
		this.name = name;
	}
	
	public Class<?> getArgumentType() {
		return this.argumentType;
	}
	
	protected void setArgumentType(Class<?> type) {
		this.argumentType = type;
	}
	
	protected ArgumentDirection getDirection() {
		return this.direction;
	}
	
	protected void setDirection(ArgumentDirection direction) {
		this.direction = direction;
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
	
	protected abstract boolean tryPopulateValue(
			LocationEnvironment environment,
			ActivityInstance activityInstance,
			ActivityContext resolutionContext);
}
