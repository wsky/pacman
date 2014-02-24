package com.taobao.top.pacman;

public interface FaultCallback {
	public void execute(NativeActivityFaultContext faultContext, Exception propagatedException, ActivityInstance propagatedFrom);
}
