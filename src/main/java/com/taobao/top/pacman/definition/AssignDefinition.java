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

	public AssignDefinition Value(InArgumentDefinition value) {
		this.value = value;
		return this;
	}

	public AssignDefinition From(String name) {
		return this.Value(new InArgumentDefinition().FromVariable(name));
	}

	public AssignDefinition To(OutArgumentDefinition to) {
		this.to = to;
		return this;
	}

	public AssignDefinition To(String name) {
		return this.To(new OutArgumentDefinition().ToVariable(name));
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
