package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class WriteLine extends NativeActivity {
	public InArgument Text;

	@Override
	protected void cacheMetadata(ActivityMetadata metadata) {
		if (this.Text == null)
			this.Text = new InArgument((Object) null);
		metadata.bindAndAddArgument(this.Text, new RuntimeArgument("Text", String.class, ArgumentDirection.In));
	}

	@Override
	protected void execute(NativeActivityContext context) {
		if (this.Text != null)
			System.err.println("println: " + this.Text.get(context));
	}
}
