package pacman.runtime;

import pacman.*;
import pacman.ActivityInstance.ActivityInstanceState;

public class CompletionBookmark {
	private CompletionCallbackWrapper callbackWrapper;

	public CompletionBookmark(CompletionCallbackWrapper callbackWrapper) {
		this.callbackWrapper = callbackWrapper;
	}

	public CompletionBookmark() {
		// only called when executor.abortActivityInstance()
	}

	public WorkItem generateWorkItem(ActivityInstance completedInstance, ActivityExecutor executor) {
		if (this.callbackWrapper != null)
			return this.callbackWrapper.createWorkItem(completedInstance, executor);

		// for variable.default and arugment.expression
		if (completedInstance.getState() != ActivityInstanceState.Closed && completedInstance.getParent().hasNotExecuted())
			completedInstance.getParent().setInitializationIncomplete();

		return new EmptyWithCancelationCheckWorkItem(completedInstance.getParent(), completedInstance);
	}

	public void checkForCancelation() {
		this.callbackWrapper.checkForCancelation();
	}

}
