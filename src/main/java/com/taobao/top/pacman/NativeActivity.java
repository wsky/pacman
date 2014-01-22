package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.BookmarkManager;

public abstract class NativeActivity extends Activity {
	@Override
	protected final void internalExecute(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
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

	protected abstract void execute(NativeActivityContext context);

	protected void abort(NativeActivityAbortContext context) {
	}

	protected void cancel(NativeActivityContext context) {
		// Helper.Assert(context.IsCancellationRequested, "当前活动实例没有取消执行的请求");
		context.cancel();
	}
}