package com.taobao.top.pacman.expressions;

import com.taobao.top.pacman.*;

public class Literal extends NativeActivityWithResult {
	private Object constValue;

	public Literal(Object constValue) {
		this.constValue = constValue;
	}

	@Override
	protected Object Execute(NativeActivityContext context) {
		return this.constValue;
	}
}
