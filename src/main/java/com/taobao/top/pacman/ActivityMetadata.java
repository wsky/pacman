package com.taobao.top.pacman;

import java.util.List;

// api wrapper for activtiy metadata, can reject some logic here, like log, validate
public class ActivityMetadata {
	private Activity activity;

	public ActivityMetadata(Activity activity) {
		this.activity = activity;
	}

	public void bindAndAddArgument(Argument binding, RuntimeArgument argument) {
		binding.setRuntimeArgument(argument);
		argument.setBoundArgument(binding);
		this.addArgument(argument);
	}

	public void addArgument(RuntimeArgument argument) {
		if (argument != null)
			this.activity.addRuntimeArgument(argument);
	}

	public void addRuntimeVariable(Variable variable) {
		if (variable != null)
			this.activity.addRuntimeVariable(variable);
	}

	public void addImplementationVariable(Variable variable) {
		if (variable != null)
			this.activity.addImplementationVariable(variable);
	}

	public void addChild(Activity child) {
		if (child != null)
			this.activity.addChild(child);
	}

	public void setChildren(List<Activity> children) {
		this.activity.setChildren(children);
	}
}
