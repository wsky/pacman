package pacman;

public class ArgumentValue extends CodeActivityWithResult {
	private Argument argument;

	// TODO support ArgumentValue(string argumentName)
	public ArgumentValue(Argument argument) {
		this.argument = argument;
	}

	@Override
	protected Object[] tryGetValue(ActivityContext context) {
		try {
			context.setAllowChainedEnvironmentAccess(true);
			return new Object[] { true, context.getValue(this.argument.getRuntimeArgument()) };
		} finally {
			context.setAllowChainedEnvironmentAccess(false);
		}
	}

	@Override
	protected Object execute(CodeActivityContext context) {
		return this.executeWithTryGetValue(context);
	}

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		// TODO check argument exist
	}
}
