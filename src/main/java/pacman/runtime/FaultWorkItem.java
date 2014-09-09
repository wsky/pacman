package pacman.runtime;

import pacman.*;

public class FaultWorkItem extends ActivityExecutionWorkItem {
	private FaultCallbackWrapper callbackWrapper;
	private Exception propagatedException;
	private ActivityInstance propagatedFrom;
	private ActivityInstance originalExceptionSource;

	public FaultWorkItem(FaultCallbackWrapper callbackWrapper,
			Exception propagatedException,
			ActivityInstance propagatedFrom,
			ActivityInstance originalExceptionSource) {
		super(callbackWrapper.getActivityInstance());
		this.callbackWrapper = callbackWrapper;
		this.propagatedException = propagatedException;
		this.propagatedFrom = propagatedFrom;
		this.originalExceptionSource = originalExceptionSource;
	}

	@Override
	public ActivityInstance getOriginalExceptionSource() {
		return this.originalExceptionSource;
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
		NativeActivityFaultContext faultContext = null;
		try {
			faultContext = new NativeActivityFaultContext(
					this.getActivityInstance(),
					executor,
					bookmarkManager,
					this.propagatedException,
					this.originalExceptionSource);
			this.callbackWrapper.invoke(faultContext, this.propagatedException, this.propagatedFrom);

			// NOTE if fault not handled, propagate but skip abort current activityInstance
			if (!faultContext.isFaultHandled())
				this.setExceptionToPropagateWithoutAbort(this.propagatedException);
		} catch (Exception e) {
			if (Helper.isFatal(e))
				throw e;
			this.setExceptionToPropagate(e);
		} finally {
			if (faultContext != null)
				faultContext.dispose();
		}
		return true;
	}
}
