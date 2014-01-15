package com.taobao.top.pacman;

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

	public Object get(OutArgument outArgument) {
		// TODO Auto-generated method stub
		return null;
	}

	public void set(OutArgument outArgument, Object value) {
		// TODO Auto-generated method stub
	}

	public Object get(InArgument inArgument) {
		// TODO Auto-generated method stub
		return null;
	}

	public void set(InArgument inArgument, Object value) {
		// TODO Auto-generated method stub
		
	}

	// TODO design DataContext holding values

}
