package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.Activity;

public class IfDefinition extends ActivityDefinition {
	public IfDefinition(ActivityContainerDefinition parent) {
		super(parent);
	}

	public ActivityContainerDefinition endIf() {
		return (ActivityContainerDefinition) super.end();
	}

	public IfDefinition condition() {
		return this;
	}

	public ThenDefinition then() {
		return new ThenDefinition(this);
	}

	public ThenDefinition then(ActivityDefinition activity) {
		return new ThenDefinition(this);
	}
	
	public ElseDefinition Else() {
		return new ElseDefinition(this);
	}
	
	public ElseDefinition Else(ActivityDefinition activity) {
		return new ElseDefinition(this);
	}

	@Override
	protected Activity toActivity() {
		return null;
	}

	public static class ThenDefinition extends ActivityContainerDefinition {
		public ThenDefinition(IfDefinition parent) {
			super(parent);
		}

		public IfDefinition endThen() {
			return (IfDefinition) this.end();
		}

		@Override
		protected Activity toActivity() {
			return null;
		}
	}

	public static class ElseDefinition extends ActivityContainerDefinition {
		public ElseDefinition(IfDefinition parent) {
			super(parent);
		}

		public IfDefinition endElse() {
			return (IfDefinition) this.end();
		}

		@Override
		protected Activity toActivity() {
			return null;
		}
	}
}
