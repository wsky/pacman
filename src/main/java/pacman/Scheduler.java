package pacman;

import pacman.runtime.Quack;
import pacman.runtime.WorkItem;

public class Scheduler {
	public static final ContinueAction CONTINUE_ACTION = new ContinueAction();
	public static final YieldSilentlyAction YIELD_SILENTLY_ACTION = new YieldSilentlyAction();
	public static final AbortAction ABORT_ACTION = new AbortAction();

	private ActivityExecutor executor;
	private WorkItem firstWorkItem;
	private Quack<WorkItem> workItemQueue;
	private boolean isRunning;
	private boolean isPausing;

	public Scheduler(ActivityExecutor executor) {
		this.executor = executor;
	}

	public boolean isIdle() {
		return this.firstWorkItem == null;
	}

	public boolean isRunning() {
		return this.isRunning;
	}

	public void markRunning() {
		this.isRunning = true;
	}

	public void pause() {
		this.isPausing = true;
	}

	public void resume() throws Exception {
		if (this.isIdle() || this.isPausing) {
			this.isPausing = false;
			this.isRunning = false;
			this.scheduleIdle();
		} else
			// NOTE 2 schedule work
			onScheduledWork(this);
	}

	public void pushWork(WorkItem workItem) {
		if (this.firstWorkItem == null)
			this.firstWorkItem = workItem;
		else {
			if (this.workItemQueue == null)
				this.workItemQueue = new Quack<WorkItem>() {
				};
			this.workItemQueue.pushFront(this.firstWorkItem);
			this.firstWorkItem = workItem;
		}

		if (Trace.isEnabled())
			Trace.traceWorkItemScheduled(workItem);
	}

	public void enqueueWork(WorkItem workItem) {
		if (this.firstWorkItem == null)
			this.firstWorkItem = workItem;
		else {
			if (this.workItemQueue == null)
				this.workItemQueue = new Quack<WorkItem>() {
				};
			this.workItemQueue.enqueue(workItem);
		}

		if (Trace.isEnabled())
			Trace.traceWorkItemScheduled(workItem);
	}

	private RequestedAction executeWorkItem(WorkItem workItem) throws Exception {
		if (Trace.isEnabled())
			Trace.traceWorkItemStarting(workItem);

		RequestedAction action = this.executor.onExecuteWorkItem(workItem);

		// NOTE if yields, item still active and the callback should to dispose it
		if (action == YIELD_SILENTLY_ACTION)
			return action;

		// NOTE check executor abort/terminate pending
		// onExecuteWorkItem will performing abort() and set isAbortPending=true
		if (this.executor.isAbortPending() || this.executor.isTerminatePending())
			action = ABORT_ACTION;

		// NOTE 5 cleanup and return workItem to pool
		workItem.dispose(this.executor);

		if (Trace.isEnabled())
			Trace.traceWorkItemCompleted(workItem);

		return action;
	}

	private void scheduleIdle() throws Exception {
		this.executor.onSchedulerIdle();
	}

	private void notifyUnhandledException(Exception exception, ActivityInstance source) throws Exception {
		this.executor.notifyUnhandledException(exception, source);
	}

	public static void onScheduledWork(Scheduler scheduler) throws Exception {
		RequestedAction nextAction = CONTINUE_ACTION;
		boolean idleOrPaused = false;

		while (nextAction == CONTINUE_ACTION) {
			if (scheduler.isIdle() || scheduler.isPausing) {
				idleOrPaused = true;
				break;
			}

			WorkItem currentWorkItem = scheduler.firstWorkItem;

			scheduler.firstWorkItem = scheduler.workItemQueue != null &&
					scheduler.workItemQueue.count() > 0 ?
					scheduler.workItemQueue.dequeue() :
					null;

			nextAction = scheduler.executeWorkItem(currentWorkItem);
		}

		// we must process events or dispose workflow resources until idle or paused

		if (idleOrPaused || nextAction == ABORT_ACTION) {
			scheduler.isRunning = false;
			scheduler.isPausing = false;
			scheduler.scheduleIdle();
			return;
		}

		if (nextAction != YIELD_SILENTLY_ACTION) {
			Helper.assertEquals(NotifyUnhandledExceptionAction.class, nextAction.getClass());
			NotifyUnhandledExceptionAction notifyAction = (NotifyUnhandledExceptionAction) nextAction;
			scheduler.isRunning = false;
			scheduler.notifyUnhandledException(notifyAction.getException(), notifyAction.getSource());
		}
	}

	public static abstract class RequestedAction {
	}

	public static class ContinueAction extends RequestedAction {
	}

	public static class YieldSilentlyAction extends RequestedAction {
	}

	public static class AbortAction extends RequestedAction {
	}

	public static class NotifyUnhandledExceptionAction extends RequestedAction {
		private Exception exception;
		private ActivityInstance source;

		public NotifyUnhandledExceptionAction(Exception exception, ActivityInstance source) {
			this.exception = exception;
			this.source = source;
		}

		public Exception getException() {
			return this.exception;
		}

		public ActivityInstance getSource() {
			return this.source;
		}
	}
}
