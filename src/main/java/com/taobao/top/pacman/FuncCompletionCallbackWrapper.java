package com.taobao.top.pacman;

import com.taobao.top.pacman.runtime.CompletionCallbackWrapper;

public class FuncCompletionCallbackWrapper<T> extends CompletionCallbackWrapper {
	private T resultValue;

	public FuncCompletionCallbackWrapper(Delegate delegate, ActivityInstance activityInstance) {
		super(delegate, activityInstance);
		this.needsToGatherOutputs = true;
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void gatherOutputs(ActivityInstance completedInstance) {
		int id = this.getResultId((ActivityWithResult) completedInstance.getActivity());
		this.resultValue = (T) completedInstance.getEnvironment().getLocation(id).getValue();
	}

	@SuppressWarnings("unchecked")
	@Override
	protected void invoke(NativeActivityContext context, ActivityInstance completedInstance) {
		((CompletionWithResultCallback<T>) this.delegate).execute(context, completedInstance, this.resultValue);
	}

	private int getResultId(ActivityWithResult activity) {
		if (activity.getResult() != null)
			return activity.getResult().getRuntimeArgument().getId();
		for (RuntimeArgument argument : activity.getRuntimeArguments())
			if (argument.getOwner().isResultArgument(argument))
				return argument.getId();
		return -1;
	}
}
