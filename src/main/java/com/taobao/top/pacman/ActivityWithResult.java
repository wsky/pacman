package com.taobao.top.pacman;

import java.util.List;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public abstract class ActivityWithResult extends Activity {
	private Class<?> type;
	private OutArgument result;

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
	protected final void internalCacheMetadata() {
		this.internalCacheMetadataExceptResult();

		List<RuntimeArgument> runtimeArguments = this.getRuntimeArguments();
		for (RuntimeArgument runtimeArgument : runtimeArguments)
			if (runtimeArgument.getName().equals("Result"))
				return;

		if (this.result == null)
			this.result = new OutArgument();
		RuntimeArgument argument = new RuntimeArgument("Result", this.getType(), ArgumentDirection.Out);
		ActivityMetadata.bindArgument(this.result, argument);
		this.addRuntimeArgument(argument);
	}

	protected void internalCacheMetadataExceptResult() {
		super.internalCacheMetadata();
	}
}
