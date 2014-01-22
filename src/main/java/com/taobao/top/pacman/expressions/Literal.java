package com.taobao.top.pacman.expressions;

import com.taobao.top.pacman.*;

public class Literal extends CodeActivityWithResult {
	private Object constValue;

	public Literal(Object constValue) {
		this.constValue = constValue;
	}

	@Override
	protected Object execute(CodeActivityContext context) {
		return this.constValue;
	}
}
