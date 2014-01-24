package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.ActivityInstance;
import com.taobao.top.pacman.Delegate;

public class FuncCompletionCallbackWrapper extends CompletionCallbackWrapper {

	public FuncCompletionCallbackWrapper(Delegate delegate, ActivityInstance currentInstance) {
		super(delegate, currentInstance);
	}

}
