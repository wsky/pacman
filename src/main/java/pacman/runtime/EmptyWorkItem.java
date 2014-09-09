package pacman.runtime;

import pacman.*;

public class EmptyWorkItem extends ActivityExecutionWorkItem {
	public EmptyWorkItem() {
		this.isEmpty = true;
		this.isPooled = true;
	}

	public void initialize(ActivityInstance activityInstance) {
		super.reinitialize(activityInstance);
	}

	@Override
	protected void releaseToPool(ActivityExecutor executor) {
		super.clear();
		executor.EmptyWorkItemPool.release(this);
	}

	@Override
	public boolean execute(ActivityExecutor activityExecutor, BookmarkManager bookmarkManager) {
		Helper.assertFail();
		return true;
	}
}
