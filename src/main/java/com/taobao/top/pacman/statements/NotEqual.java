package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.CodeActivityContext;
import com.taobao.top.pacman.CodeActivityWithResult;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class NotEqual extends CodeActivityWithResult {
	public InArgument Left;
	public InArgument Right;
	
	@Override
	protected void cacheMetadata(ActivityMetadata metadata) throws Exception {
		metadata.bindAndAddArgument(this.Left, new RuntimeArgument("Left", this.Left.getArgumentType(), ArgumentDirection.In));
		metadata.bindAndAddArgument(this.Right, new RuntimeArgument("Right", this.Right.getArgumentType(), ArgumentDirection.In));
	}
	
	@Override
	protected Object execute(CodeActivityContext context) throws Exception {
		Object left = this.Left.get(context);
		Object right = this.Right.get(context);
		
		if (left == null && right == null)
			return false;
		
		if (left == null)
			return !right.equals(left);
		else
			return !left.equals(right);
	}
}
