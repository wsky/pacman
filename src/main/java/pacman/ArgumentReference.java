package pacman;

public class ArgumentReference extends CodeActivityWithResult {
	private Argument argument;
	private RuntimeArgument targetArgument;
	
	public ArgumentReference(Argument argument) {
		this.argument = argument;
	}
	
	@Override
	protected Object[] tryGetValue(ActivityContext context) {
		try {
			context.setAllowChainedEnvironmentAccess(true);
			return new Object[] { true, context.getLocation(this.targetArgument) };
		} finally {
			context.setAllowChainedEnvironmentAccess(false);
		}
	}
	
	@Override
	protected Object execute(CodeActivityContext context) throws Exception {
		return this.executeWithTryGetValue(context);
	}
	
	@Override
	protected void cacheMetadata(ActivityMetadata metadata) throws Exception {
		this.targetArgument = this.argument.getRuntimeArgument();
	}
}
