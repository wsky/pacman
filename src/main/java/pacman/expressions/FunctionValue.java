package pacman.expressions;

import pacman.*;

public class FunctionValue extends CodeActivityWithResult {
	private Function<ActivityContext, ?> func;

	public FunctionValue(Function<ActivityContext, ?> function) {
		this.func = function;
	}

	@Override
	protected Object[] tryGetValue(ActivityContext context) {
		return new Object[] { true, this.func.execute(context) };
	}

	@Override
	protected Object execute(CodeActivityContext context) {
		return this.executeWithTryGetValue(context);
	}
}
