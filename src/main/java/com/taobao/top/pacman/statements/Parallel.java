package com.taobao.top.pacman.statements;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.*;
import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;

public class Parallel extends NativeActivity {
	private List<Activity> branches;
	private List<Variable> variables;
	private Variable hasCompleted;
	private CompletionWithResultCallback<Boolean> onConditionComplete;
	private CompletionCallback onBranchComplete;

	public ActivityWithResult CompletionCondition;

	public Parallel() {
		super();
		this.hasCompleted = new Variable();
	}

	public List<Activity> getBranches() {
		if (this.branches == null)
			this.branches = new ArrayList<Activity>();
		return this.branches;
	}

	public List<Variable> getVariables() {
		if (this.variables == null)
			this.variables = new ArrayList<Variable>();
		return this.variables;
	}

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		for (Activity branch : this.getBranches())
			metadata.addChild(branch);
		if (this.CompletionCondition != null)
			metadata.addChild(this.CompletionCondition);
		for (Variable variable : this.getVariables())
			metadata.addRuntimeVariable(variable);
		metadata.addImplementationVariable(this.hasCompleted);
	}

	@Override
	protected final void execute(NativeActivityContext context) {
		if (this.branches != null && this.branches.size() > 0) {
			if (this.onBranchComplete == null) {
				this.onBranchComplete = new CompletionCallback() {
					@Override
					public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
						onBranchComplete(context, completedInstance);
					}
				};
			}
			// sequence schedule
			for (int i = this.branches.size() - 1; i >= 0; i--)
				context.scheduleActivity(this.branches.get(i), this.onBranchComplete);
		}
	}

	@Override
	protected void cancel(NativeActivityContext context) {
		if (this.CompletionCondition == null)
			super.cancel(context);
		else
			context.cancelChildren();
	}

	private void onBranchComplete(NativeActivityContext context, ActivityInstance completedInstance) {
		if (this.CompletionCondition == null || (Boolean) this.hasCompleted.get(context))
			return;

		if (completedInstance.getState() != ActivityInstanceState.Closed && context.isCancellationRequested()) {
			context.markCanceled();
			this.hasCompleted.set(context, true);
			return;
		}

		if (this.onConditionComplete == null) {
			this.onConditionComplete = new CompletionWithResultCallback<Boolean>() {
				@Override
				public void execute(NativeActivityContext context, ActivityInstance completedInstance, Boolean result) {
					onConditionComplete(context, completedInstance, result);
				}
			};
		}
		context.scheduleActivityWithResult(this.CompletionCondition, this.onConditionComplete);
	}

	private void onConditionComplete(NativeActivityContext context, ActivityInstance completedInstance, Boolean result) {
		if (result) {
			context.cancelChildren();
			this.hasCompleted.set(context, true);
		}
	}
}
