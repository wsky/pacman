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

	public void abortWorkflowInstance(Exception reason) {
		// TODO Auto-generated method stub
		
	}

	public void abortActivityInstance(ActivityInstance activity, Exception reason) {
		// TODO Auto-generated method stub
		
	}

	public void cancelActivity(ActivityInstance activityInstance) {
		// TODO Auto-generated method stub
		
	}

	public void scheduleCompletionBookmark(Bookmark bookmark, Object value) {
		// TODO Auto-generated method stub
		
	}

	public ActivityInstance ScheduleActivity(Activity activity, ActivityInstance currentInstance, CompletionCallbackWrapper completionCallbackWrapper, FaultCallbackWrapper faultCallbackWrapper) {
		// TODO Auto-generated method stub
		return null;
	}
}
