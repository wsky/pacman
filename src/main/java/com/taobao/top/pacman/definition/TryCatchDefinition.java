package com.taobao.top.pacman.definition;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.TryCatch;

public class TryCatchDefinition extends ActivityDefinition {
	private TryDefinition _try;
	private List<CatchDefinition> _catches;
	private FinallyDefinition _finally;

	public TryCatchDefinition() {
		this("TryCatch");
	}

	public TryCatchDefinition(String displayName) {
		super(displayName);
		this._catches = new ArrayList<CatchDefinition>();
	}

	public TryDefinition Try() {
		return this._try = new TryDefinition(this);
	}

	public CatchDefinition Catch() {
		return this.Catch(Exception.class);
	}

	public CatchDefinition Catch(Class<?> exceptionType) {
		CatchDefinition _catch = new CatchDefinition(this, exceptionType);
		this._catches.add(_catch);
		return _catch;
	}

	public FinallyDefinition Finally() {
		return this._finally = new FinallyDefinition(this);
	}

	@Override
	protected Activity internalToActivity(DefinitionValidator validator) {
		if (this._try == null)
			validator.addError("Try not set");
		if (validator.hasError())
			return null;

		TryCatch tryCatch = new TryCatch();
		tryCatch.Try = this._try.toActivity(validator);

		for (CatchDefinition c : this._catches)
			tryCatch.getCatches().add(c.toCatch(validator));

		if (this._finally != null)
			tryCatch.Finally = this._finally.toActivity(validator);

		return tryCatch;
	}

	public static class TryDefinition extends SingleActivityContainerDefinition<TryCatchDefinition> {
		public TryDefinition(TryCatchDefinition parent) {
			super("Try", parent);
		}

		@Override
		public TryCatchDefinition End() {
			return (TryCatchDefinition) super.End();
		}
	}

	public static class FinallyDefinition extends SingleActivityContainerDefinition<TryCatchDefinition> {
		public FinallyDefinition(TryCatchDefinition parent) {
			super("Finally", parent);
		}
	}

	public static class CatchDefinition extends SingleActivityContainerDefinition<TryCatchDefinition> {
		private Class<?> exceptionType;

		public CatchDefinition(TryCatchDefinition parent, Class<?> exceptionType) {
			super("Catch", parent);
			this.exceptionType = exceptionType;
		}

		public TryCatch.Catch toCatch(DefinitionValidator validator) {
			return new TryCatch.Catch(this.exceptionType, this.toActivity(validator));
		}
	}
}
