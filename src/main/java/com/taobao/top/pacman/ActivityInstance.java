package com.taobao.top.pacman;

public class ActivityInstance {
	private SubState subState;

	public boolean isCancellationRequested() {
		return false;
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
}
