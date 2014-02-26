package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class Assign extends NativeActivity {
	public InArgument Value;
	public OutArgument To;

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		metadata.bindAndAddArgument(this.Value, new RuntimeArgument("Value", Object.class, ArgumentDirection.In));
		metadata.bindAndAddArgument(this.To, new RuntimeArgument("To", Object.class, ArgumentDirection.Out));
	}

	@Override
	protected void execute(NativeActivityContext context) {
		this.To.set(context, this.Value.get(context));
	}
}
