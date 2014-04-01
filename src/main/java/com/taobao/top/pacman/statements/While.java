package com.taobao.top.pacman.statements;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.*;
import com.taobao.top.pacman.expressions.FunctionValue;

public class While extends NativeActivity {
	private List<Variable> variables;
	private CompletionCallback onBodyComplete;
	private CompletionWithResultCallback<Boolean> onConditionComplete;

	public ActivityWithResult Condition;
	public Activity Body;

	public While() {
		super();
	}

	// FIXME should be Function<ActivityContext, Boolean>
	// see https://github.com/wsky/pacman/issues/6
	public While(Function<ActivityContext, Object> condition) {
		this();
		this.Condition = new FunctionValue(condition);
	}

	public List<Variable> getVariables() {
		if (this.variables == null)
			this.variables = new ArrayList<Variable>();
		return this.variables;
	}

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		metadata.setRuntimeVariables(this.getVariables());
		metadata.addChild(this.Body);
		metadata.addChild(this.Condition);
	}

	@Override
	protected void execute(NativeActivityContext context) {
		this.scheduleCondition(context);
	}

	private void scheduleCondition(NativeActivityContext context) {
		if (this.onConditionComplete == null) {
			this.onConditionComplete = new CompletionWithResultCallback<Boolean>() {
				@Override
				public void execute(NativeActivityContext context, ActivityInstance completedInstance, Boolean result) {
					onConditionComplete(context, completedInstance, result);
				}
			};
		}
		context.scheduleActivityWithResult(this.Condition, this.onConditionComplete);
	}

	private void onConditionComplete(NativeActivityContext context, ActivityInstance completedInstance, Boolean result) {
		if (result == null || !result)
			return;
		if (Body == null) {
			scheduleCondition(context);
			return;
		}
		if (onBodyComplete == null) {
			onBodyComplete = new CompletionCallback() {
				@Override
				public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
					scheduleCondition(context);
				}
			};
		}
		context.scheduleActivity(Body, onBodyComplete);
	}

}
