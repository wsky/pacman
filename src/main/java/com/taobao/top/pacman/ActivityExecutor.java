package com.taobao.top.pacman;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.Scheduler.RequestedAction;
import com.taobao.top.pacman.runtime.*;

public class ActivityExecutor {
	private int lastInstanceId;

	private WorkflowInstance host;
	private Scheduler scheduler;
	private BookmarkManager bookmarkManager;

	private Activity rootActivity;
	private ActivityInstance rootInstance;
	private Map<String, Object> workflowOutputs;
	private Exception completionException;

	// flag for schedule
	private boolean isAbortPending;
	private boolean isTerminatePending;

	public Pool<NativeActivityContext> NativeActivityContextPool;
	public Pool<CodeActivityContext> CodeActivityContextPool;
	public Pool<EmptyWorkItem> EmptyWorkItemPool;
	public Pool<ExecuteActivityWorkItem> ExecuteActivityWorkItemPool;
	public Pool<ResolveNextArgumentWorkItem> ResolveNextArgumentWorkItemPool;
	public Pool<CompletionWorkItem> CompletionWorkItemPool;

	public ActivityExecutor(WorkflowInstance host) {
		this.host = host;
		this.scheduler = new Scheduler(this);
		this.bookmarkManager = new BookmarkManager();
		this.initialize();
	}

	protected <T> T getExtension(Class<T> type) {
		return this.host.getExtension(type);
	}

	protected Map<String, Object> getWorkflowOutputs() {
		return this.workflowOutputs;
	}

	protected Exception getCompletionException() {
		return this.completionException;
	}

	protected boolean isAbortPending() {
		return this.isAbortPending;
	}

	protected boolean isTerminatePending() {
		return this.isTerminatePending;
	}

	protected void markSchedulerRunning() {
		this.scheduler.markRunning();
	}

	protected void run() throws Exception {
		this.scheduler.resume();
	}

	protected RequestedAction onExecuteWorkItem(WorkItem workItem) throws Exception {
		workItem.release();

		if (!workItem.isValid())
			return Scheduler.CONTINUE_ACTION;

		if (!workItem.isEmpty()) {
			// NOTE 3 execute workItem, maybe execute activity or other callback
			if (!workItem.execute(this, this.bookmarkManager))
				return Scheduler.YIELD_SILENTLY_ACTION;
		}

		if (workItem.getWorkflowAbortException() != null) {
			this.abortWorkflowInstance(workItem.getWorkflowAbortException());
			return Scheduler.CONTINUE_ACTION;
		}

		// NOTE 4 post workItem
		workItem.postProcess(this);

		if (workItem.getExceptionToPropagate() != null)
			this.propagateException(workItem);
		if (workItem.getExceptionToPropagate() != null)
			return new Scheduler.NotifyUnhandledExceptionAction(
					workItem.getExceptionToPropagate(),
					workItem.getOriginalExceptionSource());

		return Scheduler.CONTINUE_ACTION;
	}

	protected void onSchedulerIdle() throws Exception {
		// if (!this.scheduler.isIdle())
		// return;
		try {
			this.host.notifyPaused();
		} catch (Exception e) {
			if (Helper.isFatal(e))
				throw e;
			this.abortWorkflowInstance(e);
		}
	}

	protected void notifyUnhandledException(Exception exception, ActivityInstance source) throws Exception {
		try {
			this.host.notifyUnhandledException(exception, source.getActivity(), source.getId());
		} catch (Exception e) {
			if (Helper.isFatal(e))
				throw e;
			this.abortWorkflowInstance(e);
		}
	}

	public Exception completeActivityInstance(ActivityInstance instance) {
		Exception exception = null;

		this.handleRootCompletion(instance);

		// NOTE 4.3.2 completionBookmark to gather up any outputs and schdule callback to parent
		this.scheduleCompletionBookmark(instance);

		// TODO cleanup environmental resources
		// ...

		instance.markAsComplete();
		instance.finalize(exception != null);

		return exception;
	}

	// called from onExecuteWorkItem() or user calling context.abort()
	protected void abortWorkflowInstance(Exception reason) {
		// tell scheduler
		this.isAbortPending = true;
		this.host.abort(reason);
		System.err.println("-- abortWorkflowInstance: " + reason.getMessage());
	}

	// called from context.abortChildInstance() or postProcess()
	public void abortActivityInstance(ActivityInstance instance, Exception reason) {
		instance.abort(this, this.bookmarkManager, reason, true);
		if (instance.getCompletionBookmark() != null)
			instance.getCompletionBookmark().checkForCancelation();
		else if (instance.getParent() != null)
			// for variable.default and arugment.expression
			instance.setCompletionBookmark(new CompletionBookmark());
		this.scheduleCompletionBookmark(instance);
	}

	public void cancelActivity(ActivityInstance instance) {
		if (instance.isCancellationRequested() ||
				instance.getState() != ActivityInstanceState.Executing)
			return;

		instance.setCancellationRequested();

		this.scheduler.pushWork(instance.hasNotExecuted() ?
				this.createEmptyWorkItem(instance) :
				new CancelActivityWorkItem(instance));
	}

	public void scheduleRootActivity(Activity activity, Map<String, Object> argumentValues) {
		// FIXME trace start here

		this.rootActivity = activity;
		this.rootInstance = new ActivityInstance(activity);
		boolean requiresSymbolResolution = this.rootInstance.initialize(null, this.lastInstanceId, null, this);

		if (Trace.isEnabled())
			Trace.traceActivityScheduled(activity, this.rootInstance, null);

		this.scheduler.pushWork(new ExecuteRootActivityWorkItem(this.rootInstance, requiresSymbolResolution, argumentValues));
	}

	public ActivityInstance scheduleActivity(
			Activity activity,
			ActivityInstance parent,
			CompletionBookmark completionBookmark,
			FaultBookmark faultBookmark) {
		return this.scheduleActivity(activity, parent, completionBookmark, faultBookmark, null);
	}

	public ActivityInstance scheduleActivity(
			Activity activity,
			ActivityInstance parent,
			CompletionBookmark completionBookmark,
			FaultBookmark faultBookmark,
			LocationEnvironment parentEnvironment) {
		return this.scheduleActivity(activity, parent,
				completionBookmark, faultBookmark, parentEnvironment, null, null);
	}

	public ActivityInstance scheduleActivity(
			Activity activity,
			ActivityInstance parent,
			CompletionBookmark completionBookmark,
			FaultBookmark faultBookmark,
			LocationEnvironment parentEnvironment,
			Map<String, Object> argumentValues,
			Location resultLocation) {
		ActivityInstance instance = this.createActivityInstance(activity, parent, completionBookmark, faultBookmark);
		boolean requiresSymbolResolution = instance.initialize(parent, this.lastInstanceId, parentEnvironment, this);

		if (Trace.isEnabled())
			Trace.traceActivityScheduled(activity, instance, parent);

		this.scheduleBody(instance, requiresSymbolResolution, argumentValues, resultLocation);
		return instance;
	}

	public void scheduleCompletionBookmark(Bookmark bookmark, Object value) {
		this.scheduler.enqueueWork(this.bookmarkManager.generateWorkItem(this, bookmark, value));
	}

	// for argument/variable schedule
	public void scheduleExpression(Activity activity,
			ActivityInstance parent,
			LocationEnvironment parentEnvironment,
			Location resultLocation) {
		this.scheduleActivity(activity, parent, null, null, parentEnvironment, null, resultLocation);
	}

	public void scheduleItem(WorkItem workItem) {
		this.scheduler.pushWork(workItem);
	}

	protected void scheduleBody(ActivityInstance instance,
			boolean requiresSymbolResolution,
			Map<String, Object> argumentValues,
			Location resultLocation) {
		if (resultLocation != null) {
			this.scheduler.pushWork(new ExecuteExpressionWorkItem(
					instance, requiresSymbolResolution, argumentValues, resultLocation));
			return;
		}
		ExecuteActivityWorkItem workItem = this.ExecuteActivityWorkItemPool.acquire();
		workItem.initialize(instance, requiresSymbolResolution, argumentValues);
		this.scheduler.pushWork(workItem);
	}

	private void scheduleCompletionBookmark(ActivityInstance completedInstance) {
		if (completedInstance.getCompletionBookmark() != null) {
			this.scheduler.pushWork(completedInstance.getCompletionBookmark().generateWorkItem(completedInstance, this));
			return;
		}
		if (completedInstance.getParent() != null) {
			// for variable.default and arugment.expression
			// if resovle failed, it's state not equal to closed, should tell parent init incomplete
			if (completedInstance.getState() != ActivityInstanceState.Closed && completedInstance.getParent().hasNotExecuted())
				completedInstance.getParent().setInitializationIncomplete();
			this.scheduler.pushWork(this.createEmptyWorkItem(completedInstance.getParent()));
		}
	}

	private void propagateException(WorkItem workItem) {
		ActivityInstance exceptionSource = workItem.getActivityInstance();
		Exception exception = workItem.getExceptionToPropagate();

		ActivityInstance exceptionPropagator = exceptionSource;
		FaultBookmark targetBookmark = null;

		while (exceptionPropagator != null && targetBookmark == null) {
			if (!exceptionPropagator.isCompleted()) {
				// transaction abort here
			}

			// check canceling
			if (exceptionPropagator.isCancellationRequested()) {
				this.abortWorkflowInstance(new Exception("CannotPropagateExceptionWhileCanceling", exception));
				workItem.exceptionPropagated();
				return;
			}

			if (exceptionPropagator.getFaultBookmark() != null)
				targetBookmark = exceptionPropagator.getFaultBookmark();
			else
				exceptionPropagator = exceptionPropagator.getParent();
		}

		if (targetBookmark != null) {
			this.scheduler.pushWork(targetBookmark.generateWorkItem(
					exception, exceptionPropagator, workItem.getOriginalExceptionSource()));
			workItem.exceptionPropagated();
		}

		if (Trace.isEnabled())
			Trace.traceExceptionPropagated(exception,
					exceptionSource,
					exceptionPropagator != null ?
							exceptionPropagator.getParent() :
							null);
	}

	// NOTE 4.3.1 gather root activity outputs
	protected void handleRootCompletion(ActivityInstance completedInstance) {
		if (completedInstance.getParent() != null)
			return;
		if (completedInstance == this.rootInstance)
			if (this.rootInstance.getState() != ActivityInstanceState.Executing)
				this.gatherRootOutputs(this.rootInstance.getEnvironment());
	}

	private void gatherRootOutputs(LocationEnvironment rootEnvironment) {
		Helper.assertNull(this.workflowOutputs);
		Helper.assertNotEquals(ActivityInstanceState.Executing, this.rootInstance.getState());
		Helper.assertNotNull(rootEnvironment);

		if (this.rootInstance.getState() != ActivityInstanceState.Closed)
			return;

		List<RuntimeArgument> rootArguments = this.rootActivity.getRuntimeArguments();
		for (int i = 0; i < rootArguments.size(); i++) {
			RuntimeArgument argument = rootArguments.get(i);

			if (argument.getDirection() != ArgumentDirection.Out)
				continue;

			if (this.workflowOutputs == null)
				this.workflowOutputs = new HashMap<String, Object>();

			Location location = rootEnvironment.getLocation(argument.getId());
			Helper.assertNotNull(location);
			this.workflowOutputs.put(argument.getName(), location.getValue());
		}
	}

	private ActivityInstance createActivityInstance(Activity activity,
			ActivityInstance parent,
			CompletionBookmark completionBookmark,
			FaultBookmark faultBookmark) {
		ActivityInstance instance = new ActivityInstance(activity);
		if (parent != null) {
			instance.setCompletionBookmark(completionBookmark);
			instance.setFaultBookmakr(faultBookmark);
			parent.addChild(instance);
		}
		this.lastInstanceId++;
		return instance;
	}

	private WorkItem createEmptyWorkItem(ActivityInstance instance) {
		EmptyWorkItem workItem = this.EmptyWorkItemPool.acquire();
		workItem.initialize(instance);
		return workItem;
	}

	private void initialize() {
		this.NativeActivityContextPool = new Pool<NativeActivityContext>() {
			@Override
			protected NativeActivityContext createNew() {
				return new NativeActivityContext();
			}
		};
		this.CodeActivityContextPool = new Pool<CodeActivityContext>() {
			@Override
			protected CodeActivityContext createNew() {
				return new CodeActivityContext();
			}
		};
		this.EmptyWorkItemPool = new Pool<EmptyWorkItem>() {
			@Override
			protected EmptyWorkItem createNew() {
				return new EmptyWorkItem();
			}
		};
		this.ExecuteActivityWorkItemPool = new Pool<ExecuteActivityWorkItem>() {
			@Override
			protected ExecuteActivityWorkItem createNew() {
				return new ExecuteActivityWorkItem();
			}
		};
		this.ResolveNextArgumentWorkItemPool = new Pool<ResolveNextArgumentWorkItem>() {
			@Override
			protected ResolveNextArgumentWorkItem createNew() {
				return new ResolveNextArgumentWorkItem();
			}
		};
		this.CompletionWorkItemPool = new Pool<CompletionWorkItem>() {
			@Override
			protected CompletionWorkItem createNew() {
				return new CompletionWorkItem();
			}
		};
	}
}
