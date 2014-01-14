package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;

public class If extends NativeActivity {
	public InArgument Condition;
	public Activity Then;
	public Activity Else;

	@Override
	protected void execute(NativeActivityContext context) {
		if ((Boolean) this.Condition.get(context))
			context.scheduleActivity(this.Then);
		else
			context.scheduleActivity(this.Else);
	}

}
