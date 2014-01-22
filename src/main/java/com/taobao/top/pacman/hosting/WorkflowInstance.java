package com.taobao.top.pacman.hosting;

import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

import com.taobao.top.pacman.*;

public class WorkflowInstance {
	private ActivityExecutor executor;
	private WorkflowInstanceState state;
	private Map<String, Object> initialArguments;
	private Activity workflowDefinition;

	private boolean _isInitialized;
	private UUID id;

	// private Exception abortedException;

	public WorkflowInstance(Activity workflowDefinition) {
		this(workflowDefinition, new HashMap<String, Object>());
	}

	public WorkflowInstance(Activity workflowDefinition, Map<String, Object> inputs) {
		this(workflowDefinition, UUID.randomUUID(), inputs);
	}

	public WorkflowInstance(Activity workflowDefinition, UUID id, Map<String, Object> inputs)
	{
		this.id = id;
		this.workflowDefinition = workflowDefinition;
		this.initialArguments = inputs == null ? new HashMap<String, Object>() : inputs;

		// TODO check isRuntimeReady
		ActivityUtilities.cacheRootMetadata(
				this.workflowDefinition,
				new ActivityLocationReferenceEnvironment(null), null);

	}

	public void resumeBookmark(String bookmarkName, Object value) {
		this.ensureInitialized();
		this.executor.scheduleCompletionBookmark(new Bookmark(bookmarkName), value);
		this.runScheduler();
	}

	private void ensureInitialized()
	{
		if (!this._isInitialized) {
			this.executor = new ActivityExecutor(this);
			this.initialize(this.initialArguments);
		}
	}

	private void initialize(Map<String, Object> inputs) {
		this.executor.scheduleRootActivity(this.workflowDefinition, inputs, null, null);
		this._isInitialized = true;
	}

	private void runScheduler() {
		this.state = WorkflowInstanceState.Runnable;
		this.executor.markSchedulerRunning();
		this.executor.run();
	}

	public static void invoke(Activity activity, Map<String, Object> inputs) {
		WorkflowInstance instance = new WorkflowInstance(activity, inputs);
		instance.ensureInitialized();
		instance.runScheduler();
	}

	enum WorkflowInstanceState {
		Paused,
		Runnable,
		Unloaded,
		Aborted
	}
}