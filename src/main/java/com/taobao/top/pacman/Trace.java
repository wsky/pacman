package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.WorkItem;

// used for global schedule trace
public class Trace {
	private static boolean isEnabled = true;
	private static Writer writer = new Writer();

	public static boolean isEnabled() {
		return isEnabled;
	}

	public static void setEnabled(boolean value) {
		isEnabled = value;
	}

	public static void setWriter(Writer writer) {
		Trace.writer = writer;
	}

	public static void writeLine(Object input) {
		if (!isEnabled)
			return;
		writer.writeLine("[TRACE] " + input);
	}

	public static void write(Object input) {
		if (!isEnabled)
			return;
		writer.write(input);
	}

	public static void traceWorkflowStart(WorkflowInstance workflowInstance) {
		if (!isEnabled)
			return;
		writeLine(String.format("workflow start"));
	}

	public static void traceWorkflowCompleted(WorkflowInstance workflowInstance) {
		if (!isEnabled)
			return;
		writeLine(String.format("workflow completed"));
	}

	public static void traceWorkflowUnhandledException(WorkflowInstance workflowInstance, Activity source, Exception exception) {
		if (!isEnabled)
			return;
		writeLine(String.format(
				"workflow unhandled exception(%s) from %s",
				exception.getMessage(),
				parse(source)));
	}

	public static void traceActivityScheduled(Activity activity, ActivityInstance activityInstance, ActivityInstance parent) {
		if (!isEnabled)
			return;
		writeLine(String.format("activity scheduled: instance#%s|%s|%s, state=%s",
				activityInstance.getId(),
				activityInstance.getActivity().getClass().getSimpleName(),
				activityInstance.getActivity().getDisplayName(),
				activityInstance.getState()));
	}

	public static void traceActivityCompleted(ActivityInstance activityInstance) {
		if (!isEnabled)
			return;
		writeLine(String.format("activity completed: instance#%s|%s|%s, state=%s",
				activityInstance.getId(),
				activityInstance.getActivity().getClass().getSimpleName(),
				activityInstance.getActivity().getDisplayName(),
				activityInstance.getState()));
	}

	public static void traceWorkItemScheduled(WorkItem workItem) {
		if (!isEnabled)
			return;
		writeLine(String.format("workItem scheduled: %s, for instance#%s, %s#%s",
				workItem.getClass().getSimpleName(),
				workItem.getActivityInstance().getId(),
				workItem.getActivityInstance().getActivity().getClass().getSimpleName(),
				workItem.getActivityInstance().getActivity().getDisplayName()));
	}

	public static void traceWorkItemStarting(WorkItem workItem) {
		if (!isEnabled)
			return;
		writeLine(String.format("workItem starting: %s, isValid=%s, isEmpty=%s, for instance#%s, %s#%s",
				workItem.getClass().getSimpleName(),
				workItem.isValid(),
				workItem.isEmpty(),
				workItem.getActivityInstance().getId(),
				workItem.getActivityInstance().getActivity().getClass().getSimpleName(),
				workItem.getActivityInstance().getActivity().getDisplayName()));
	}

	public static void traceWorkItemCompleted(WorkItem workItem) {
		if (!isEnabled)
			return;
		writeLine(String.format("workItem completed: %s, for %s",
				workItem.getClass().getSimpleName(),
				parse(workItem.getActivityInstance())));
	}

	public static void traceExceptionPropagated(Exception exception, ActivityInstance exceptionSource, ActivityInstance activityInstance) {
		if (!isEnabled)
			return;
		if (activityInstance != null) {
			writeLine(String.format(
					"exception propagated from %s to %s",
					parse(exceptionSource),
					parse(activityInstance)));
			// exception.printStackTrace();
		} else
			writeLine(String.format(
					"exception(%s) propagated from %s can not be catched",
					exception.getMessage(),
					parse(exceptionSource)));
	}

	private static String parse(ActivityInstance activityInstance) {
		return activityInstance != null ?
				String.format("instance#%s, %s",
						activityInstance.getId(),
						parse(activityInstance.getActivity())) : null;
	}

	private static String parse(Activity activity) {
		return activity != null ?
				String.format("%s#%s",
						activity.getClass().getSimpleName(),
						activity.getDisplayName()) : null;
	}

	public static class Writer {
		public void write(Object input) {
			System.out.print(input);
		}

		public void writeLine(Object input) {
			System.out.println(input);
		}
	}
}
