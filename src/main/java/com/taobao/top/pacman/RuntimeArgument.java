package com.taobao.top.pacman;

public class RuntimeArgument extends LocationReference {
	private String name;
	private ArgumentDirection direction;
	private Argument boundArgument;

	public RuntimeArgument(String name, Class<?> type, ArgumentDirection direction) {
		this.name = name;
		this.setType(type);
		this.direction = direction;
	}

	@Override
	protected String getName() {
		return this.name;
	}

	protected void setBoundArgument(Argument argument) {
		this.boundArgument = argument;
	}

	public ArgumentDirection getDirection() {
		return this.direction;
	}

	public Argument getBoundArgument() {
		return this.boundArgument;
	}

	public enum ArgumentDirection {
		In, Out
	}
}
