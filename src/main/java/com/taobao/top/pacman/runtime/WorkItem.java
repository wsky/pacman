package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;

public abstract class WorkItem {
	private ActivityInstance activityInstance;
	private Exception exceptionToPropagate;
	protected Exception workflowAbortException;
	protected boolean isEmpty;
	protected boolean isPooled;

	protected WorkItem() {
	}

	protected WorkItem(ActivityInstance activityInstance) {
		this.activityInstance = activityInstance;
	}

	public ActivityInstance getActivityInstance() {
		return this.activityInstance;
	}

	public void reinitialize(ActivityInstance activityInstance) {
		this.activityInstance = activityInstance;
		this.activityInstance.incrementBusyCount();
	}

	public boolean isEmpty() {
		return this.isEmpty;
	}

	public Exception getWorkflowAbortException() {
		return this.workflowAbortException;
	}

	public Exception getExceptionToPropagate() {
		return this.exceptionToPropagate;
	}

	public void setExceptionToPropagate(Exception exception) {
		this.exceptionToPropagate = exception;
	}

	public void exceptionPropagated() {
		this.exceptionToPropagate = null;
	}

	public void release() {
		this.activityInstance.decrementBusyCount();
	}

	public void dispose(ActivityExecutor executor) {
		if (this.isPooled)
			this.releaseToPool(executor);
	}

	protected void releaseToPool(ActivityExecutor executor) {
	}

	protected void clear() {
		this.exceptionToPropagate = null;
		this.workflowAbortException = null;
		this.activityInstance = null;
	}

	public abstract boolean isValid();

	public abstract boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager);

	public abstract void postProcess(ActivityExecutor executor);
}
