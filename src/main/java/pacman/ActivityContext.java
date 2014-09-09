package pacman;

public class ActivityContext {
	private ActivityInstance currentInstance;
	private Activity activity;
	protected ActivityExecutor executor;

	private boolean allowChainedEnvironmentAccess;

	protected ActivityContext() {
	}

	protected ActivityContext(ActivityInstance instance, ActivityExecutor executor) {
		this.reinitialize(instance, executor);
	}

	protected void reinitialize(ActivityInstance instance, ActivityExecutor executor) {
		this.currentInstance = instance;
		this.executor = executor;
		this.activity = this.currentInstance.getActivity();
	}

	public void dispose() {
		this.currentInstance = null;
		this.executor = null;
		this.activity = null;
	}

	protected ActivityInstance getCurrentInstance() {
		return this.currentInstance;
	}

	protected Activity getActivity() {
		return this.activity;
	}

	protected void setActivity(Activity activity) {
		this.activity = activity;
	}

	protected boolean allowChainedEnvironmentAccess() {
		return this.allowChainedEnvironmentAccess;
	}

	protected void setAllowChainedEnvironmentAccess(boolean value) {
		this.allowChainedEnvironmentAccess = value;
	}

	protected LocationEnvironment getEnvironment() {
		return this.currentInstance.getEnvironment();
	}

	public Location getLocation(LocationReference locationReference) {
		return locationReference.getLocation(this);
	}

	public Object getValue(LocationReference locationReference) {
		return locationReference.getLocation(this).getValue();
	}

	public void setValue(LocationReference locationReference, Object value) {
		locationReference.getLocation(this).setValue(value);
	}

	public Object get(Argument argument) {
		return this.getValue(argument.getRuntimeArgument());
	}

	public void set(Argument argument, Object value) {
		this.setValue(argument.getRuntimeArgument(), value);
	}

	public Object get(Variable variable) {
		return this.getValue(variable);
	}

	public void set(Variable variable, Object value) {
		this.setValue(variable, value);
	}

	public <T> T getExtension(Class<T> type) {
		return this.executor.getExtension(type);
	}
}
