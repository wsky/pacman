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

	public WriteLineDefinition text(Object constValue) {
		return this.text(new InArgumentDefinition(constValue));
	}

	public WriteLineDefinition text(InArgumentDefinition text) {
		this.text = text;
		return this;
	}

	public WriteLineDefinition fromVar(String name) {
		return this.text(new InArgumentDefinition().fromVariable(name));
	}

	@Override
	protected Activity toActivity() {
		WriteLine writeLine = new WriteLine();
		writeLine.Text = this.text.toArgument(this.getParent());
		return writeLine;
	}
}
