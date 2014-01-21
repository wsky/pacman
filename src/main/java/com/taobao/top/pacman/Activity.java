package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.runtime.BookmarkManager;

public abstract class Activity {
	private int id;
	private String displayName;
	private RootProperties rootProperties;
	private Activity root;
	private Activity parent;

	private List<Activity> children;
	private List<RuntimeArgument> runtimeArguments;
	private List<Variable> runtimeVariables;
	private List<Variable> implementationVariables;

	private LocationReferenceEnvironment publicEnvironment;
	private LocationReferenceEnvironment implementationEnvironment;

	private RelationshipType relationshipToParent;

	public String getDisplayName() {
		return this.displayName;
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
		return this.root;
	}

	protected RootProperties getRootProperties() {
		return this.rootProperties;
	}

	protected Activity getParent() {
		return this.parent;
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

	protected void addRuntimeArgument(RuntimeArgument argument) {
		if (this.runtimeArguments == null)
			this.runtimeArguments = new ArrayList<RuntimeArgument>();
		this.runtimeArguments.add(argument);
	}

	protected List<Variable> getRuntimeVariables() {
		return this.runtimeVariables;
	}

	protected void addRuntimeVariable(Variable variable) {
		if (this.runtimeVariables == null)
			this.runtimeVariables = new ArrayList<Variable>();
		this.runtimeVariables.add(variable);
	}

	protected List<Variable> getImplementationVariables() {
		return this.implementationVariables;
	}

	protected void addImplementationVariable(Variable variable) {
		if (this.implementationVariables == null)
			this.implementationVariables = new ArrayList<Variable>();
		this.implementationVariables.add(variable);
	}

	protected LocationReferenceEnvironment getPublicEnvironment() {
		return this.publicEnvironment;
	}

	protected void setPublicEnvironment(LocationReferenceEnvironment environment) {
		this.publicEnvironment = environment;
	}

	protected LocationReferenceEnvironment getImplementationEnvironment() {
		return this.implementationEnvironment;
	}

	protected void setImplementationEnvironment(LocationReferenceEnvironment environment) {
		this.implementationEnvironment = environment;
	}

	protected LocationReferenceEnvironment getParentEnvironment() {
		if (this.parent == null)
			return new ActivityLocationReferenceEnvironment(this.rootProperties.HostEnvironment);

		LocationReferenceEnvironment parentEnvironment = null;
		switch (this.relationshipToParent) {
		case ArgumentExpression:
			parentEnvironment = this.getParent().getPublicEnvironment().getParent();
			if (parentEnvironment == null)
				parentEnvironment = this.getRoot().getRootProperties().HostEnvironment;
			break;
		case Child:
			break;
		case VariableDefault:
			parentEnvironment = this.getParent().getPublicEnvironment();
			break;
		default:
			break;
		}

		return parentEnvironment;
	}

	protected void initializeAsRoot(LocationReferenceEnvironment hostEnvironment) {
		this.parent = null;
		this.root = this;
		this.rootProperties = new RootProperties();
		this.rootProperties.HostEnvironment = hostEnvironment;
	}

	protected void initializeRelationship(Activity parent, RelationshipType relationshipType) {
		this.parent = parent;
		this.root = parent.getRoot();
		this.relationshipToParent = relationshipType;
	}

	protected void clearCachedMetadata() {
		this.children = null;
		this.runtimeArguments = null;
		this.runtimeVariables = null;
		this.implementationVariables = null;
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

	public class RootProperties {
		public LocationReferenceEnvironment HostEnvironment;
	}
}
