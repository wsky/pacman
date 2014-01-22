package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.CompletionCallbackWrapper;
import com.taobao.top.pacman.runtime.FaultCallbackWrapper;

public class ActivityExecutor {
	public Pool<NativeActivityContext> NativeActivityContextPool = new Pool<NativeActivityContext>() {
		@Override
		protected NativeActivityContext createNew() {
			return null;
		}
	};

	public Pool<CodeActivityContext> CodeActivityContextPool = new Pool<CodeActivityContext>() {
		@Override
		protected CodeActivityContext createNew() {
			return null;
		}
	};

	public void abortWorkflowInstance(Exception reason) {

	}

	public void abortActivityInstance(ActivityInstance activity, Exception reason) {

	}

	public void cancelActivity(ActivityInstance activityInstance) {

	}

	public void scheduleCompletionBookmark(Bookmark bookmark, Object value) {

	}

	public ActivityInstance ScheduleActivity(Activity activity, ActivityInstance currentInstance, CompletionCallbackWrapper completionCallbackWrapper, FaultCallbackWrapper faultCallbackWrapper) {
		return null;
	}
}
