package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.LocationEnvironment;

public class ActivityContext {
	protected ActivityInstance currentInstance;
	protected ActivityExecutor executor;

	protected ActivityContext() {
	}

	protected ActivityContext(ActivityInstance instance, ActivityExecutor executor) {
		this.reinitialize(instance, executor);
	}

	protected void reinitialize(ActivityInstance instance, ActivityExecutor executor) {
		this.currentInstance = instance;
		this.executor = executor;
	}

	protected void dispose() {
		this.reinitialize(null, null);
	}

	protected Activity getActivity() {
		return this.currentInstance.getActivity();
	}

	protected LocationEnvironment getEnvironment() {
		return this.currentInstance.getEnvironment();
	}

	protected Location getLocation(LocationReference locationReference) {
		return this.getEnvironment().getLocation(locationReference.getId());
	}

	protected Object getValue(LocationReference locationReference) {
		return this.getLocation(locationReference).getValue();
	}

	protected void setValue(LocationReference locationReference, Object value) {
		this.getLocation(locationReference).setValue(value);
	}

	public Object get(Argument argument) {
		return this.getValue(argument.getRuntimeArgument());
	}

	public void set(Argument argument, Object value) {
		this.setValue(argument.getRuntimeArgument(), value);
	}

	public Object get(Variable variable) {
		return this.getValue(variable);
	}

	public void set(Variable variable, Object value) {
		this.setValue(variable, value);
	}
}
