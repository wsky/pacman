package com.taobao.top.pacman.expressions;

import java.lang.reflect.Constructor;
import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.Argument;
import com.taobao.top.pacman.CodeActivityContext;
import com.taobao.top.pacman.CodeActivityWithResult;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public final class New extends CodeActivityWithResult {
	private Class<?> type;
	private Constructor<?> constructor;
	// java method not support multi-return, like <object method(ref object arg)>
	private List<InArgument> arguments;
	
	public New(Class<?> type) {
		this.type = type;
	}
	
	public List<InArgument> getArguments() {
		if (this.arguments == null)
			this.arguments = new ArrayList<InArgument>();
		return this.arguments;
	}
	
	@Override
	protected void cacheMetadata(ActivityMetadata metadata) throws Exception {
		Class<?>[] types = new Class<?>[this.getArguments().size()];
		for (int i = 0; i < types.length; i++) {
			Argument argument = this.getArguments().get(i);
			types[i] = argument.getArgumentType();
			metadata.bindAndAddArgument(argument, new RuntimeArgument(
					"Argument" + i,
					argument.getArgumentType(),
					// only support in
					ArgumentDirection.In));
		}
		this.constructor = this.type.getConstructor(types);
	}
	
	@Override
	protected Object execute(CodeActivityContext context) throws Exception {
		Object[] objects = new Object[this.getArguments().size()];
		for (int i = 0; i < objects.length; i++)
			objects[i] = this.getArguments().get(i).get(context);
		return this.constructor.newInstance(objects);
	}
}
