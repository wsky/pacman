package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.If;

public class IfDefinition extends ActivityDefinition {
	protected InArgumentDefinition condition;
	protected ThenDefinition then;
	protected ElseDefinition _else;

	public IfDefinition() {
		super("If");
	}

	public IfDefinition(String displayName) {
		super(displayName);
	}

	public ActivityContainerDefinition EndIf() {
		return (ActivityContainerDefinition) super.End();
	}

	public IfDefinition Condition() {
		return this.Condition(new InArgumentDefinition(true));
	}

	public IfDefinition Condition(VariableReferenceDefinition variable) {
		return this.Condition(new InArgumentDefinition(variable));
	}

	public IfDefinition Condition(FunctionDefinition function) {
		return this.Condition(new InArgumentDefinition(function));
	}

	public IfDefinition Condition(InArgumentDefinition condition) {
		this.condition = condition;
		return this;
	}

	public ThenDefinition Then() {
		this.then = new ThenDefinition(null, this);
		return this.then;
	}

	public IfDefinition Then(ActivityDefinition activity) {
		this.then = new ThenDefinition(null, this);
		this.then.addActivity(activity);
		return this;
	}

	public ElseDefinition Else() {
		this._else = new ElseDefinition(null, this);
		return this._else;
	}

	public IfDefinition Else(ActivityDefinition activity) {
		this._else = new ElseDefinition(null, this);
		this._else.addActivity(activity);
		return this;
	}

	@Override
	protected Activity internalToActivity(DefinitionValidator validator) {
		if (this.condition == null)
			validator.addError("Condition not set");
		if (validator.hasError())
			return null;

		If _if = new If();
		_if.Condition = this.condition.toArgument(this.getParent(), validator);
		if (this.then != null)
			_if.Then = this.then.toActivity(validator);
		if (this._else != null)
			_if.Else = this._else.toActivity(validator);
		return _if;
	}

	public static class ThenDefinition extends ActivityContainerDefinition {
		private ActivityDefinition activity;

		public ThenDefinition(String displayName, IfDefinition parent) {
			super(displayName, parent);
		}

		public IfDefinition EndThen() {
			return (IfDefinition) this.End();
		}

		@Override
		protected Activity internalToActivity(DefinitionValidator validator) {
			return this.activity != null ? this.activity.toActivity(validator) : null;
		}

		@Override
		protected void addActivity(ActivityDefinition activity) {
			this.activity = activity;
			super.addActivity(activity);
		}
	}

	public static class ElseDefinition extends ActivityContainerDefinition {
		private ActivityDefinition activity;

		public ElseDefinition(String displayName, IfDefinition parent) {
			super(displayName, parent);
		}

		public IfDefinition EndElse() {
			return (IfDefinition) this.End();
		}

		@Override
		protected Activity internalToActivity(DefinitionValidator validator) {
			return this.activity != null ? this.activity.toActivity(validator) : null;
		}

		@Override
		protected void addActivity(ActivityDefinition activity) {
			this.activity = activity;
			super.addActivity(activity);
		}
	}
}
