package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.*;

public class If extends NativeActivity {
	public InArgument Condition;
	public Activity Then;
	public Activity Else;

	public If() {
		super();
	}

	@Override
	protected void execute(NativeActivityContext context) {
		if ((Boolean) this.Condition.get(context))
			context.scheduleActivity(this.Then);
		else
			context.scheduleActivity(this.Else);
	}

}
