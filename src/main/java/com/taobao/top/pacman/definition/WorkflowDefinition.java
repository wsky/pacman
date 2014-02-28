package com.taobao.top.pacman.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.Argument;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class WorkflowDefinition extends ActivityContainerDefinition {
	private Map<String, Object> inArguments;
	private Map<String, Object> outArguments;
	private Map<String, Argument> arguments;
	private ActivityDefinition body;

	public WorkflowDefinition(String displayName, ActivityContainerDefinition parent) {
		super(displayName, parent);
	}

	public WorkflowDefinition in(String name) {
		return this.in(name, null);
	}

	public WorkflowDefinition in(String name, Object defaultValue) {
		if (this.inArguments == null)
			this.inArguments = new HashMap<String, Object>();
		this.inArguments.put(name, defaultValue);
		this.addVariable(name, new Variable(name));
		return this;
	}

	public WorkflowDefinition out(String name) {
		if (this.outArguments == null)
			this.outArguments = new HashMap<String, Object>();
		this.outArguments.put(name, null);
		this.addVariable(name, new Variable(name));
		return this;
	}

	@Override
	protected Activity toActivity() {
		return new NativeActivity() {
			private Activity body;

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				Map<String, Variable> map = getVariables();
				if (map != null)
					for (Variable var : map.values())
						metadata.addRuntimeVariable(var);

				if (inArguments != null)
					for (Entry<String, Object> e : inArguments.entrySet()) {
						if (arguments == null)
							arguments = new HashMap<String, Argument>();
						arguments.put(e.getKey(), new InArgument());
						metadata.bindAndAddArgument(
								arguments.get(e.getKey()),
								new RuntimeArgument(e.getKey(), Object.class, ArgumentDirection.In));
					}

				if (outArguments != null)
					for (Entry<String, Object> e : outArguments.entrySet())
						metadata.bindAndAddArgument(
								new OutArgument(),
								new RuntimeArgument(e.getKey(), Object.class, ArgumentDirection.Out));

				this.body = WorkflowDefinition.this.body.toActivity();
				metadata.addChild(this.body);
			}

			@Override
			protected void execute(NativeActivityContext context) {
				Map<String, Variable> map = getVariables();
				if (map != null && arguments != null)
					for (Entry<String, Variable> e : map.entrySet())
						if (arguments.containsKey(e.getKey()))
							e.getValue().set(context, arguments.get(e.getKey()).get(context));

				context.scheduleActivity(this.body);
			}
		};
	}

	public static WorkflowDefinition create() {
		return new WorkflowDefinition("workflow", null);
	}

	public static WorkflowDefinition create(String displayName) {
		return new WorkflowDefinition(displayName, null);
	}

	@Override
	protected void addActivity(ActivityDefinition activity) {
		super.addActivity(activity);
		if (body == null)
			this.body = activity;
	}
}
