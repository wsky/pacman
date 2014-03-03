package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.If;

public class IfDefinition extends ActivityDefinition {
	protected InArgumentDefinition condition;
	protected ThenDefinition then;
	protected ElseDefinition _else;

	public IfDefinition(String displayName) {
		super(displayName);
	}

	public ActivityContainerDefinition endIf() {
		return (ActivityContainerDefinition) super.end();
	}

	public IfDefinition condition() {
		return this.condition(new InArgumentDefinition(true));
	}

	public IfDefinition condition(InArgumentDefinition condition) {
		this.condition = condition;
		return this;
	}

	public ThenDefinition then() {
		this.then = new ThenDefinition(null, this);
		return this.then;
	}

	public IfDefinition then(ActivityDefinition activity) {
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
	public Activity toActivity() {
		If _if = new If();
		_if.Condition = this.condition.toArgument(this.getParent());
		_if.Then = this.then.toActivity();
		_if.Else = this._else.toActivity();
		return _if;
	}

	public static class ThenDefinition extends ActivityContainerDefinition {
		private ActivityDefinition activity;

		public ThenDefinition(String displayName, IfDefinition parent) {
			super(displayName, parent);
		}

		public IfDefinition endThen() {
			return (IfDefinition) this.end();
		}

		@Override
		public Activity toActivity() {
			return this.activity.toActivity();
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

		public IfDefinition endElse() {
			return (IfDefinition) this.end();
		}

		@Override
		public Activity toActivity() {
			return this.activity.toActivity();
		}

		@Override
		protected void addActivity(ActivityDefinition activity) {
			this.activity = activity;
			super.addActivity(activity);
		}
	}
}
