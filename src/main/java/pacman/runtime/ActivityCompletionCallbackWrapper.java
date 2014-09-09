package pacman.runtime;

import pacman.ActivityInstance;
import pacman.CompletionCallback;
import pacman.Delegate;
import pacman.NativeActivityContext;

public class ActivityCompletionCallbackWrapper extends CompletionCallbackWrapper {

	public ActivityCompletionCallbackWrapper(Delegate delegate, ActivityInstance activityInstance) {
		super(delegate, activityInstance);
	}

	@Override
	protected void invoke(NativeActivityContext context, ActivityInstance completedInstance) {
		((CompletionCallback) this.delegate).execute(context, completedInstance);
	}

}
