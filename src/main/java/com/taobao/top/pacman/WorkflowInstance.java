package com.taobao.top.pacman;

import java.util.HashMap;
import java.util.Map;

public class WorkflowInstance {
	private ActivityExecutor executor;
	private Map<String, Object> initialArguments;
	private Activity workflowDefinition;

	private boolean isInitialized;
	private boolean isAborted;
	private Exception abortedException;

	private WorkflowExtensionManager extensionManager;

	public WorkflowInstance(Activity workflowDefinition) {
		this(workflowDefinition, new HashMap<String, Object>());
	}

	public WorkflowInstance(Activity workflowDefinition, Map<String, Object> inputs) {
		this.workflowDefinition = workflowDefinition;
		this.initialArguments = inputs;
	}

	protected <T> T getExtension(Class<T> type) {
		return this.extensionManager != null ? this.extensionManager.getExtension(type) : null;// type.newInstance();
	}

	// TODO impl bookmark resume
	// public void resumeBookmark(String bookmarkName, Object value) {
	// this.ensureInitialized();
	// this.executor.scheduleCompletionBookmark(new Bookmark(bookmarkName), value);
	// this.runScheduler();
	// }

	protected void abort(Exception reason) {
		if (this.isAborted)
			return;
		this.isAborted = true;
		if (reason != null)
			this.abortedException = reason;
	}

	protected void notifyPaused() {
		if (Trace.isEnabled())
			Trace.traceWorkflowCompleted(this);
		// TODO check shouldRaiseComplte
		// TODO raise some eventhandlers or trace
	}

	protected void notifyUnhandledException(Exception exception, Activity activity, int id) {
		if (Trace.isEnabled())
			Trace.traceWorkflowUnhandledException(this, activity, exception);

		// TODO raise some eventhanles or trace

		this.abort(exception);

		this.notifyPaused();
	}

	private void ensureInitialized() {
		if (!this.isInitialized) {
			this.executor = new ActivityExecutor(this);
			this.initialize(this.initialArguments);
		}
	}

	private void initialize(Map<String, Object> inputs) {
		if (!workflowDefinition.isRuntimeReady()) {
			ActivityUtilities.cacheRootMetadata(
					this.workflowDefinition,
					new ActivityLocationReferenceEnvironment(null),
					// FIXME just for test
					new RenderProcessActivityCallback());
		}
		// if (inputs == null)
		// inputs = ActivityUtilities.EmptyParameters;
		// NOTE 1 prepare root schedule, first workItem
		this.executor.scheduleRootActivity(this.workflowDefinition, inputs);
		this.isInitialized = true;
	}

	private void runScheduler() throws Exception {
		this.executor.markSchedulerRunning();
		this.executor.run();
	}

	public static Map<String, Object> invoke(Activity activity, Map<String, Object> inputs) throws Exception {
		return invoke(activity, inputs, null);
	}

	public static Map<String, Object> invoke(
			Activity activity,
			Map<String, Object> inputs,
			WorkflowExtensionManager extensionManager) throws Exception {
		WorkflowInstance instance = new WorkflowInstance(activity, inputs);
		instance.extensionManager = extensionManager;

		if (Trace.isEnabled())
			Trace.traceWorkflowStart(instance);

		instance.ensureInitialized();
		instance.runScheduler();

		if (instance.abortedException != null)
			throw instance.abortedException;
		if (instance.executor.getCompletionException() != null)
			throw instance.executor.getCompletionException();

		return instance.executor.getWorkflowOutputs();
	}
}