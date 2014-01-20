package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.runtime.BookmarkManager;

public abstract class Activity {
	private int id;
	private String displayName;
	private Activity root;
	private Activity parent;

	private List<Activity> children;
	private List<RuntimeArgument> runtimeArguments;
	private List<Variable> variables;

	private LocationReferenceEnvironment publicEnvironment;
	private LocationReferenceEnvironment privateEnvironment;

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

	protected Activity getRoot() {
		return root;
	}

	protected void setRoot(Activity root) {
		this.root = root;
	}

	protected Activity getParent() {
		return parent;
	}

	protected void setParent(Activity parent) {
		this.parent = parent;
	}

	protected List<Activity> getChildren() {
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

	protected List<RuntimeArgument> getRuntimeArguments() {
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

	protected List<Variable> getVariables() {
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

	protected LocationReferenceEnvironment getPublicEnvironment() {
		return publicEnvironment;
	}

	protected void setPublicEnvironment(LocationReferenceEnvironment publicEnvironment) {
		this.publicEnvironment = publicEnvironment;
	}

	protected LocationReferenceEnvironment getPrivateEnvironment() {
		return privateEnvironment;
	}

	protected void setPrivateEnvironment(LocationReferenceEnvironment privateEnvironment) {
		this.privateEnvironment = privateEnvironment;
	}

	protected void initializeAsRoot() {
		this.parent = null;
		this.root = this;
	}

	protected LocationReferenceEnvironment getParentEnvironment() {
		// TODO Auto-generated method stub
		return null;
	}

	protected void initializeRelationship(Activity parent) {
	}

	protected void internalCacheMetadata() {
		this.cacheMetadata(new ActivityMetadata(this));
	}

	protected void internalExecute(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
	}

	protected void internalAbort(ActivityInstance instance, ActivityExecutor executor, Exception terminationReason) {
	}

	protected void internalCancel(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
	}

	protected void cacheMetadata(ActivityMetadata metadata) {
		// TODO default dynamic scan and prepare
	}
}
