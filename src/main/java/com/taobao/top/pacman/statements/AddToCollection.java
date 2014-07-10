package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public final class AddToCollection extends NativeActivity {
	public InArgument Collection;
	public InArgument Item;
	
	@Override
	protected void cacheMetadata(ActivityMetadata metadata) throws Exception {
		metadata.bindAndAddArgument(this.Collection, new RuntimeArgument("Collection", java.util.Collection.class, ArgumentDirection.In));
		metadata.bindAndAddArgument(this.Item, new RuntimeArgument("Item", Object.class, ArgumentDirection.In));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void execute(NativeActivityContext context) throws Exception {
		((java.util.Collection) this.Collection.get(context)).add(this.Item.get(context));
	}
}
