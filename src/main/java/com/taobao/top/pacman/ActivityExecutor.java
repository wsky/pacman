package com.taobao.top.pacman;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.taobao.top.pacman.ActivityInstance.ActivityInstanceState;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.hosting.*;
import com.taobao.top.pacman.runtime.*;

public class ActivityExecutor {
	private WorkflowInstance host;
	private Scheduler scheduler;
	private BookmarkManager bookmarkManager;

	private Activity rootActivity;
	private ActivityInstance rootInstance;
	private Map<String, Object> workflowOutputs;

	private int lastInstanceId;

	public Pool<NativeActivityContext> NativeActivityContextPool;
	public Pool<CodeActivityContext> CodeActivityContextPool;
	public Pool<EmptyWorkItem> EmptyWorkItemPool;
	public Pool<ExecuteActivityWorkItem> ExecuteActivityWorkItemPool;
	public Pool<ExecuteExpressionWorkItem> ExecuteExpressionWorkItemPool;
	public Pool<ResolveNextArgumentWorkItem> ResolveNextArgumentWorkItemPool;
	public Pool<CompletionWorkItem> CompletionWorkItemPool;

	public ActivityExecutor(WorkflowInstance host) {
		this.host = host;
		this.scheduler = new Scheduler(this);
		this.bookmarkManager = new BookmarkManager();
		this.initialize();
	}

	public void markSchedulerRunning() {
		this.scheduler.markRunning();
	}

	public void run() {
		this.scheduler.resume();
	}

	public boolean onExecuteWorkItem(WorkItem workItem) {
		workItem.release();

		if (!(workItem instanceof CompletionWorkItem) &&
				!(workItem instanceof EmptyWorkItem) &&
				!workItem.isValid())
			return true;

		boolean isContinue = true;
		if (!workItem.isEmpty()) {
			try {
				// NOTE 3 execute workItem, maybe execute activity or other callback
				isContinue = workItem.execute(this, this.bookmarkManager);
			} catch (Exception e) {
				this.abortWorkflowInstance(e);
				return true;
			}
		}

		if (workItem.getWorkflowAbortException() != null) {
			this.abortWorkflowInstance(workItem.getWorkflowAbortException());
			return true;
		}

		// NOTE 4 post workItem
		workItem.postProcess(this);

		if (workItem.getExceptionToPropagate() != null)
			// upgrade to workflowException if error not processed
			if (!this.propagateException(workItem))
				this.abortWorkflowInstance(workItem.getExceptionToPropagate());

		return isContinue;
	}

	// performing schedule() by pushing workitem to scheduler
	// executor preparing workItems for scheduler

	public Exception completeActivityInstance(ActivityInstance instance) {
		Exception exception = null;

		this.handleRootCompletion(instance);

		// NOTE 4.3.2 completionBookmark to gather up any outputs and schdule callback to parent
		this.scheduleCompletionBookmark(instance);

		// TODO cleanup environmental resources
		// ...

		instance.markAsComplete();

		return exception;
	}

	public void abortWorkflowInstance(Exception reason) {
		this.host.abort(reason);
	}

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

		this.scheduler.pushWork(instance.haveNotExecuted() ?
				this.createEmptyWorkItem(instance) :
				new CancelActivityWorkItem(instance));
	}

	public void scheduleRootActivity(Activity activity, Map<String, Object> argumentValues) {
		this.rootActivity = activity;
		this.rootInstance = new ActivityInstance(activity);
		boolean requiresSymbolResolution = this.rootInstance.initialize(null, this.lastInstanceId, null, this);
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
			if (completedInstance.getState() != ActivityInstanceState.Closed && completedInstance.getParent().haveNotExecuted())
				completedInstance.getParent().setInitializationIncomplete();
			this.scheduler.pushWork(this.createEmptyWorkItem(completedInstance.getParent()));
		}
	}

	private boolean propagateException(WorkItem workItem) {
		// TODO impl propagete exception
		return false;
	}

	// NOTE 4.3.1 gather root activity outputs
	private void handleRootCompletion(ActivityInstance completedInstance) {
		if (completedInstance.getParent() != null)
			return;
		if (completedInstance == this.rootInstance)
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
				return null;
			}
		};
		this.CodeActivityContextPool = new Pool<CodeActivityContext>() {
			@Override
			protected CodeActivityContext createNew() {
				return null;
			}
		};
		this.EmptyWorkItemPool = new Pool<EmptyWorkItem>() {
			@Override
			protected EmptyWorkItem createNew() {
				return null;
			}
		};
		this.ExecuteActivityWorkItemPool = new Pool<ExecuteActivityWorkItem>() {
			@Override
			protected ExecuteActivityWorkItem createNew() {
				return null;
			}
		};
		this.ExecuteExpressionWorkItemPool = new Pool<ExecuteExpressionWorkItem>() {
			@Override
			protected ExecuteExpressionWorkItem createNew() {
				return null;
			}
		};
		this.ResolveNextArgumentWorkItemPool = new Pool<ResolveNextArgumentWorkItem>() {
			@Override
			protected ResolveNextArgumentWorkItem createNew() {
				return null;
			}
		};
	}
}
