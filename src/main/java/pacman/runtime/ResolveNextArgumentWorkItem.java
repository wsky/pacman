package pacman.runtime;

import java.util.Map;

import pacman.*;

public class ResolveNextArgumentWorkItem extends ActivityExecutionWorkItem {
	private int nextArgumentIndex;
	private Map<String, Object> argumentValues;
	private Location resultLocation;

	public ResolveNextArgumentWorkItem() {
		this.isPooled = true;
	}

	public void initialize(ActivityInstance activityInstance,
			int nextArgumentIndex,
			Map<String, Object> argumentValues,
			Location resultLocation) {
		super.reinitialize(activityInstance);
		this.nextArgumentIndex = nextArgumentIndex;
		this.argumentValues = argumentValues;
		this.resultLocation = resultLocation;
	}

	@Override
	protected void releaseToPool(ActivityExecutor executor) {
		super.clear();
		this.nextArgumentIndex = 0;
		this.argumentValues = null;
		this.resultLocation = null;
		executor.ResolveNextArgumentWorkItemPool.release(this);
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		this.getActivityInstance().resolveArguments(
				executor, this.argumentValues, this.resultLocation, this.nextArgumentIndex);
		return true;
	}
}
