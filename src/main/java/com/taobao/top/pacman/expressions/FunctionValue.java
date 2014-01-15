package com.taobao.top.pacman.expressions;

import com.taobao.top.pacman.*;

public class FunctionValue extends NativeActivityWithResult {
	private Function<ActivityContext, Object> func;

	public FunctionValue(Function<ActivityContext, Object> function) {
		this.func = function;
	}

	@Override
	protected Object Execute(NativeActivityContext context) {
		return this.func.execute(context);
	}
}
