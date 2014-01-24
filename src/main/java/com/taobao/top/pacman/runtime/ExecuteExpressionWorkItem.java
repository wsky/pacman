package com.taobao.top.pacman.runtime;

import java.util.Map;

import com.taobao.top.pacman.ActivityExecutor;
import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.Location;

public class ExecuteExpressionWorkItem extends ExecuteActivityWorkItem {
	private Location resultLocation;

	public ExecuteExpressionWorkItem(ActivityInstance activityInstance,
			boolean requireSymbolResolution,
			Map<String, Object> argumentValues,
			Location resultLocation) {
		super(activityInstance, requireSymbolResolution, argumentValues);
		this.resultLocation = resultLocation;
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		return super.executeBody(executor, bookmarkManager, this.resultLocation);
	}
}
