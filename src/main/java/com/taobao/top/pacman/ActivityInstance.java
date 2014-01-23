package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.runtime.*;

public class ActivityInstance {
	private Activity activity;
	private ActivityInstance parent;
	private List<ActivityInstance> children;

	private CompletionBookmark completionBookmark;
	private FaultBookmark faultBookmark;
	private LocationEnvironment environment;

	// instance state
	private ActivityInstanceState state;
	// execution state
	private SubState subState;

	private boolean isCompleted;
	private boolean isCancellationRequested;
	private boolean isInitializationIncomplete;
	private boolean isPerformingDefaultCancelation;

	public ActivityInstance(Activity activity) {
	}

	protected void initialize(ActivityInstance parent, int id, LocationEnvironment parentEnvironment, ActivityExecutor executor) {
		// TODO init env
	}

	protected void setCompletionBookmark(CompletionBookmark completionBookmark) {
		this.completionBookmark = completionBookmark;
	}

	protected CompletionBookmark getCompletionBookmark() {
		return this.completionBookmark;
	}

	protected void setFaultBookmakr(FaultBookmark faultBookmark) {
		this.faultBookmark = faultBookmark;
	}

	protected FaultBookmark getFaultBookmark() {
		return this.faultBookmark;
	}

	protected LocationEnvironment getEnvironment() {
		return this.environment;
	}

	protected Iterable<ActivityInstance> getChildren() {
		return this.children;
	}

	protected void addChild(ActivityInstance child) {
		if (this.children == null)
			this.children = new ArrayList<ActivityInstance>();
		this.children.add(child);
	}

	private void removeChild(ActivityInstance child) {
		this.children.remove(child);
	}

	public ActivityInstanceState getState() {
		return this.state;
	}

	protected boolean isCancellationRequested() {
		return this.isCancellationRequested;
	}

	protected void setCancellationRequested() {
		this.isCancellationRequested = true;
	}

	protected void markCanceled() {
		this.subState = SubState.Canceling;
	}

	protected void markExecuted() {
		this.subState = SubState.Executing;
	}

	protected boolean isCompleted() {
		return this.isCompleted;
	}

	public ActivityInstance getParent() {
		return this.parent;
	}

	public Activity getActivity() {
		return this.activity;
	}

	public void markAsComplete() {
		if (this.parent != null)
			this.parent.removeChild(this);
	}

	protected void baseCancel(NativeActivityContext context) {
		// FIXME assert isCancellationRequested
		this.isPerformingDefaultCancelation = true;
		this.cancelChildren(context);
	}

	protected void cancelChildren(NativeActivityContext context) {
		if (this.children != null && this.children.size() > 0)
			for (ActivityInstance child : this.children)
				context.cancelChild(child);
	}

	protected void abort(ActivityExecutor activityExecutor,
			BookmarkManager bookmarkManager,
			Exception reason,
			boolean isTerminate) {
	}

	public boolean isPerformingDefaultCancelation() {
		return this.isPerformingDefaultCancelation;
	}

	public void setInitializationIncomplete() {
		this.isInitializationIncomplete = true;
	}

	public boolean haveNotExecuted() {
		return this.subState == SubState.PreExecuting;
	}

	public void execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		if (this.isInitializationIncomplete)
			throw new SecurityException("init incomplete");
		this.markExecuted();
		this.activity.internalExecute(this, executor, bookmarkManager);
	}

	public void cancel(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		this.activity.internalCancel(this, executor, bookmarkManager);
	}

	protected static ActivityInstance createCanceledActivityInstance(Activity activity) {
		ActivityInstance instance = new ActivityInstance(activity);
		instance.state = ActivityInstanceState.Canceled;
		return instance;
	}

	public enum ActivityInstanceState {
		Executing,
		Closed,
		Canceling,
		Canceled,
		Faulted
	}

	enum SubState {
		Executing,
		PreExecuting,
		Created,
		ResolvingArguments,
		ResolvingVariables,
		Initialized,
		Canceling
	}
}
