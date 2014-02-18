package com.taobao.top.pacman;

public class VariableValue extends CodeActivityWithResult {
	private Variable variable;

	public VariableValue(Variable variable) {
		this.variable = variable;
	}

	@Override
	protected Object[] tryGetValue(ActivityContext context) {
		try {
			context.setAllowChainedEnvironmentAccess(true);
			return new Object[] { true, context.getValue(this.variable) };
		} finally {
			context.setAllowChainedEnvironmentAccess(false);
		}
	}

	@Override
	protected Object execute(CodeActivityContext context) {
		return this.executeWithTryGetValue(context);
	}

	// TODO check variable specified

}
