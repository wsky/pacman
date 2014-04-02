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
	
	public ActivityContainerDefinition Var(String name) {
		this.variables.add(new VariableDefinition(name));
		return this;
	}

	public SequenceDefinition Sequence() {
		return this.Sequence("Sequence");
	}

	public SequenceDefinition Sequence(String displayName) {
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

	public WriteLineDefinition WriteLine() {
		return this.WriteLine("WriteLine");
	}

	public WriteLineDefinition WriteLine(String displayName) {
		WriteLineDefinition writeLine = new WriteLineDefinition(displayName);
		this.addActivity(writeLine);
		return writeLine;
	}

	public AssignDefinition Assign() {
		return this.Assign("Assign");
	}

	public AssignDefinition Assign(String displayName) {
		AssignDefinition assign = new AssignDefinition(displayName);
		this.addActivity(assign);
		return assign;
	}

	public WhileDefinition While() {
		return this.While("While");
	}

	public WhileDefinition While(String displayName) {
		WhileDefinition whileDefinition = new WhileDefinition(displayName);
		this.addActivity(whileDefinition);
		return whileDefinition;
	}

	protected void addActivity(ActivityDefinition activity) {
		activity.setParent(this);
		this.activities.add(activity);
	}

}
