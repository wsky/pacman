package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.ActivityExecutor;
import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;
import com.taobao.top.pacman.Helper;

public class EmptyWithCancelationCheckWorkItem extends ActivityExecutionWorkItem {
	private ActivityInstance completedInstance;

	public EmptyWithCancelationCheckWorkItem(ActivityInstance activityInstance, ActivityInstance completedInstance) {
		super(activityInstance);
		this.completedInstance = completedInstance;
		this.isEmpty = true;
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		Helper.assertFail();
		return true;
	}

	@Override
	public void postProcess(ActivityExecutor executor) {
		if (this.completedInstance.getState() != ActivityInstanceState.Closed &&
				this.getActivityInstance().isPerformingDefaultCancelation()) {
			this.getActivityInstance().markCanceled();
			System.out.println("set canceling for " + this.getActivityInstance() + " in emptyWorkItem");
		}
		super.postProcess(executor);
	}
}
