package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;
import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;

public class CompletionBookmark {
	private CompletionCallbackWrapper callbackWrapper;

	public CompletionBookmark(CompletionCallbackWrapper callbackWrapper) {
	}

	public CompletionBookmark() {
		// only called when executor.abortActivityInstance()
	}

	public WorkItem generateWorkItem(ActivityInstance completedInstance, ActivityExecutor executor) {
		if (this.callbackWrapper != null)
			return this.callbackWrapper.createWorkItem(completedInstance, executor);

		// for variable.default and arugment.expression
		if (completedInstance.getState() != ActivityInstanceState.Closed && completedInstance.getParent().haveNotExecuted())
			completedInstance.getParent().setInitializationIncomplete();
		// some one call abort()
		return new EmptyWithCancelationCheckWorkItem(completedInstance.getParent(), completedInstance);
	}

	public void checkForCancelation() {
		this.callbackWrapper.checkForCancelation();
	}

}
