package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;

public class Scheduler {
	private ActivityExecutor executor;
	private WorkItem firstWorkItem;
	private Quack<WorkItem> workItemQueue;
	private boolean isRunning;
	private boolean isIdle;

	public Scheduler(ActivityExecutor executor) {
		this.executor = executor;
	}

	public void markRunning() {
		this.isRunning = true;
	}

	public void resume() {
		if (this.isIdle) {

		}
		//NOTE 2 schedule work
		onScheduledWork(this);
	}

	public void pushWork(WorkItem workItem) {
		if (this.firstWorkItem == null)
			this.firstWorkItem = workItem;
		else {
			if (this.workItemQueue == null)
				this.workItemQueue = new Quack<WorkItem>();
			this.workItemQueue.pushFront(this.firstWorkItem);
			this.firstWorkItem = workItem;
		}
	}

	public void enqueueWork(WorkItem workItem) {
		if (this.firstWorkItem == null)
			this.firstWorkItem = workItem;
		else {
			if (this.workItemQueue == null)
				this.workItemQueue = new Quack<WorkItem>();
			this.workItemQueue.enqueue(workItem);
		}
	}

	private boolean executeWorkItem(WorkItem workItem) {
		boolean flag = this.executor.onExecuteWorkItem(workItem);
		//NOTE 5 cleanup and return workItem to pool
		workItem.dispose(this.executor);
		return flag;
	}

	private void scheduleIdle() {
		// TODO impl schedule idle
		// this.executor.onSchedulerIdle();
	}

	public static void onScheduledWork(Scheduler scheduler) {
		boolean flag = true;

		while (flag) {
			if (scheduler.isIdle || !scheduler.isRunning)
				break;

			WorkItem currentWorkItem = scheduler.firstWorkItem;

			scheduler.firstWorkItem = scheduler.workItemQueue != null &&
					scheduler.workItemQueue.count() > 0 ?
					scheduler.workItemQueue.dequeue() :
					null;

			flag = scheduler.executeWorkItem(currentWorkItem);
		}

		if (scheduler.isIdle) {
			scheduler.isRunning = false;
			scheduler.scheduleIdle();
		}
	}
}
