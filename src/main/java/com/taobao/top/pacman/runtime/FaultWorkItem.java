package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;

public class FaultWorkItem extends ActivityExecutionWorkItem {
	private FaultCallbackWrapper callbackWrapper;
	private Exception propagatedException;
	private ActivityInstance propagateFrom;
	private ActivityInstance originalExceptionSource;

	public FaultWorkItem(FaultCallbackWrapper callbackWrapper,
			Exception propagatedException,
			ActivityInstance propagateFrom,
			ActivityInstance originalExceptionSource) {
		super(callbackWrapper.getActivityInstance());
		this.callbackWrapper = callbackWrapper;
		this.propagatedException = propagatedException;
		this.propagateFrom = propagateFrom;
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
			this.callbackWrapper.invoke(faultContext, this.propagatedException, this.propagateFrom);

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
