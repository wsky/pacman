package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.ActivityContext;
import com.taobao.top.pacman.Function;

public abstract class FunctionDefinition {
	public abstract Function<ActivityContext, Object> toFunction(ActivityDefinition parent, DefinitionValidator validator);
}
