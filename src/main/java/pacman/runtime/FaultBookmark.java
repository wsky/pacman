package pacman.runtime;

import pacman.ActivityInstance;

public class FaultBookmark {
	private FaultCallbackWrapper callbackWrapper;

	public FaultBookmark(FaultCallbackWrapper callbackWrapper) {
		this.callbackWrapper = callbackWrapper;
	}

	public WorkItem generateWorkItem(Exception propagatedException,
			ActivityInstance propagatedFrom,
			ActivityInstance originalExceptionSource) {
		return this.callbackWrapper.createWorkItem(
				propagatedException, propagatedFrom, originalExceptionSource);
	}

}
