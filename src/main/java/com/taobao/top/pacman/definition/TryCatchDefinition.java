package com.taobao.top.pacman.definition;

import java.util.HashMap;
import java.util.Map;
import java.util.Map.Entry;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.statements.TryCatch;

public class TryCatchDefinition extends ActivityDefinition {
	private ActivityDefinition _try;
	private Map<Class<? extends Exception>, ActivityDefinition> _catches;
	private ActivityDefinition _finally;

	public TryCatchDefinition() {
		this("TryCatch");
	}

	public TryCatchDefinition(String displayName) {
		super(displayName);
		this._catches = new HashMap<Class<? extends Exception>, ActivityDefinition>();
	}

	public TryCatchDefinition Try(ActivityDefinition activity) {
		this._try = activity;
		this.addActivity(activity);
		return this;
	}

	public TryCatchDefinition Catch(Class<? extends Exception> exceptionType, ActivityDefinition activity) {
		this._catches.put(exceptionType, activity);
		this.addActivity(activity);
		return this;
	}

	public TryCatchDefinition Finally(ActivityDefinition activity) {
		this._finally = activity;
		this.addActivity(activity);
		return this;
	}

	@Override
	protected Activity internalToActivity(DefinitionValidator validator) {
		if (this._try == null)
			validator.addError("Try not set");
		if (validator.hasError())
			return null;

		TryCatch tryCatch = new TryCatch();
		tryCatch.Try = this._try.toActivity(validator);

		for (Entry<Class<? extends Exception>, ActivityDefinition> c : this._catches.entrySet())
			tryCatch.getCatches().add(
					new TryCatch.Catch(
							c.getKey(),
							c.getValue().toActivity(validator)));

		if (this._finally != null)
			tryCatch.Finally = this._finally.toActivity(validator);

		return tryCatch;
	}

	// fluent

	public ActivityDefinition Try() {
		this.Try(new ReferenceActivityDefinition("Try"));
		return this._try;
	}

	public ActivityDefinition Catch() {
		ActivityDefinition c = new ReferenceActivityDefinition("Catch");
		this.Catch(Exception.class, c);
		return c;
	}

	public ActivityDefinition Finally() {
		this.Finally(new ReferenceActivityDefinition("Finally"));
		return this._finally;
	}
}
