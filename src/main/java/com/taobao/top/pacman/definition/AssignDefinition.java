package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.Assign;

public class AssignDefinition extends ActivityDefinition {
	private InArgumentDefinition value;
	private OutArgumentDefinition to;

	public AssignDefinition(String displayName) {
		super(displayName);
	}

	@Override
	protected Activity toActivity() {
		Assign assign = new Assign();
		assign.Value = this.value.toArgument(this.getParent());
		assign.To = this.to.toArgument();
		return assign;
	}

}
