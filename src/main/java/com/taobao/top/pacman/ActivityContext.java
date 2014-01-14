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

}
