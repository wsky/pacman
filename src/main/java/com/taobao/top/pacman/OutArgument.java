package com.taobao.top.pacman;

public class OutArgument extends Argument {
	//FIXME impl VariableReference
	private Variable variable;

	public OutArgument() {
	}

	public OutArgument(Variable variable) {
		this();
		this.variable = variable;
	}

	public Object get(ActivityContext context) {
		return this.variable == null ? context.get(this) : this.variable.get(context);
	}

	public void set(ActivityContext context, Object value) {
		if (this.variable == null)
			context.set(this, value);
		else
			this.variable.set(context, value);
	}

}
