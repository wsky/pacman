package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;
import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;

public class CompletionWithCancelationCheckWorkItem extends CompletionWorkItem {
	public CompletionWithCancelationCheckWorkItem(CompletionCallbackWrapper callbackWrapper, ActivityInstance completedInstance) {
		super(callbackWrapper, completedInstance);
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		if (this.completedInstance.getState() != ActivityInstanceState.Closed &&
				this.getActivityInstance().isPerformingDefaultCancelation()) {
			this.getActivityInstance().markCanceled();
		}
		// NOTE still execute completionCallback for cancelaton
		return super.execute(executor, bookmarkManager);
	}
}
