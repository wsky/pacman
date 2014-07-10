package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.CodeActivityContext;
import com.taobao.top.pacman.CodeActivityWithResult;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class ExistsInMap extends CodeActivityWithResult {
	public InArgument Map;
	public InArgument Key;
	
	@Override
	protected void cacheMetadata(ActivityMetadata metadata) throws Exception {
		metadata.bindAndAddArgument(this.Map, new RuntimeArgument("Map", java.util.Map.class, ArgumentDirection.In));
		metadata.bindAndAddArgument(this.Key, new RuntimeArgument("Key", Object.class, ArgumentDirection.In));
	}
	
	@SuppressWarnings("rawtypes")
	@Override
	protected Object execute(CodeActivityContext context) throws Exception {
		return ((java.util.Map) this.Map.get(context)).containsKey(this.Key.get(context));
	}
}
