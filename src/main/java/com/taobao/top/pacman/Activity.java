package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.runtime.BookmarkManager;

public class Activity {
	private int id;
	private String displayName;

	// private Activity runtimeImplementation;
	private List<Activity> runtimeChildren;
	private List<RuntimeArgument> runtimeArguments;
	private List<Variable> runtimeVariables;

	public String getDisplayName() {
		return displayName;
	}

	public void setDisplayName(String displayName) {
		this.displayName = displayName;
	}

	protected void setId(int id) {
		this.id = id;
	}

	protected int getId() {
		return this.id;
	}

	protected void addChild(Activity child) {
		if (this.runtimeChildren == null)
			this.runtimeChildren = new ArrayList<Activity>();
		this.runtimeChildren.add(child);
	}

	protected void addArgument(RuntimeArgument argument) {
		if (this.runtimeArguments == null)
			this.runtimeArguments = new ArrayList<RuntimeArgument>();
		this.runtimeArguments.add(argument);
	}

	protected void addVariable(Variable variable) {
		if (this.runtimeVariables == null)
			this.runtimeVariables = new ArrayList<Variable>();
		this.runtimeVariables.add(variable);
	}

	protected void internalExecute(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
	}

	protected void internalAbort(ActivityInstance instance, ActivityExecutor executor, Exception terminationReason) {
	}

	protected void internalCancel(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
	}

	protected void cacheMeta() {
		// TODO default dynamic scan and prepare
	}
}
