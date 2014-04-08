package com.taobao.top.pacman.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.Variable;

public abstract class ActivityDefinition {
	private ActivityDefinition parent;
	private Map<String, Variable> internalVariables;
	private String displayName;

	protected List<ActivityDefinition> activities;
	protected List<VariableDefinition> variables;

	public ActivityDefinition(String displayName) {
		this(displayName, null);
	}

	public ActivityDefinition(String displayName, ActivityDefinition parent) {
		this.displayName = displayName;
		this.setParent(parent);

		this.activities = new ArrayList<ActivityDefinition>();
		this.variables = new ArrayList<VariableDefinition>();
	}

	protected void addVariable(String name) {
		this.variables.add(new VariableDefinition(name));
	}

	protected void addActivity(ActivityDefinition activity) {
		activity.setParent(this);
		this.activities.add(activity);
	}

	protected void setParent(ActivityDefinition parent) {
		this.parent = parent;
	}

	protected Variable getVariable(String name) {
		Variable variable = this.internalVariables != null ? this.internalVariables.get(name) : null;
		return variable == null &&
				this.parent != null ?
				this.parent.getVariable(name) : variable;
	}

	protected void addVariable(String name, Variable variable) {
		if (this.internalVariables == null)
			this.internalVariables = new HashMap<String, Variable>();
		this.internalVariables.put(name, variable);
	}

	protected Map<String, Variable> getVariables() {
		return this.internalVariables;
	}

	protected abstract Activity internalToActivity(DefinitionValidator validator);

	@Override
	public String toString() {
		return this.getDisplayName() != null ? this.getDisplayName() : this.getClass().getSimpleName();
	}

	public final Activity toActivity(DefinitionValidator validator) {
		validator.setCurrent(this);
		
		Activity activity = this.internalToActivity(validator);
		if (activity != null && activity.getDisplayName() == null)
			activity.setDisplayName(this.getDisplayName());

		ProcessCallback callback = validator.getExtension(ProcessCallback.class);
		if (callback != null)
			callback.execute(this, activity);

		return activity;
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public ActivityDefinition End() {
		return this.parent;
	}

	public ActivityDefinition getParent() {
		return this.parent;
	}
	
	public ActivityDefinition Activity(ActivityDefinition activity) {
		this.addActivity(activity);
		return this;
	}

	// fluent

	public SequenceDefinition Sequence() {
		SequenceDefinition activity = new SequenceDefinition();
		this.Activity(activity);
		return activity;
	}

	public IfDefinition If() {
		IfDefinition activity = new IfDefinition();
		this.Activity(activity);
		return activity;
	}

	public WhileDefinition While() {
		WhileDefinition activity = new WhileDefinition();
		this.Activity(activity);
		return activity;
	}

	public TryCatchDefinition TryCatch() {
		TryCatchDefinition activity = new TryCatchDefinition();
		this.Activity(activity);
		return activity;
	}

	public WriteLineDefinition WriteLine() {
		WriteLineDefinition activity = new WriteLineDefinition();
		this.Activity(activity);
		return activity;
	}

	public AssignDefinition Assign() {
		AssignDefinition activity = new AssignDefinition();
		this.Activity(activity);
		return activity;
	}

	public static interface ProcessCallback {
		public void execute(ActivityDefinition definition, Activity activity);
	}
}
