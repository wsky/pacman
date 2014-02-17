package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;

import com.taobao.top.pacman.runtime.*;

public class ActivityInstance {
	private int id;
	private Activity activity;
	private ActivityInstance parent;
	private List<ActivityInstance> children;

	private CompletionBookmark completionBookmark;
	private FaultBookmark faultBookmark;
	private LocationEnvironment environment;

	// instance state
	private ActivityInstanceState state;
	// execution state
	private SubState subState;

	private boolean isCompleted;
	private boolean isCancellationRequested;
	private boolean isInitializationIncomplete;
	private boolean isPerformingDefaultCancelation;

	private boolean noSymbols;

	public ActivityInstance(Activity activity) {
		this.activity = activity;
		this.state = ActivityInstanceState.Executing;
		this.subState = SubState.Created;
	}

	protected boolean initialize(ActivityInstance parent, int id, LocationEnvironment parentEnvironment, ActivityExecutor executor) {
		this.parent = parent;
		this.id = id;

		if (this.parent != null) {
			if (parentEnvironment != null)
				parentEnvironment = this.parent.getEnvironment();
		}

		int symbolCount = this.getActivity().getSymbolCount();

		if (symbolCount > 0) {
			this.environment = new LocationEnvironment(parentEnvironment, symbolCount);
			this.subState = SubState.ResolvingArguments;
			return true;
		}

		if (parentEnvironment == null) {
			this.environment = new LocationEnvironment();
			return false;
		}

		this.noSymbols = true;
		this.environment = parentEnvironment;
		return false;
	}

	protected void setCompletionBookmark(CompletionBookmark completionBookmark) {
		this.completionBookmark = completionBookmark;
	}

	protected CompletionBookmark getCompletionBookmark() {
		return this.completionBookmark;
	}

	protected void setFaultBookmakr(FaultBookmark faultBookmark) {
		this.faultBookmark = faultBookmark;
	}

	protected FaultBookmark getFaultBookmark() {
		return this.faultBookmark;
	}

	protected LocationEnvironment getEnvironment() {
		return this.environment;
	}

	protected Iterable<ActivityInstance> getChildren() {
		return this.children;
	}

	protected void addChild(ActivityInstance child) {
		if (this.children == null)
			this.children = new ArrayList<ActivityInstance>();
		this.children.add(child);
	}

	private void removeChild(ActivityInstance child) {
		this.children.remove(child);
	}

	public ActivityInstanceState getState() {
		return this.state;
	}

	public void markCanceled() {
		this.subState = SubState.Canceling;
	}

	protected void markExecuted() {
		this.subState = SubState.Executing;
	}

	protected boolean isCancellationRequested() {
		return this.isCancellationRequested;
	}

	protected void setCancellationRequested() {
		Helper.assertFalse(this.isCancellationRequested);
		this.isCancellationRequested = true;
	}

	protected boolean isCompleted() {
		return this.isCompleted;
	}

	public ActivityInstance getParent() {
		return this.parent;
	}

	public Activity getActivity() {
		return this.activity;
	}

	public void markAsComplete() {
		if (this.parent != null)
			this.parent.removeChild(this);
	}

	protected void baseCancel(NativeActivityContext context) {
		Helper.assertTrue(this.isCancellationRequested());
		this.isPerformingDefaultCancelation = true;
		this.cancelChildren(context);
	}

	protected void cancelChildren(NativeActivityContext context) {
		if (this.children != null && this.children.size() > 0)
			for (ActivityInstance child : this.children)
				context.cancelChild(child);
	}

	protected void abort(ActivityExecutor activityExecutor,
			BookmarkManager bookmarkManager,
			Exception reason,
			boolean isTerminate) {
	}

	public boolean isPerformingDefaultCancelation() {
		return this.isPerformingDefaultCancelation;
	}

	public boolean haveNotExecuted() {
		return this.subState == SubState.PreExecuting;
	}

	public void setInitializationIncomplete() {
		this.isInitializationIncomplete = true;
	}

	public void setInitialized() {
		Helper.assertNotEquals(SubState.Initialized, this.subState);
		this.subState = SubState.Initialized;
	}

	public void finalize(boolean fault) {
		if (!fault)
			return;
		this.tryCancelParent();
		this.state = ActivityInstanceState.Faulted;
	}

	public void setCanceled() {
		Helper.assertFalse(this.isCompleted());
		this.tryCancelParent();
		this.state = ActivityInstanceState.Canceled;
	}

	public void setClosed() {
		Helper.assertFalse(this.isCompleted());
		this.state = ActivityInstanceState.Closed;
	}

	public void execute(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		Helper.assertFalse(this.isInitializationIncomplete, "init incomplete");
		this.markExecuted();
		this.getActivity().internalExecute(this, executor, bookmarkManager);
	}

	public void cancel(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		this.getActivity().internalCancel(this, executor, bookmarkManager);
	}

	public boolean resolveArguments(ActivityExecutor executor,
			Map<String, Object> argumentValues,
			Location resultLocation,
			int startIndex) {
		boolean sync = true;

		List<RuntimeArgument> runtimeArguments = this.getActivity().getRuntimeArguments();
		int argumentCount = runtimeArguments.size();

		if (argumentCount == 0)
			return sync;

		for (int i = startIndex; i < argumentCount; i++) {
			RuntimeArgument argument = runtimeArguments.get(i);
			Object value = null;

			if (argumentValues != null)
				value = argumentValues.get(argument.getName());

			// some types of argument not need schedule, just reference to another argument value
			// argumentReference
			if (!argument.tryPopuateValue(this.getEnvironment(), this, value, resultLocation)) {
				sync = false;
				int next = i + 1;
				// if have one more argument, should resume argument resolution after current expression scheduled
				if (next < runtimeArguments.size()) {
					ResolveNextArgumentWorkItem workItem = executor.ResolveNextArgumentWorkItemPool.acquire();
					workItem.initialize(this, next, argumentValues, resultLocation);
					executor.scheduleItem(workItem);
				}
				// schedule argument expression
				executor.scheduleExpression(
						argument.getBoundArgument().getExpression(),
						this,
						this.getEnvironment(),
						// FIXME should not direct use real Location, use Referencelocation
						this.getEnvironment().getLocation(argument.getId()));
			}
		}

		if (sync && startIndex == 0)
			this.subState = SubState.ResolvingVariables;

		return sync;
	}

	public boolean resolveVariables(ActivityExecutor executor) {
		this.subState = SubState.ResolvingVariables;
		boolean sync = true;

		List<Variable> implementationVariables = this.getActivity().getImplementationVariables();
		List<Variable> runtimevaVariables = this.getActivity().getRuntimeVariables();

		for (int i = 0; i < implementationVariables.size(); i++) {
			Variable variable = implementationVariables.get(i);
			if (!variable.tryPopulateLocation(executor)) {
				Helper.assertNotNull(variable.getDefault());
				executor.scheduleExpression(
						variable.getDefault(),
						this,
						this.getEnvironment(),
						this.getEnvironment().getLocation(variable.getId()));
				sync = false;
			}
		}

		for (int i = 0; i < runtimevaVariables.size(); i++) {
			Variable variable = runtimevaVariables.get(i);
			// some types of varibale not need schedule, just reference to another varibale value
			if (!variable.tryPopulateLocation(executor)) {
				Helper.assertNotNull(variable.getDefault());
				executor.scheduleExpression(
						variable.getDefault(),
						this,
						this.getEnvironment(),
						// FIXME should not direct use real location, use Referencelocation
						this.getEnvironment().getLocation(variable.getId()));
				sync = false;
			}
		}
		return sync;
	}

	private void tryCancelParent() {
		if (this.getParent() != null && this.getParent().isPerformingDefaultCancelation())
			this.getParent().markCanceled();
	}

	protected static ActivityInstance createCanceledActivityInstance(Activity activity) {
		ActivityInstance instance = new ActivityInstance(activity);
		instance.state = ActivityInstanceState.Canceled;
		return instance;
	}

	public enum ActivityInstanceState {
		Executing,
		Closed,
		Canceling,
		Canceled,
		Faulted
	}

	enum SubState {
		Executing,
		PreExecuting,
		Created,
		ResolvingArguments,
		ResolvingVariables,
		Initialized,
		Canceling
	}

	public void incrementBusyCount() {
		// TODO Auto-generated method stub

	}

	public void decrementBusyCount() {
		// TODO Auto-generated method stub

	}

	public boolean updateState(ActivityExecutor executor) {
		// TODO Auto-generated method stub
		return false;
	}
}
