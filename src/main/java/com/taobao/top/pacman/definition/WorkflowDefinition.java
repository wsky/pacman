package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;

public class WorkflowDefinition extends ActivityContainerDefinition {
	public WorkflowDefinition(ActivityContainerDefinition parent) {
		super(parent);
	}

	public WorkflowDefinition in(String name) {
		return this;
	}

	public WorkflowDefinition out(String name) {
		return this;
	}

	@Override
	protected Activity toActivity() {
		return null;
	}

	public static WorkflowDefinition create() {
		return new WorkflowDefinition(null);
	}
}
