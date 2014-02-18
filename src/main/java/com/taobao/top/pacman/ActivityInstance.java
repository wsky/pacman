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
	private boolean noSymbols;

	// instance state
	private ActivityInstanceState state;
	// execution state
	private SubState subState;

	private boolean isCancellationRequested;
	private boolean isInitializationIncomplete;
	private boolean isPerformingDefaultCancelation;

	private int busyCount;

	public ActivityInstance(Activity activity) {
		this.activity = activity;
		this.state = ActivityInstanceState.Executing;
		this.subState = SubState.Created;
	}

	protected boolean initialize(
			ActivityInstance parent,
			int id,
			LocationEnvironment parentEnvironment,
			ActivityExecutor executor) {
		this.parent = parent;
		this.id = id;

		if (this.parent != null) {
			if (parentEnvironment == null)
				parentEnvironment = this.parent.getEnvironment();
		}

		int symbolCount = this.getActivity().getSymbolCount();

		if (symbolCount > 0) {
			System.err.println("parentEnv: " + this.activity.getClass().getSimpleName() + parentEnvironment);
			this.environment = new LocationEnvironment(this.getActivity(), parentEnvironment, symbolCount);
			this.subState = SubState.ResolvingArguments;
			return true;
		}

		if (parentEnvironment == null) {
			this.environment = new LocationEnvironment(this.getActivity());
			return false;
		}

		this.noSymbols = true;
		this.environment = parentEnvironment;
		return false;
	}

	protected int getId() {
		return this.id;
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

	protected boolean isEnvironmentOwner() {
		return !this.noSymbols;
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
		return this.getState() != ActivityInstanceState.Executing;
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
		return this.subState != SubState.Executing;
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
		// NOTE 3.2 internal execute activity
		this.getActivity().internalExecute(this, executor, bookmarkManager);
	}

	public void cancel(ActivityExecutor executor, BookmarkManager bookmarkManager) {
		this.getActivity().internalCancel(this, executor, bookmarkManager);
	}

	public boolean resolveArguments(ActivityExecutor executor,
			Map<String, Object> argumentValues,
			Location resultLocation,
			int startIndex) {
		System.out.println("overrideValues: " + argumentValues);

		boolean sync = true;

		List<RuntimeArgument> runtimeArguments = this.getActivity().getRuntimeArguments();
		int argumentCount = runtimeArguments.size();

		if (argumentCount == 0)
			return sync;

		ActivityContext resolutionContext = new ActivityContext(this, executor);

		for (int i = startIndex; i < argumentCount; i++) {
			RuntimeArgument argument = runtimeArguments.get(i);

			System.out.println("resolve argument:" + argument.getName());

			Object value = null;

			if (argumentValues != null)
				value = argumentValues.get(argument.getName());

			// some types of argument not need schedule, just reference to another argument value
			// argumentReference
			if (!argument.tryPopuateValue(this.getEnvironment(), this, resolutionContext, value, resultLocation)) {
				sync = false;
				int next = i + 1;
				// if have one more argument, should resume argument resolution after current expression scheduled
				if (next < runtimeArguments.size()) {
					ResolveNextArgumentWorkItem workItem = executor.ResolveNextArgumentWorkItemPool.acquire();
					// looks confused that still pass resultLocation,
					// but only used when resultArgument and the activtiy must be activityWithResult
					workItem.initialize(this, next, argumentValues, resultLocation);
					executor.scheduleItem(workItem);
				}
				// schedule argument expression
				executor.scheduleExpression(
						argument.getBoundArgument().getExpression(),
						this,
						resolutionContext.getEnvironment(),
						// FIXME should not direct use real Location, use Referencelocation
						this.getEnvironment().getLocation(argument.getId()));
				// must break, different from variables
				break;
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
		ActivityContext context = new ActivityContext(this, executor);

		for (int i = 0; i < implementationVariables.size(); i++) {
			Variable variable = implementationVariables.get(i);

			System.out.println("resolve variable:" + variable.getName());

			if (!variable.tryPopulateLocation(executor, context)) {
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

			System.out.println("resolve variable:" + variable.getName());

			// some types of varibale not need schedule, just reference to another varibale value
			if (!variable.tryPopulateLocation(executor, context)) {
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

	public boolean updateState(ActivityExecutor executor) {
		boolean activityCompleted = false;

		if (this.haveNotExecuted()) {
			if (this.isCancellationRequested()) {
				if (this.hasChildren()) {
					for (ActivityInstance child : this.getChildren()) {
						Helper.assertTrue(
								child.getState() == ActivityInstanceState.Executing,
								"should only have children if they're still executing");
						executor.cancelActivity(child);
					}
				} else {
					this.setCanceled();
					activityCompleted = true;
				}
			} else if (!this.hasPendingWork()) {
				boolean scheduleBody = false;
				if (this.subState == SubState.ResolvingArguments) {
					// NOTE 4.2.1 finish async resolution of arguments now and continue variables resolution
					this.getEnvironment().collapseTemporaryResolutionLocations();
					this.subState = SubState.ResolvingVariables;
					scheduleBody = this.resolveVariables(executor);
				} else if (this.subState == SubState.ResolvingVariables)
					scheduleBody = true;

				if (scheduleBody)
					executor.scheduleBody(this, false, null, null);
			}

			Helper.assertTrue(
					this.hasPendingWork() || activityCompleted,
					"should have scheduled work pending if we're not complete");
		} else if (!this.hasPendingWork()) {
			activityCompleted = true;
			if (this.subState == SubState.Canceling)
				this.setCanceled();
			else
				this.setClosed();
			// transaction maybe check here
		} else if (this.isPerformingDefaultCancelation()) {
			// TODO impl bookmark cleanup
			// if (this.onlyHasOutstandingBookmarks()) {
			// executor.getBookmarkManager().removeAll(this);
			// RemoveAllBookmarks(executor.RawBookmarkScopeManager, executor.RawBookmarkManager);
			// this.markCanceled();
			// Helper.assertFalse(this.hasPendingWork(), "Shouldn't have pending work here.");
			// this.setCanceled();
			// activityCompleted = true;
			// }
		}

		System.out.println(String.format("update state: %s, %s, %s", activityCompleted, this.getState(), this.subState));

		return activityCompleted;
	}

	public void incrementBusyCount() {
		this.busyCount++;
	}

	public void decrementBusyCount() {
		Helper.assertTrue(this.busyCount > 0);
		this.busyCount--;
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

	private boolean hasPendingWork() {
		return this.hasChildren() || this.busyCount > 0;
	}

	private boolean hasChildren() {
		return this.children != null && this.children.size() > 0;
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
		Created,
		ResolvingArguments,
		ResolvingVariables,
		Initialized,
		Canceling
	}
}
