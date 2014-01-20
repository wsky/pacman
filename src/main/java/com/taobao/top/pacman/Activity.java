package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.runtime.BookmarkManager;

public abstract class Activity {
	private int id;
	private String displayName;

	
	private Activity parent;
	// TODO dynamic
	// private Activity runtimeImplementation;

	private List<Activity> children;
	private List<RuntimeArgument> runtimeArguments;
	private List<Variable> variables;

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
	
	protected Activity getParent() {
		return parent;
	}

	protected void setParent(Activity parent) {
		this.parent = parent;
	}

	protected Iterable<Activity> getChildren() {
		return this.children;
	}

	protected void setChildren(List<Activity> children) {
		this.children = children;
	}

	protected void addChild(Activity child) {
		if (this.children == null)
			this.children = new ArrayList<Activity>();
		this.children.add(child);
	}

	protected Iterable<RuntimeArgument> getRuntimeArguments() {
		return this.runtimeArguments;
	}

	protected void setRuntimeArguments(List<RuntimeArgument> runtimeArguments) {
		this.runtimeArguments = runtimeArguments;
	}

	protected void addArgument(RuntimeArgument argument) {
		if (this.runtimeArguments == null)
			this.runtimeArguments = new ArrayList<RuntimeArgument>();
		this.runtimeArguments.add(argument);
	}

	protected Iterable<Variable> getVariables() {
		return this.variables;
	}

	protected void setVariables(List<Variable> variables) {
		this.variables = variables;
	}

	protected void addVariable(Variable variable) {
		if (this.variables == null)
			this.variables = new ArrayList<Variable>();
		this.variables.add(variable);
	}

	protected void cacheMetadata(ActivityMetadata metadata) {
		// TODO default dynamic scan and prepare
	}
	
	protected void internalExecute(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
	}

	protected void internalAbort(ActivityInstance instance, ActivityExecutor executor, Exception terminationReason) {
	}

	protected void internalCancel(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
	}
}
