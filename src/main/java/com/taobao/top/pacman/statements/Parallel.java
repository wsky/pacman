package com.taobao.top.pacman.statements;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.*;

public class Parallel extends NativeActivity {
	private List<Activity> branches;
	private Variable hasCompleted;
	private CompletionCallback onConditionComplete;

	public ActivityWithResult CompletionCondition;

	public Parallel() {
		this.hasCompleted = new Variable();
	}

	public List<Activity> getBranches() {
		if (this.branches == null)
			this.branches = new ArrayList<Activity>();
		return this.branches;
	}

	@Override
	protected final void execute(NativeActivityContext context) {
		if (this.branches != null && this.branches.size() > 0)
			for (int i = this.branches.size() - 1; i >= 0; i--)
				context.scheduleActivity(this.branches.get(i), new CompletionCallback() {
					@Override
					public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
						onBranchComplete(context, completedInstance);
					}
				});
	}

	protected void onHasCompleted(NativeActivityContext context, ActivityInstance completedInstance) {
	}

	private void onBranchComplete(NativeActivityContext context, ActivityInstance completedInstance)
	{
		if (this.CompletionCondition != null && !(Boolean) this.hasCompleted.get(context)) {
			if (this.onConditionComplete == null)
				this.onConditionComplete = new CompletionCallback() {
					@Override
					public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
						onConditionComplete(context, completedInstance);
					}
				};
			context.scheduleActivity(this.CompletionCondition, this.onConditionComplete);
		}
	}

	private void onConditionComplete(NativeActivityContext context, ActivityInstance completedInstance) {
		this.onHasCompleted(context, completedInstance);
		context.cancelChildren();
		this.hasCompleted.set(context, true);
	}
}
