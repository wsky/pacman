package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.Sequence;

public class SequenceDefinition extends ActivityContainerDefinition {
	public SequenceDefinition(String displayName) {
		super(displayName);
	}

	public SequenceDefinition var(String name) {
		return (SequenceDefinition) super.var(name);
	}

	@Override
	protected Activity toActivity() {
		Sequence sequence = new Sequence();
		sequence.setDisplayName(this.displayName);
		for (VariableDefinition variable : this.variables)
			sequence.getVariables().add(variable.toVariable());
		for (ActivityDefinition activity : this.activities)
			sequence.getActivities().add(activity.toActivity());
		return sequence;
	}
}
