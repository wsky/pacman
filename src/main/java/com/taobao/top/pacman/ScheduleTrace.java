package com.taobao.top.pacman;

public interface ScheduleTrace {
	public boolean isEnabled();
	
	public void traceWorkflowStart();
	public void traceWorkflowSuspend();
	
	public void traceActivityScheduled();

	public void traceWorkItemScheduled();
	public void traceWorkItemStarting();
	public void traceWorkItemCompleted();
}
