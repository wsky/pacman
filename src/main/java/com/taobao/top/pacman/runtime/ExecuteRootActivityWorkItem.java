package com.taobao.top.pacman.runtime;

import java.util.Map;

import com.taobao.top.pacman.ActivityExecutor;
import com.taobao.top.pacman.ActivityInstance;

public class ExecuteRootActivityWorkItem extends ExecuteActivityWorkItem {
	public ExecuteRootActivityWorkItem(
			ActivityInstance activityInstance,
			boolean requiresSymbolResolution,
			Map<String, Object> argumentValues) {
		super(activityInstance, requiresSymbolResolution, argumentValues);
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		return super.execute(executor, bookmarkManager);
	}

}
