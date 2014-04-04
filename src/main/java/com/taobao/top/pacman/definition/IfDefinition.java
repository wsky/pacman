package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.If;

public class IfDefinition extends ActivityDefinition {
	protected InArgumentDefinition condition;
	protected ActivityDefinition then;
	protected ActivityDefinition _else;

	public IfDefinition() {
		super("If");
	}

	public IfDefinition(String displayName) {
		super(displayName);
	}

	public IfDefinition Condition() {
		return this.Condition(new InArgumentDefinition(true));
	}

	public IfDefinition Condition(VariableReferenceDefinition variable) {
		return this.Condition(new InArgumentDefinition(variable));
	}

	public IfDefinition Condition(InlinedFunctionDefinition function) {
		return this.Condition(new InArgumentDefinition(function));
	}

	public IfDefinition Condition(InArgumentDefinition condition) {
		this.condition = condition;
		return this;
	}

	public IfDefinition Then(ActivityDefinition activity) {
		this.then = activity;
		this.addActivity(activity);
		return this;
	}

	public IfDefinition Else(ActivityDefinition activity) {
		this._else = activity;
		this.addActivity(activity);
		return this;
	}

	@Override
	protected Activity internalToActivity(DefinitionValidator validator) {
		if (this.condition == null)
			validator.addError("Condition not set");
		if (validator.hasError())
			return null;

		If _if = new If();
		_if.setDisplayName(this.getDisplayName());
		_if.Condition = this.condition.toArgument(this.getParent(), validator);
		if (this.then != null)
			_if.Then = this.then.toActivity(validator);
		if (this._else != null)
			_if.Else = this._else.toActivity(validator);
		return _if;
	}

	// fluent

	public ActivityDefinition Then() {
		this.Then(new ReferenceActivityDefinition("Then"));
		return this.then;
	}

	public ActivityDefinition Else() {
		this.Else(new ReferenceActivityDefinition("Else"));
		return this._else;
	}
}
