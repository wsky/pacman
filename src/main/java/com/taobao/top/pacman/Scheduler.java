package com.taobao.top.pacman;

import com.taobao.top.pacman.*;
import com.taobao.top.pacman.runtime.Quack;
import com.taobao.top.pacman.runtime.WorkItem;

public class Scheduler {
	private ActivityExecutor executor;
	private WorkItem firstWorkItem;
	private Quack<WorkItem> workItemQueue;
	private boolean isRunning;

	public Scheduler(ActivityExecutor executor) {
		this.executor = executor;
	}

	public void markRunning() {
		this.isRunning = true;
	}

	public void resume() throws Exception {
		if (this.isIdle()) {

		}
		// NOTE 2 schedule work
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

	private boolean executeWorkItem(WorkItem workItem) throws Exception {
		boolean flag = this.executor.onExecuteWorkItem(workItem);
		// NOTE 5 cleanup and return workItem to pool
		workItem.dispose(this.executor);
		return flag;
	}

	private void scheduleIdle() throws Exception {
		this.executor.onSchedulerIdle();
	}

	private void notifyUnhandledException(Exception exception, ActivityInstance source) throws Exception {
		this.executor.notifyUnhandledException(exception, source);
	}

	private boolean isIdle() {
		return this.firstWorkItem == null;
	}

	public static void onScheduledWork(Scheduler scheduler) throws Exception {
		boolean flag = true;

		while (flag) {
			if (scheduler.isIdle())
				break;

			WorkItem currentWorkItem = scheduler.firstWorkItem;

			scheduler.firstWorkItem = scheduler.workItemQueue != null &&
					scheduler.workItemQueue.count() > 0 ?
					scheduler.workItemQueue.dequeue() :
					null;

			flag = scheduler.executeWorkItem(currentWorkItem);
		}

		// we must process events or dispose workflow resources until idle
		// FIXME impl logic after idle, raise workflowInstance pausing or complete

		// idle or paused
		if (scheduler.isIdle()) {
			scheduler.isRunning = false;
			scheduler.scheduleIdle();
		}

		// if(!yieldSilentlyAction)
		// scheduler.notifyUnhandledException(exception, source);
	}

	// internal abstract class RequestedAction
	// {
	// protected RequestedAction()
	// {
	// }
	// }
	//
	// class ContinueAction : RequestedAction
	// {
	// public ContinueAction()
	// {
	// }
	// }
	//
	// class YieldSilentlyAction : RequestedAction
	// {
	// public YieldSilentlyAction()
	// {
	// }
	// }
	//
	// class AbortAction : RequestedAction
	// {
	// public AbortAction()
	// {
	// }
	// }
	//
	// class NotifyUnhandledExceptionAction : RequestedAction
	// {
	// public NotifyUnhandledExceptionAction(Exception exception, ActivityInstance source)
	// {
	// this.Exception = exception;
	// this.Source = source;
	// }
	//
	// public Exception Exception
	// {
	// get;
	// private set;
	// }
	//
	// public ActivityInstance Source
	// {
	// get;
	// private set;
	// }
	// }
}
