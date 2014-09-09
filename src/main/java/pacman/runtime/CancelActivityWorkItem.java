package pacman.runtime;

import pacman.ActivityExecutor;
import pacman.ActivityInstance;
import pacman.Helper;

public class CancelActivityWorkItem extends ActivityExecutionWorkItem {

	public CancelActivityWorkItem(ActivityInstance activityInstance) {
		super(activityInstance);
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		try {
			this.getActivityInstance().cancel(executor, bookmarkManager);
		} catch (Exception e) {
			if (Helper.isFatal(e))
				throw e;
			this.setExceptionToPropagate(e);
		}
		return true;
	}
}
