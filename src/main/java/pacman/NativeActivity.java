package pacman;

import pacman.runtime.BookmarkManager;

public abstract class NativeActivity extends Activity {
	@Override
	protected final void internalExecute(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		NativeActivityContext context = executor.NativeActivityContextPool.acquire();
		try {
			context.initialize(instance, executor, bookmarkManager);
			this.execute(context);
		} finally {
			context.dispose();
			executor.NativeActivityContextPool.release(context);
		}
	}

	@Override
	protected final void internalAbort(ActivityInstance instance, ActivityExecutor executor, Exception terminationReason) {
		NativeActivityAbortContext context = new NativeActivityAbortContext(instance, executor, terminationReason);
		try {
			abort(context);
		} finally {
		}
	}

	@Override
	protected final void internalCancel(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
		NativeActivityContext context = executor.NativeActivityContextPool.acquire();
		try {
			context.initialize(instance, executor, bookmarkManager);
			this.cancel(context);
		} finally {
			context.dispose();
			executor.NativeActivityContextPool.release(context);
		}
	}

	protected abstract void execute(NativeActivityContext context) throws Exception;

	protected void abort(NativeActivityAbortContext context) {
	}

	protected void cancel(NativeActivityContext context) {
		Helper.assertFalse(context.isCancellationRequested());
		context.cancel();
	}
}