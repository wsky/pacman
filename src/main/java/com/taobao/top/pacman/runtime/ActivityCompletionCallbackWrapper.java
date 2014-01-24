package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.CompletionCallback;
import com.taobao.top.pacman.Delegate;
import com.taobao.top.pacman.NativeActivityContext;

public class ActivityCompletionCallbackWrapper extends CompletionCallbackWrapper {

	public ActivityCompletionCallbackWrapper(Delegate delegate, ActivityInstance activityInstance) {
		super(delegate, activityInstance);
	}

	@Override
	protected void invoke(NativeActivityContext context, ActivityInstance completedInstance) {
		((CompletionCallback) this.delegate).execute(context, completedInstance);
	}

}
