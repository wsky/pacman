package com.taobao.top.pacman.definition;

import com.taobao.top.pacman.InlinedFunctionValue;

public abstract class InlinedFunctionDefinition {
	public abstract InlinedFunctionValue toFunction(ActivityDefinition parent, DefinitionValidator validator);
}
