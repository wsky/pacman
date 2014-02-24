package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.FaultCallback;
import com.taobao.top.pacman.NativeActivityFaultContext;

public class FaultCallbackWrapper {
	private FaultCallback callback;
	private ActivityInstance activityInstance;

	public FaultCallbackWrapper(FaultCallback onFault, ActivityInstance currentInstance) {
		this.callback = onFault;
		this.activityInstance = currentInstance;
	}

	public ActivityInstance getActivityInstance() {
		return this.activityInstance;
	}

	public void invoke(NativeActivityFaultContext faultContext,
			Exception propagatedException,
			ActivityInstance propagatedFrom) {
		this.callback.execute(faultContext, propagatedException, propagatedFrom);
	}

	public WorkItem createWorkItem(Exception propagatedException, ActivityInstance propagatedFrom, ActivityInstance originalExceptionSource) {
		return new FaultWorkItem(this, propagatedException, propagatedFrom, originalExceptionSource);
	}
}
