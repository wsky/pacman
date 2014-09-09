package pacman.expressions;

import pacman.*;

public class Literal extends CodeActivityWithResult {
	private Object constValue;

	public Literal(Object constValue) {
		this.constValue = constValue;
	}

	@Override
	protected Object[] tryGetValue(ActivityContext context) {
		return new Object[] { true, this.constValue };
	}

	@Override
	protected Object execute(CodeActivityContext context) {
		return this.executeWithTryGetValue(context);
	}
}
