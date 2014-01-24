package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.ActivityExecutor;
import com.taobao.top.pacman.ActivityInstance;

public class CancelActivityWorkItem extends ActivityExecutionWorkItem {

	public CancelActivityWorkItem(ActivityInstance activityInstance) {
		super(activityInstance);
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		try {
			this.getActivityInstance().cancel(executor, bookmarkManager);
		} catch (Exception e) {
			// FIXME assert fatal
			this.setExceptionToPropagate(e);
		}
		return true;
	}
}
