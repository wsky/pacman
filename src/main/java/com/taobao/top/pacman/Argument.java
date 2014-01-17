package com.taobao.top.pacman;

public abstract class Argument {
	private String name;
	private RuntimeArgument runtimeArgument;

	protected String getName() {
		return this.name;
	}

	protected void setName(String name) {
		this.name = name;
	}

	protected RuntimeArgument getRuntimeArgument() {
		return this.runtimeArgument;
	}

	protected void setRuntimeArgument(RuntimeArgument runtimeArgument) {
		this.runtimeArgument = runtimeArgument;
	}
}
