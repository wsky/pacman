package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.statements.Sequence;

public class SequenceDefinition extends ActivityContainerDefinition {
	public SequenceDefinition(String displayName) {
		super(displayName);
	}

	@Override
	public SequenceDefinition Var(String name) {
		return (SequenceDefinition) super.Var(name);
	}

	public SequenceDefinition Activity(ActivityDefinition activity) {
		this.addActivity(activity);
		return this;
	}

	@Override
	protected Activity internalToActivity(DefinitionValidator validator) {
		Sequence sequence = new Sequence();
		sequence.setDisplayName(this.displayName);
		for (VariableDefinition variable : this.variables) {
			Variable var = variable.toVariable();
			this.addVariable(variable.getName(), var);
			sequence.getVariables().add(var);
		}
		for (ActivityDefinition activity : this.activities)
			sequence.getActivities().add(activity.toActivity(validator));
		return sequence;
	}
}
