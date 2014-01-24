package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;

public abstract class CompletionCallbackWrapper {
	private ActivityInstance activityInstance;
	private boolean checkForCancelation;
	protected Delegate delegate;
	protected boolean needsToGatherOutputs;

	public CompletionCallbackWrapper(Delegate delegate, ActivityInstance activityInstance) {
		this.delegate = delegate;
		this.activityInstance = activityInstance;
	}

	public ActivityInstance getActivityInstance() {
		return this.activityInstance;
	}

	public void checkForCancelation() {
		this.checkForCancelation = true;
	}

	public WorkItem createWorkItem(ActivityInstance completedInstance, ActivityExecutor executor) {
		if (this.needsToGatherOutputs)
			this.gatherOutputs(completedInstance);

		CompletionWorkItem workItem;

		if (this.checkForCancelation)
			workItem = new CompletionWithCancelationCheckWorkItem(this, completedInstance);
		else {
			workItem = executor.CompletionWorkItemPool.acquire();
			workItem.initialize(this, completedInstance);
		}
		return workItem;
	}

	protected abstract void invoke(NativeActivityContext context, ActivityInstance completedInstance);

	protected void gatherOutputs(ActivityInstance completedInstance) {
	}

}
