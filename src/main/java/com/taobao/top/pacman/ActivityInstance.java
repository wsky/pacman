package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.*;

public class ActivityInstance {
	private SubState subState;
	private ActivityInstanceState state;

	public ActivityInstance(Activity activity) {
	}

	public void initialize(ActivityInstance parent, int id, LocationEnvironment parentEnvironment, ActivityExecutor executor) {
	}

	public void setCompletionBookmark(CompletionBookmark completionBookmark) {

	}

	public void setFaultBookmakr(FaultBookmark faultBookmark) {

	}

	public boolean isCancellationRequested() {
		return false;
	}

	public void setCancellationRequested() {
	}

	public void beginCancel(NativeActivityContext nativeActivityContext) {
	}

	public Iterable<ActivityInstance> getChildren() {
		return null;
	}

	public boolean isCompleted() {
		return false;
	}

	public ActivityInstanceState getState() {
		return null;
	}

	public void cancelChildren(NativeActivityContext nativeActivityContext) {

	}

	public Activity getActivity() {
		return null;
	}

	protected LocationEnvironment getEnvironment() {
		return null;
	}

	public void markCanceled() {
		this.subState = SubState.Canceling;
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

	public CompletionBookmark getCompletionBookmark() {
		// TODO Auto-generated method stub
		return null;
	}

	public ActivityInstance getParent() {
		// TODO Auto-generated method stub
		return null;
	}

	public void addChild(ActivityInstance instance) {
		// TODO Auto-generated method stub

	}

	public void markAsComplete() {
		// TODO Auto-generated method stub

	}

	public void abort(ActivityExecutor activityExecutor, BookmarkManager bookmarkManager, Exception reason, boolean b) {
		// TODO Auto-generated method stub

	}

	public boolean haveNotExecuted() {
		// TODO Auto-generated method stub
		return false;
	}

	public boolean isPerformingDefaultCancelation() {
		// TODO Auto-generated method stub
		return false;
	}

	protected static ActivityInstance createCanceledActivityInstance(Activity activity) {
		ActivityInstance instance = new ActivityInstance(activity);
		instance.state = ActivityInstanceState.Canceled;
		return instance;
	}
}
