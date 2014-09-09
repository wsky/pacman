package pacman.runtime;

import pacman.ActivityInstance;
import pacman.FaultCallback;
import pacman.NativeActivityFaultContext;

public class FaultCallbackWrapper {
	private FaultCallback callback;
	private ActivityInstance activityInstance;

	public FaultCallbackWrapper(FaultCallback callback, ActivityInstance activityInstance) {
		this.callback = callback;
		this.activityInstance = activityInstance;
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
