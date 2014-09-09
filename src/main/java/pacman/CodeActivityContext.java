package pacman;

public class CodeActivityContext extends ActivityContext {
	protected CodeActivityContext() {
	}

	protected CodeActivityContext(ActivityInstance instance, ActivityExecutor executor) {
		super(instance, executor);
	}

	public void initialize(ActivityInstance instance, ActivityExecutor executor) {
		super.reinitialize(instance, executor);
	}
}
