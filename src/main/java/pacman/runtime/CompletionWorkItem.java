package pacman.runtime;

import pacman.ActivityExecutor;
import pacman.ActivityInstance;
import pacman.Helper;
import pacman.NativeActivityContext;

public class CompletionWorkItem extends ActivityExecutionWorkItem {
	private CompletionCallbackWrapper callbackWrapper;
	protected ActivityInstance completedInstance;

	public CompletionWorkItem() {
		this.isPooled = true;
	}

	protected CompletionWorkItem(CompletionCallbackWrapper callbackWrapper, ActivityInstance completedInstance) {
		this.initialize(callbackWrapper, completedInstance);
	}

	public void initialize(CompletionCallbackWrapper callbackWrapper, ActivityInstance completedInstance) {
		super.reinitialize(callbackWrapper.getActivityInstance());
		this.callbackWrapper = callbackWrapper;
		this.completedInstance = completedInstance;
	}

	@Override
	protected void releaseToPool(ActivityExecutor executor) {
		super.clear();
		this.callbackWrapper = null;
		this.completedInstance = null;
		executor.CompletionWorkItemPool.release(this);
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		NativeActivityContext context = executor.NativeActivityContextPool.acquire();
		try {
			context.initialize(this.getActivityInstance(), executor, bookmarkManager);
			this.callbackWrapper.invoke(context, this.completedInstance);
		} catch (Exception e) {
			if (Helper.isFatal(e))
				throw e;
			this.setExceptionToPropagate(e);
		} finally {
			context.dispose();
			executor.NativeActivityContextPool.release(context);
		}
		return true;
	}
}
