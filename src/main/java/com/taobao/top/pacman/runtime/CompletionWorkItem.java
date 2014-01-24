package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.ActivityExecutor;
import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.NativeActivityContext;

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
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		NativeActivityContext context = executor.NativeActivityContextPool.acquire();
		try {
			context.initialize(this.getActivityInstance(), executor, bookmarkManager);
			this.callbackWrapper.invoke(context, this.completedInstance);
		} catch (Exception e) {
			// FIXME assert fatal
			this.setExceptionToPropagate(e);
		} finally {
			context.dispose();
			executor.NativeActivityContextPool.release(context);
		}
		return true;
	}
}
