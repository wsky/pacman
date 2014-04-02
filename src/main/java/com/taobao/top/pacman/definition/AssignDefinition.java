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

	public AssignDefinition Value(Object constValue) {
		return this.Value(new InArgumentDefinition(constValue));
	}
	
	public AssignDefinition Value(InlinedFunctionDefinition function) {
		return this.Value(new InArgumentDefinition(function));
	}

	public AssignDefinition Value(VariableReferenceDefinition variable) {
		return this.Value(new InArgumentDefinition(variable));
	}

	public AssignDefinition Value(InArgumentDefinition value) {
		this.value = value;
		return this;
	}

	public AssignDefinition To(OutArgumentDefinition to) {
		this.to = to;
		return this;
	}

	public AssignDefinition To(VariableReferenceDefinition variable) {
		return this.To(new OutArgumentDefinition(variable));
	}

	@Override
	protected Activity internalToActivity(DefinitionValidator validator) {
		if (this.value == null)
			validator.addError("Value not set");
		if (this.to == null)
			validator.addError("To not set");

		if (validator.hasError())
			return null;

		Assign assign = new Assign();
		assign.Value = this.value.toArgument(this.getParent(), validator);
		assign.To = this.to.toArgument(this.getParent(), validator);
		return assign;
	}
}
