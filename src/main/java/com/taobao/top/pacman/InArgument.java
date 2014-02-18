package com.taobao.top.pacman;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.expressions.FunctionValue;
import com.taobao.top.pacman.expressions.Literal;

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

	@Override
	protected boolean tryPopulateValue(LocationEnvironment environment,
			ActivityInstance activityInstance,
			ActivityContext resolutionContext) {
		Location location = new Location();
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
