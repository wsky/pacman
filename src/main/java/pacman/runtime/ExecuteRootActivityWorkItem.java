package pacman.runtime;

import java.util.Map;

import pacman.ActivityExecutor;
import pacman.ActivityInstance;

public class ExecuteRootActivityWorkItem extends ExecuteActivityWorkItem {
	public ExecuteRootActivityWorkItem(
			ActivityInstance activityInstance,
			boolean requiresSymbolResolution,
			Map<String, Object> argumentValues) {
		super(activityInstance, requiresSymbolResolution, argumentValues);
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		return this.executeBody(executor, bookmarkManager, null);
	}

}
