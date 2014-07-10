package com.taobao.top.pacman.statements;

import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class AddToMap extends NativeActivity {
	public InArgument Map;
	public InArgument Key;
	public InArgument Value;
	
	@Override
	protected void cacheMetadata(ActivityMetadata metadata) throws Exception {
		metadata.bindAndAddArgument(this.Map, new RuntimeArgument("Map", java.util.Map.class, ArgumentDirection.In));
		metadata.bindAndAddArgument(this.Key, new RuntimeArgument("Key", Object.class, ArgumentDirection.In));
		metadata.bindAndAddArgument(this.Value, new RuntimeArgument("Value", Object.class, ArgumentDirection.In));
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	@Override
	protected void execute(NativeActivityContext context) throws Exception {
		((java.util.Map) this.Map.get(context)).put(this.Key.get(context), this.Value.get(context));
	}
	
}
