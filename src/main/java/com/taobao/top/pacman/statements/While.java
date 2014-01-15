package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.*;

public class While extends NativeActivity {
	private CompletionCallback onBodyComplete;

	public InArgument Condition;
	public Activity Body;

	public While() {
		super();
		this.onBodyComplete = new CompletionCallback() {
			@Override
			public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
				onBodyComplete(context, completedInstance);
			}
		};
	}

	@Override
	protected void execute(NativeActivityContext context) {
		this.onBodyComplete(context, null);
	}

	private void onBodyComplete(NativeActivityContext context, ActivityInstance completedInstance) {
		if (this.Body != null && (Boolean) this.Condition.get(context))
			context.scheduleActivity(this.Body, this.onBodyComplete);
	}

}
