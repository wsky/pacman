package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.*;

public class FaultWorkItem extends ActivityExecutionWorkItem {
	private FaultCallbackWrapper callbackWrapper;
	private Exception propagatedException;
	private ActivityInstance propagateFrom;

	public FaultWorkItem(FaultCallbackWrapper callbackWrapper, Exception propagatedException, ActivityInstance propagateFrom) {
		super(callbackWrapper.getActivityInstance());
		this.callbackWrapper = callbackWrapper;
		this.propagatedException = propagatedException;
		this.propagateFrom = propagateFrom;
	}

	@Override
	public boolean execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		NativeActivityFaultContext faultContext = null;
		try {
			faultContext = new NativeActivityFaultContext(this.getActivityInstance(), executor, bookmarkManager);
			this.callbackWrapper.invoke(faultContext, this.propagatedException, this.propagateFrom);

			if (faultContext.isFaultHandled())
				this.setExceptionToPropagateWithoutSkip(this.propagatedException);
		} catch (Exception e) {
			// FIXME assert fatal
			this.setExceptionToPropagate(e);
		} finally {
			if (faultContext != null)
				faultContext.dispose();
		}
		return true;
	}
}
