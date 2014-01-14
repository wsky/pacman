package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;

public class WriteLine extends NativeActivity {
	public InArgument Text;

	@Override
	protected void execute(NativeActivityContext context) {
		if (this.Text != null)
			System.out.println(this.Text.get(context));
	}
}
