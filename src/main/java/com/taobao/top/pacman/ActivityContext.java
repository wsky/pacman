package com.taobao.top.pacman;

public class ActivityContext {
	protected ActivityInstance currentInstance;
	protected ActivityExecutor executor;
	private Activity activity;

	protected ActivityContext() {
	}

	protected ActivityContext(ActivityInstance instance, ActivityExecutor executor) {
		this.reinitialize(instance, executor);
	}

	protected void reinitialize(ActivityInstance instance, ActivityExecutor executor) {
		this.currentInstance = instance;
		this.executor = executor;
		this.activity = this.currentInstance.getActivity();
	}

	public void dispose() {
		this.reinitialize(null, null);
	}

	protected Activity getActivity() {
		return this.activity;
	}

	protected LocationEnvironment getEnvironment() {
		return this.currentInstance.getEnvironment();
	}

	public Location getLocation(LocationReference locationReference) {
		return this.getEnvironment().getLocation(locationReference.getId());
	}

	public Object getValue(LocationReference locationReference) {
		return this.getLocation(locationReference).getValue();
	}

	public void setValue(LocationReference locationReference, Object value) {
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
