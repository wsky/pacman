package com.taobao.top.pacman;

public abstract class NativeActivityWithResult extends ActivityWithResult {
	@Override
	public OutArgument getInternalResult() {
		return null;
	}
	
	protected abstract Object Execute(NativeActivityContext context);

}
