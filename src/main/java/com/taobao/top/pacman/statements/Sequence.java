package com.taobao.top.pacman.statements;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.*;

public class Sequence extends NativeActivity {
	private List<Activity> activities;
	private List<Variable> variables;
	private Variable lastIndexHint;
	private CompletionCallback onChildComplete;

	public Sequence() {
		super();
		this.lastIndexHint = new Variable("_lastIndexHint", 0);

	}

	public List<Activity> getActivities() {
		if (this.activities == null)
			this.activities = new ArrayList<Activity>();
		return this.activities;
	}

	public List<Variable> getVariables() {
		if (this.variables == null)
			this.variables = new ArrayList<Variable>();
		return this.variables;
	}

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		for (Activity activity : this.getActivities())
			metadata.addChild(activity);
		for (Variable variable : this.getVariables())
			metadata.addRuntimeVariable(variable);
		metadata.addImplementationVariable(this.lastIndexHint);
	}

	@Override
	protected void execute(NativeActivityContext context) {
		if (this.activities != null && this.activities.size() > 0)
			context.scheduleActivity(this.activities.get(0), this.onChildComplete());
	}

	private void InternalExecute(NativeActivityContext context, ActivityInstance completedInstance) {
		int completedInstanceIndex = (Integer) this.lastIndexHint.get(context);

		if (completedInstanceIndex >= this.activities.size() ||
				this.activities.get(completedInstanceIndex) != completedInstance.getActivity())
			completedInstanceIndex = this.activities.indexOf(completedInstance.getActivity());

		int nextChildIndex = completedInstanceIndex + 1;

		if (nextChildIndex == this.activities.size())
			return;

		Activity nextChild = this.activities.get(nextChildIndex);
		context.scheduleActivity(nextChild, this.onChildComplete());
		this.lastIndexHint.set(context, nextChildIndex);
	}

	private CompletionCallback onChildComplete() {
		if (this.onChildComplete == null) {
			this.onChildComplete = new CompletionCallback() {
				@Override
				public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
					InternalExecute(context, completedInstance);
				}
			};
		}
		return this.onChildComplete;
	}
}
