package pacman.runtime;

import java.util.Map;

import pacman.ActivityExecutor;
import pacman.ActivityInstance;
import pacman.Location;

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
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		return super.executeBody(executor, bookmarkManager, this.resultLocation);
	}
}
