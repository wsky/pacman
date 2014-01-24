package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.ActivityExecutor;
import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;

public abstract class ActivityExecutionWorkItem extends WorkItem {
	private boolean skipAbort;

	protected ActivityExecutionWorkItem() {
	}

	protected ActivityExecutionWorkItem(ActivityInstance activityInstance) {
		super(activityInstance);
	}

	@Override
	public boolean isValid() {
		return this.getActivityInstance().getState() == ActivityInstanceState.Executing;
	}

	@Override
	protected void clear() {
		super.clear();
		this.skipAbort = false;
	}

	protected void setExceptionToPropagateWithoutSkip(Exception exception) {
		this.setExceptionToPropagate(exception);
		this.skipAbort = true;
	}

	@Override
	public void postProcess(ActivityExecutor executor) {
		if (this.getExceptionToPropagate() != null && !this.skipAbort) {
			executor.abortActivityInstance(this.getActivityInstance(), this.getExceptionToPropagate());
			return;
		}

		if (!this.getActivityInstance().updateState(executor))
			return;

		Exception exception = executor.completeActivityInstance(this.getActivityInstance());
		if (exception != null)
			this.setExceptionToPropagate(exception);
	}

}
