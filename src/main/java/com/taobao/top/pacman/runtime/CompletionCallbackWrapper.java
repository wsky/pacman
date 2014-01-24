package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;

public abstract class CompletionCallbackWrapper {

	public CompletionCallbackWrapper(Delegate delegate, ActivityInstance currentInstance) {
	}

	public void checkForCancelation() {
		
	}

	public WorkItem createWorkItem(ActivityInstance completedInstance, ActivityExecutor executor) {
		return null;
	}

}
