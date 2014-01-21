package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.*;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class If extends NativeActivity {
	public InArgument Condition;
	public Activity Then;
	public Activity Else;

	public If() {
		super();
		this.Condition = new InArgument(true);
	}

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		metadata.bindAndAddArgument(this.Condition,
				new RuntimeArgument("Condition", Boolean.class, ArgumentDirection.In));
		metadata.addChild(this.Then);
		metadata.addChild(this.Else);
	}

	@Override
	protected void execute(NativeActivityContext context) {
		if ((Boolean) this.Condition.get(context))
			context.scheduleActivity(this.Then);
		else
			context.scheduleActivity(this.Else);
	}
}
