package pacman;

import pacman.expressions.FunctionValue;

// as do not have expression tree model 
// to resovle function to inlinedFunction that only chained access for get(), 
// i defined this activity.
public class InlinedFunctionValue extends FunctionValue {
	public InlinedFunctionValue(Function<ActivityContext, Object> function) {
		super(function);
	}

	@Override
	protected Object[] tryGetValue(ActivityContext context) {
		// NOTE inlined function can chained access, if call context.set, it was not inlined
		try {
			context.setAllowChainedEnvironmentAccess(true);
			return super.tryGetValue(context);
		} finally {
			context.setAllowChainedEnvironmentAccess(false);
		}
	}
}
