package pacman;

public interface CompletionWithResultCallback<T> extends Delegate {
	public void execute(NativeActivityContext context, ActivityInstance completedInstance, T result);
}
