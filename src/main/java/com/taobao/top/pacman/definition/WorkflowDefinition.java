package com.taobao.top.pacman.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.Argument;
import com.taobao.top.pacman.CompletionCallback;
import com.taobao.top.pacman.FaultCallback;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.NativeActivityFaultContext;
import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class WorkflowDefinition extends ActivityContainerDefinition {
	protected Map<String, Object> inArguments;
	protected Map<String, Object> outArguments;
	protected Map<String, Argument> arguments;
	protected ActivityDefinition body;

	public WorkflowDefinition(String displayName, ActivityContainerDefinition parent) {
		super(displayName, parent);
	}

	public WorkflowDefinition In(String name) {
		return this.In(name, null);
	}

	public WorkflowDefinition In(String name, Object defaultValue) {
		if (this.inArguments == null)
			this.inArguments = new HashMap<String, Object>();
		this.inArguments.put(name, defaultValue);
		this.addVariable(name, new Variable(name));
		return this;
	}

	public WorkflowDefinition Out(String name) {
		if (this.outArguments == null)
			this.outArguments = new HashMap<String, Object>();
		this.outArguments.put(name, null);
		this.addVariable(name, new Variable(name));
		return this;
	}

	@Override
	public WorkflowDefinition Var(String name) {
		return (WorkflowDefinition) super.Var(name);
	}

	@Override
	public Activity toActivity() {
		if (this.variables != null)
			for (VariableDefinition var : this.variables)
				this.addVariable(var.getName(), var.toVariable());

		Activity workflow = new NativeActivity() {
			private OutArgument exception;
			private Activity body;
			private CompletionCallback onCompleted;
			private FaultCallback onFaulted;

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				Map<String, Variable> map = getVariables();
				if (map != null)
					for (Variable var : map.values())
						metadata.addRuntimeVariable(var);

				if (inArguments != null) {
					for (Entry<String, Object> e : inArguments.entrySet()) {
						if (arguments == null)
							arguments = new HashMap<String, Argument>();
						arguments.put(e.getKey(), new InArgument());
						metadata.bindAndAddArgument(
								arguments.get(e.getKey()),
								new RuntimeArgument(e.getKey(), Object.class, ArgumentDirection.In));
					}
				}

				if (outArguments != null) {
					for (Entry<String, Object> e : outArguments.entrySet()) {
						if (arguments == null)
							arguments = new HashMap<String, Argument>();
						arguments.put(e.getKey(), new OutArgument());
						metadata.bindAndAddArgument(
								arguments.get(e.getKey()),
								new RuntimeArgument(e.getKey(), Object.class, ArgumentDirection.Out));
					}
				}

				metadata.bindAndAddArgument(this.exception = new OutArgument(),
						new RuntimeArgument("exception", Exception.class, ArgumentDirection.Out));

				this.body = WorkflowDefinition.this.body.toActivity();
				metadata.addChild(this.body);
			}

			@Override
			protected void execute(NativeActivityContext context) {
				// init variable from inputs
				Map<String, Variable> map = getVariables();
				if (map != null && arguments != null)
					for (Entry<String, Variable> e : map.entrySet())
						if (arguments.containsKey(e.getKey()))
							e.getValue().set(context, arguments.get(e.getKey()).get(context));

				if (this.onCompleted == null) {
					this.onCompleted = new CompletionCallback() {
						@Override
						public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
							onCompleted(context, completedInstance);
						}
					};
				}

				if (this.onFaulted == null) {
					this.onFaulted = new FaultCallback() {
						@Override
						public void execute(NativeActivityFaultContext faultContext, Exception propagatedException, ActivityInstance propagatedFrom) {
							onFaulted(faultContext, propagatedException, propagatedFrom);
						}
					};
				}

				context.scheduleActivity(this.body, this.onCompleted, this.onFaulted);
			}

			protected void onCompleted(NativeActivityContext context, ActivityInstance completedInstance) {
				if (completedInstance.getState() != ActivityInstanceState.Closed)
					return;

				// pick variables render to outArguments
				// as arugmentReference not support currently
				if (outArguments != null)
					for (String var : outArguments.keySet())
						arguments.get(var).set(context, getVariable(var).get(context));
			}

			protected void onFaulted(
					NativeActivityFaultContext faultContext,
					Exception propagatedException,
					ActivityInstance propagatedFrom) {
				this.exception.set(faultContext, propagatedException);
				faultContext.handleFault();
			}
		};
		workflow.setDisplayName(this.displayName);
		return workflow;
	}

	@Override
	protected void addActivity(ActivityDefinition activity) {
		super.addActivity(activity);
		if (body == null)
			this.body = activity;
	}

	public static WorkflowDefinition Create() {
		return new WorkflowDefinition("Workflow", null);
	}

	public static WorkflowDefinition Create(String displayName) {
		return new WorkflowDefinition(displayName, null);
	}
}