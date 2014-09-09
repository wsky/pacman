package pacman.runtime;

import pacman.ActivityExecutor;
import pacman.ActivityInstance;
import pacman.Helper;
import pacman.ActivityInstance.ActivityInstanceState;

public class EmptyWithCancelationCheckWorkItem extends ActivityExecutionWorkItem {
	private ActivityInstance completedInstance;

	public EmptyWithCancelationCheckWorkItem(ActivityInstance activityInstance, ActivityInstance completedInstance) {
		super(activityInstance);
		this.completedInstance = completedInstance;
		this.isEmpty = true;
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		Helper.assertFail();
		return true;
	}

	@Override
	public void postProcess(ActivityExecutor executor) {
		if (this.completedInstance.getState() != ActivityInstanceState.Closed &&
				this.getActivityInstance().isPerformingDefaultCancelation()) {
			this.getActivityInstance().markCanceled();
		}
		super.postProcess(executor);
	}
}
