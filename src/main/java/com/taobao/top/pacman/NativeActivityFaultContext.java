package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.BookmarkManager;

public class NativeActivityFaultContext {

	public NativeActivityFaultContext(ActivityInstance activityInstance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
	}

	public boolean isFaultHandled() {
		return false;
	}

	public void dispose() {		
	}

}
