package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.WorkItem;

public class Trace {
	private static boolean isEnabled = true;

	public static boolean isEnabled() {
		return isEnabled;
	}

	public static void setEnabled(boolean value) {
		isEnabled = value;
	}

	public static void write(Object input) {
		if (!isEnabled)
			return;
		System.out.println(input);
	}

	public static void traceWorkflowStart(WorkflowInstance workflowInstance) {
		if (!isEnabled)
			return;
		System.out.println(String.format("[TRACE] workflow start"));
	}

	public static void traceWorkflowCompleted(WorkflowInstance workflowInstance) {
		if (!isEnabled)
			return;
		System.out.println(String.format("[TRACE] workflow completed"));
	}

	public static void traceWorkflowUnhandledException(WorkflowInstance workflowInstance, Activity source, Exception exception) {
		if (!isEnabled)
			return;
		System.out.println(String.format(
				"[TRACE] workflow unhandled exception(%s) from %s",
				exception.getMessage(),
				parse(source)));
	}

	public static void traceActivityScheduled(Activity activity, ActivityInstance activityInstance, ActivityInstance parent) {
		if (!isEnabled)
			return;
		System.out.println(String.format("[TRACE] activity scheduled: instance#%s|%s|%s, state=%s",
				activityInstance.getId(),
				activityInstance.getActivity().getClass().getSimpleName(),
				activityInstance.getActivity().getDisplayName(),
				activityInstance.getState()));
	}

	public static void traceActivityCompleted(ActivityInstance activityInstance) {
		if (!isEnabled)
			return;
		System.out.println(String.format("[TRACE] activity completed: instance#%s|%s|%s, state=%s",
				activityInstance.getId(),
				activityInstance.getActivity().getClass().getSimpleName(),
				activityInstance.getActivity().getDisplayName(),
				activityInstance.getState()));
	}

	public static void traceWorkItemScheduled(WorkItem workItem) {
		if (!isEnabled)
			return;
		System.out.println(String.format("[TRACE] workItem scheduled: %s, for instance#%s, %s#%s",
				workItem.getClass().getSimpleName(),
				workItem.getActivityInstance().getId(),
				workItem.getActivityInstance().getActivity().getClass().getSimpleName(),
				workItem.getActivityInstance().getActivity().getDisplayName()));
	}

	public static void traceWorkItemStarting(WorkItem workItem) {
		if (!isEnabled)
			return;
		System.out.println(String.format("[TRACE] workItem starting: %s, isValid=%s, isEmpty=%s, for instance#%s, %s#%s",
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
		System.out.println(String.format("[TRACE] workItem completed: %s, for %s",
				workItem.getClass().getSimpleName(),
				parse(workItem.getActivityInstance())));
	}

	public static void traceExceptionPropagated(Exception exception, ActivityInstance exceptionSource, ActivityInstance activityInstance) {
		if (!isEnabled)
			return;
		if (activityInstance != null) {
			System.out.println(String.format(
					"[TRACE] exception propagated from %s to %s",
					parse(exceptionSource),
					parse(activityInstance)));
			// exception.printStackTrace();
		} else
			System.out.println(String.format(
					"[TRACE] exception(%s) propagated from %s can not be catched",
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
}
