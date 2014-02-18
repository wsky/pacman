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

	@Override
	protected boolean tryPopulateValue(LocationEnvironment environment,
			ActivityInstance activityInstance,
			ActivityContext resolutionContext) {
		Object[] ret = this.getExpression().tryGetValue(resolutionContext);
		if ((Boolean) ret[0]) {
			environment.declare(
					this.getRuntimeArgument(),
					// FIXME create referenceLocation for outArgument
					// ((Location)ret[1]).CreateReference(true),
					(Location) ret[1],
					activityInstance);
			return true;
		} else {
			// FIXME temp location
			// environment.declareTemporaryLocation<Location<T>>(this.RuntimeArgument, targetActivityInstance, true);
			return false;
		}
	}
	// TODO impl more outArgument output reference

	// ActivityWithResult<Location>
	// public OutArgument(ActivityWithResult expression) {}
}
