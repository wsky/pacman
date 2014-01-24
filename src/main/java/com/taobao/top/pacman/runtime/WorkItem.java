package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;

public class WorkItem {

	public void release() {
		
	}

	public boolean isEmpty() {
		return false;
	}

	public boolean execute(ActivityExecutor activityExecutor, BookmarkManager bookmarkManager) {
		return false;
	}

	public Exception getWorkflowAbortException() {
		return null;
	}

	public void postProcess(ActivityExecutor activityExecutor) {
		
	}

	public Exception getExceptionToPropagate() {
		return null;
	}

	public boolean isValid() {
		return false;
	}

}
