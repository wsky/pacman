package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.Assign;

public class AssignDefinition extends ActivityDefinition {
	private InArgumentDefinition value;
	private OutArgumentDefinition to;

	public AssignDefinition() {
		this("Assign");
	}

	public AssignDefinition(String displayName) {
		super(displayName);
	}

	public AssignDefinition value(InArgumentDefinition value) {
		this.value = value;
		return this;
	}

	public AssignDefinition fromVar(String name) {
		return this.value(new InArgumentDefinition().fromVariable(name));
	}

	public AssignDefinition to(OutArgumentDefinition to) {
		this.to = to;
		return this;
	}

	public AssignDefinition toVar(String name) {
		return this.to(new OutArgumentDefinition().toVariable(name));
	}

	@Override
	public Activity toActivity() {
		Assign assign = new Assign();
		assign.Value = this.value.toArgument(this.getParent());
		assign.To = this.to.toArgument(this.getParent());
		assign.setDisplayName(this.displayName);
		return assign;
	}

}
