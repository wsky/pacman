package com.taobao.top.pacman.definition;

import java.util.HashMap;
import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.Variable;

public abstract class ActivityDefinition {
	private ActivityDefinition parent;
	private Map<String, Variable> variables;
	private String displayName;

	public ActivityDefinition(String displayName) {
		this(displayName, null);
	}

	public ActivityDefinition(String displayName, ActivityDefinition parent) {
		this.displayName = displayName;
		this.setParent(parent);
	}

	public String getDisplayName() {
		return this.displayName;
	}

	public ActivityDefinition End() {
		return this.parent;
	}

	protected ActivityDefinition getParent() {
		return this.parent;
	}

	protected void setParent(ActivityDefinition parent) {
		this.parent = parent;
	}

	protected Variable getVariable(String name) {
		Variable variable = this.variables != null ? this.variables.get(name) : null;
		return variable == null &&
				this.parent != null ?
				this.parent.getVariable(name) : variable;
	}

	protected void addVariable(String name, Variable variable) {
		if (this.variables == null)
			this.variables = new HashMap<String, Variable>();
		this.variables.put(name, variable);
	}

	protected Map<String, Variable> getVariables() {
		return this.variables;
	}

	@Override
	public String toString() {
		return this.getDisplayName() != null ? this.getDisplayName() : this.getClass().getSimpleName();
	}

	public final Activity toActivity(DefinitionValidator validator) {
		validator.setCurrent(this);
		Activity activity = this.internalToActivity(validator);
		if (activity != null && activity.getDisplayName() == null)
			activity.setDisplayName(this.getDisplayName());
		return activity;
	}

	protected abstract Activity internalToActivity(DefinitionValidator validator);
}
