package pacman;

import pacman.runtime.BookmarkManager;

public class NativeActivityFaultContext extends NativeActivityContext {
	private boolean isFaultHandled;

	private Exception exception;
	private ActivityInstance source;

	public NativeActivityFaultContext(
			ActivityInstance activityInstance,
			ActivityExecutor executor,
			BookmarkManager bookmarkManager,
			Exception exception,
			ActivityInstance source) {
		super(activityInstance, executor, bookmarkManager);
		this.exception = exception;
		this.source = source;
	}

	public boolean isFaultHandled() {
		return this.isFaultHandled;
	}

	public void handleFault() {
		this.isFaultHandled = true;
	}

	public FaultContext createFaultContext() {
		return new FaultContext(this.exception, this.source);
	}
}
