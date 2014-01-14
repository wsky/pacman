package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;

public class While extends NativeActivity {
	private InArgument Condition;
	private Activity Body;

	@Override
	protected void execute(NativeActivityContext context) {
	}

	private void OnBodyComplete(NativeActivityContext context, ActivityInstance completedInstance)
	{
		// if (this.Condition.Get(context))
		// context.ScheduleActivity(this.Body, this.OnBodyComplete);
	}

}
