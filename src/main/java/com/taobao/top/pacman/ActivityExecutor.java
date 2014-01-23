package com.taobao.top.pacman;

import java.util.Map;

import com.taobao.top.pacman.hosting.*;
import com.taobao.top.pacman.runtime.*;

public class ActivityExecutor {
	private WorkflowInstance host;
	private Scheduler scheduler;
	private BookmarkManager bookmarkManager;

	private Activity rootActivity;
	private ActivityInstance rootInstance;

	private int lastInstanceId;

	public Pool<NativeActivityContext> NativeActivityContextPool;
	public Pool<CodeActivityContext> CodeActivityContextPool;
	public Pool<ExecuteActivityWorkItem> ExecuteActivityWorkItemPool;
	public Pool<ExecuteExpressionWorkItem> ExecuteExpressionWorkItemPool;
	public Pool<EmptyWorkItem> EmptyWorkItemPool;

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

		workItem.postProcess(this);

		if (workItem.getExceptionToPropagate() != null)
			// upgrade to workflowException if error not processed
			if (!this.propagateException(workItem))
				this.abortWorkflowInstance(workItem.getExceptionToPropagate());

		return isContinue;
	}

	public void scheduleRootActivity(Activity activity, Map<String, Object> inputs, CompletionCallbackWrapper onCompleteWrapper, FaultCallbackWrapper onFaultWrapper) {
		// DataContext dataContext = this.GenerateRuntimeDataContext(inputs, null, activity);

		this.rootActivity = activity;
		// 生成根活动实例
		this.rootInstance = new ActivityInstance(activity
				, null
				, ++this.lastInstanceId
				, onCompleteWrapper
				, onFaultWrapper);

		// 调度根活动
		this.scheduler.pushWork(new ExecuteRootActivityWorkItem(this.rootInstance));
		// 调度活动变量
		// this.ResolveVariables(this.rootInstance, dataContext);
		// 调度活动参数
		// this.ResolveArguments(this.RootInstance, dataContext);
	}

	public ActivityInstance scheduleActivity(
			Activity activity,
			ActivityInstance parent,
			CompletionCallbackWrapper onCompleteWrapper,
			FaultCallbackWrapper onFaultWrapper) {
		// 数据上下文
		// DataContext dataContext = this.GenerateRuntimeDataContext(null, parentDataContext, activity);

		// 创建activity实例
		ActivityInstance instance = new ActivityInstance(activity
				, parent
				, ++this.lastInstanceId
				, onCompleteWrapper
				, onFaultWrapper);

		if (parent != null)
			parent.addChild(instance);

		// 调度活动
		this.scheduleBody(instance);
		// 调度活动变量
		// this.ResolveVariables(instance, dataContext);
		// 调度活动参数
		// this.ResolveArguments(instance, dataContext);

		return instance;
	}

	public void scheduleCompletionBookmark(Bookmark bookmark, Object value) {
		this.scheduler.enqueueWork(this.bookmarkManager.generateWorkItem(this, bookmark, value));
	}

	public void abortWorkflowInstance(Exception reason) {
		// TODO impl abort workflow
	}

	public void abortActivityInstance(ActivityInstance activity, Exception reason) {
		// TODO impl abort activity
	}

	public void cancelActivity(ActivityInstance activityInstance) {
		// TODO impl cancel activity
	}

	private void scheduleBody(ActivityInstance instance) {
		ExecuteActivityWorkItem workItem = this.ExecuteActivityWorkItemPool.acquire();
		workItem.initialize(instance);
		this.scheduler.pushWork(workItem);
	}

	private void scheduleExpression(ActivityInstance instance, int resultReference) {
		ExecuteExpressionWorkItem workItem = this.ExecuteExpressionWorkItemPool.acquire();
		workItem.initialize(instance, resultReference);
		this.scheduler.pushWork(workItem);
	}

	private void scheduleCompletionBookmark(ActivityInstance completedInstance) {
		if (completedInstance.getCompletionBookmark() != null) {
			WorkItem w = completedInstance.getCompletionBookmark().generateWorkItem(completedInstance, this);
			this.scheduler.pushWork(w);
			return;
		}
		if (completedInstance.getParent() != null)
			this.scheduler.pushWork(this.createEmptyWorkItem(completedInstance.getParent()));
	}

	private boolean propagateException(WorkItem workItem) {
		return false;
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
		this.EmptyWorkItemPool = new Pool<EmptyWorkItem>() {
			@Override
			protected EmptyWorkItem createNew() {
				return null;
			}
		};
	}
}
