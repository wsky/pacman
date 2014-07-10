package com.taobao.top.pacman;

import java.util.List;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public abstract class ActivityWithResult extends Activity {
	private Class<?> type;
	private OutArgument result;
	private RuntimeArgument resultRuntimeArgument;

	public Class<?> getType() {
		return this.type == null ? Object.class : this.type;
	}

	public void setType(Class<?> type) {
		this.type = type;
	}

	public OutArgument getResult() {
		return this.result;
	}

	public void setResult(OutArgument result) {
		this.result = result;
	}

	@Override
	protected boolean isResultArgument(RuntimeArgument runtimeArgument) {
		return runtimeArgument.equals(this.resultRuntimeArgument);
	}

	@Override
	protected final void onInternalCacheMetadata() throws Exception {
		this.internalCacheMetadataExceptResult();

		List<RuntimeArgument> runtimeArguments = this.getRuntimeArguments();
		if (runtimeArguments != null) {
			for (RuntimeArgument runtimeArgument : runtimeArguments) {
				if (runtimeArgument.getName().equals("Result")) {
					this.resultRuntimeArgument = runtimeArgument;
					return;
				}
			}
		}

		if (this.result == null)
			this.result = new OutArgument();
		this.resultRuntimeArgument = new RuntimeArgument("Result", this.getType(), ArgumentDirection.Out);
		ActivityMetadata.bindArgument(this.result, this.resultRuntimeArgument);
		this.addRuntimeArgument(this.resultRuntimeArgument);

	}

	protected void internalCacheMetadataExceptResult() throws Exception {
		super.onInternalCacheMetadata();
	}

	// fast-path for expressions that can be resolved synchronously
	// use array as missing multi-return in java
	protected Object[] tryGetValue(ActivityContext context) {
		return new Object[] { false, null };
	}

	protected Object executeWithTryGetValue(ActivityContext context) {
		Object[] ret = this.tryGetValue(context);
		Helper.assertTrue((Boolean) ret[0]);
		return ret[1];
	}
}
