package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.FaultCallback;
import com.taobao.top.pacman.NativeActivityFaultContext;

public class FaultCallbackWrapper {

	public FaultCallbackWrapper(FaultCallback onFault, ActivityInstance currentInstance) {
	}

	public ActivityInstance getActivityInstance() {
		return null;
	}

	public void invoke(NativeActivityFaultContext faultContext, Exception propagatedException, ActivityInstance propagateFrom) {

	}

}
