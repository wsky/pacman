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
		// NOTE 4.1 check exception, abort activityInstance if exception not handled
		if (this.getExceptionToPropagate() != null && !this.skipAbort) {
			System.out.println("abort activityInstance in postProcess()");
			executor.abortActivityInstance(this.getActivityInstance(), this.getExceptionToPropagate());
			return;
		}

		// NOTE 4.2 update activityInstance state and check weather activity completed
		if (!this.getActivityInstance().updateState(executor))
			return;

		// NOTE 4.3 complete activityInstance
		Exception exception = executor.completeActivityInstance(this.getActivityInstance());
		if (exception != null)
			this.setExceptionToPropagate(exception);
	}

}
