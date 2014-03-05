package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.WriteLine;

public class WriteLineDefinition extends ActivityDefinition {
	private InArgumentDefinition text;

	public WriteLineDefinition() {
		this("WriteLine");
	}

	public WriteLineDefinition(String displayName) {
		this(displayName, null);
	}

	public WriteLineDefinition(String displayName, ActivityDefinition parent) {
		super(displayName, parent);
	}

	public WriteLineDefinition Text(Object constValue) {
		return this.Text(new InArgumentDefinition(constValue));
	}

	public WriteLineDefinition Text(InArgumentDefinition text) {
		this.text = text;
		return this;
	}

	public WriteLineDefinition From(String name) {
		return this.Text(new InArgumentDefinition().FromVariable(name));
	}

	@Override
	public Activity toActivity() {
		WriteLine writeLine = new WriteLine();
		writeLine.Text = this.text.toArgument(this.getParent());
		writeLine.setDisplayName(this.displayName);
		return writeLine;
	}
}