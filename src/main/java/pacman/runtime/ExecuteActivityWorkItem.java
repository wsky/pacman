package pacman.runtime;

import java.util.Map;

import pacman.ActivityExecutor;
import pacman.ActivityInstance;
import pacman.Helper;
import pacman.Location;

public class ExecuteActivityWorkItem extends ActivityExecutionWorkItem {
	private boolean requiresSymbolResolution;
	private Map<String, Object> argumentValues;

	public ExecuteActivityWorkItem() {
		this.isPooled = true;
	}

	protected ExecuteActivityWorkItem(ActivityInstance activityInstance,
			boolean requiresSymbolResolution,
			Map<String, Object> argumentValues) {
		this.initialize(activityInstance, requiresSymbolResolution, argumentValues);
	}

	@Override
	protected void releaseToPool(ActivityExecutor executor) {
		super.clear();
		this.requiresSymbolResolution = false;
		this.argumentValues = null;
		executor.ExecuteActivityWorkItemPool.release(this);
	}

	public void initialize(ActivityInstance activityInstance, boolean requiresSymbolResolution, Map<String, Object> argumentValues) {
		super.reinitialize(activityInstance);
		this.requiresSymbolResolution = requiresSymbolResolution;
		this.argumentValues = argumentValues;
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		return this.executeBody(executor, bookmarkManager, null);
	}

	protected boolean executeBody(ActivityExecutor executor, BookmarkManager bookmarkManager, Location resultLocation) throws Exception {
		try {
			//NOTE 3.1 resolve argument and variable now or schedule them
			if (this.requiresSymbolResolution) {
				if (!this.getActivityInstance().resolveArguments(executor, argumentValues, resultLocation, 0))
					return true;
				if (!this.getActivityInstance().resolveVariables(executor))
					return true;
			}
			this.getActivityInstance().setInitialized();
			this.getActivityInstance().execute(executor, bookmarkManager);
		} catch (Exception e) {
			if (Helper.isFatal(e))
				throw e;
			this.setExceptionToPropagate(e);
		}
		return true;
	}
}
