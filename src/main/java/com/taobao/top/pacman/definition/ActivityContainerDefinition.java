package com.taobao.top.pacman.definition;

public abstract class ActivityContainerDefinition extends ActivityDefinition {
	public ActivityContainerDefinition(ActivityDefinition parent) {
		super(parent);
	}

	public SequenceDefinition sequence() {
		return new SequenceDefinition(this);
	}

	public IfDefinition If() {
		return new IfDefinition(this);
	}

	public WriteLineDefinition writeLine() {
		return new WriteLineDefinition(this);
	}

	public AssignDefinition assign() {
		return new AssignDefinition(this);
	}

	public WhileDefinition While() {
		return new WhileDefinition();
	}
}
