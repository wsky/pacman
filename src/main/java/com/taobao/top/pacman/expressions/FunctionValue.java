package com.taobao.top.pacman.expressions;

import com.taobao.top.pacman.*;

public class FunctionValue extends CodeActivityWithResult {
	private Function<ActivityContext, Object> func;

	public FunctionValue(Function<ActivityContext, Object> function) {
		this.func = function;
	}

	@Override
	protected Object execute(CodeActivityContext context) {
		return this.func.execute(context);
	}
}
