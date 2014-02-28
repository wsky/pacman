package com.taobao.top.pacman.definition;

import java.util.ArrayList;
import java.util.List;

public abstract class ActivityContainerDefinition extends ActivityDefinition {
	protected List<ActivityDefinition> activities;
	protected List<VariableDefinition> variables;

	public ActivityContainerDefinition(String displayName) {
		this(displayName, null);
	}

	public ActivityContainerDefinition(String displayName, ActivityDefinition parent) {
		super(displayName, parent);
		this.activities = new ArrayList<ActivityDefinition>();
		this.variables = new ArrayList<VariableDefinition>();
	}

	public ActivityContainerDefinition var(String name) {
		this.variables.add(new VariableDefinition(name));
		return this;
	}

	public SequenceDefinition sequence() {
		return this.sequence("Sequence");
	}

	public SequenceDefinition sequence(String displayName) {
		SequenceDefinition sequence = new SequenceDefinition(displayName);
		this.addActivity(sequence);
		return sequence;
	}

	public IfDefinition If() {
		return this.If("If");
	}

	public IfDefinition If(String displayName) {
		IfDefinition _if = new IfDefinition(displayName);
		this.addActivity(_if);
		return _if;
	}

	public WriteLineDefinition writeLine() {
		return this.writeLine("WriteLine");
	}

	public WriteLineDefinition writeLine(String displayName) {
		WriteLineDefinition writeLine = new WriteLineDefinition(displayName);
		this.addActivity(writeLine);
		return writeLine;
	}

	public AssignDefinition assign() {
		return this.assign("Assign");
	}

	public AssignDefinition assign(String displayName) {
		AssignDefinition assign = new AssignDefinition(displayName);
		this.addActivity(assign);
		return assign;
	}

	protected void addActivity(ActivityDefinition activity) {
		activity.setParent(this);
		this.activities.add(activity);
	}

}
