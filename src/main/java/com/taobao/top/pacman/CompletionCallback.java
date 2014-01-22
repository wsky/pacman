package com.taobao.top.pacman;

public interface CompletionCallback extends Delegate {
	public void execute(NativeActivityContext context, ActivityInstance completedInstance);
}
