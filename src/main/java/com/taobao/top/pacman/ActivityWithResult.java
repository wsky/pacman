package com.taobao.top.pacman;

public abstract class ActivityWithResult extends Activity {
	private OutArgument result;

	public OutArgument getResult() {
		if (this.result == null)
			this.result = new OutArgument();
		return this.result;
	}
}
