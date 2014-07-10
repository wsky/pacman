package com.taobao.top.pacman;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.expressions.FunctionValue;
import com.taobao.top.pacman.expressions.Literal;

public class InArgument extends Argument {
	public InArgument() {
		this(Object.class);
	}
	
	public InArgument(Class<?> type) {
		this.setArgumentType(type);
		this.setDirection(ArgumentDirection.In);
	}
	
	public InArgument(Object constValue) {
		this(constValue != null ? constValue.getClass() : Object.class, constValue);
	}
	
	public InArgument(Class<?> type, Object constValue) {
		this(type, new Literal(constValue));
	}
	
	public InArgument(Variable variable) {
		this(variable.getType(), new VariableValue(variable));
	}
	
	public InArgument(Function<ActivityContext, Object> expression) {
		this(Object.class, expression);
	}
	
	public <T> InArgument(Class<T> type, Function<ActivityContext, T> expression) {
		this(type, new FunctionValue(expression));
	}
	
	public InArgument(ActivityWithResult expression) {
		this(Object.class, expression);
	}
	
	public InArgument(Class<?> type, ActivityWithResult expression) {
		this(type);
		this.setExpression(expression);
	}
	
	@Override
	protected boolean tryPopulateValue(LocationEnvironment environment,
			ActivityInstance activityInstance,
			ActivityContext resolutionContext) {
		Location location = new Location();
		// declare first, differecnt from outArgument, no temp location
		environment.declare(this.getRuntimeArgument(), location, activityInstance);
		
		Object[] ret = this.getExpression().tryGetValue(resolutionContext);
		if ((Boolean) ret[0]) {
			location.setValue(ret[1]);
			return true;
		}
		else
			return false;
	}
}
