package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;

import com.taobao.top.pacman.runtime.BookmarkManager;

public abstract class Activity {
	private static final List<Activity> emptyActivities = new ArrayList<Activity>(0);
	private static final List<RuntimeArgument> emptyArguments = new ArrayList<RuntimeArgument>(0);
	private static final List<Variable> emptyVariables = new ArrayList<Variable>(0);

	private int id;
	private String displayName;
	private RootProperties rootProperties;
	private Activity root;
	private Activity parent;

	private List<Activity> children;
	private List<Activity> implementationChildren;
	private List<RuntimeArgument> runtimeArguments;
	private List<Variable> runtimeVariables;
	private List<Variable> implementationVariables;
	private int symbolCount;

	private LocationReferenceEnvironment publicEnvironment;
	private LocationReferenceEnvironment implementationEnvironment;

	private RelationshipType relationshipToParent;
	// which it belong to
	private ActivityMembers memberOf;
	// it's private members
	private ActivityMembers parentOf;

	private MetadataCacheState cacheState;

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

	protected List<Activity> getImplementationChildren() {
		return this.implementationChildren;
	}

	protected void addImplementationChild(Activity implementationChild) {
		if (this.implementationChildren == null)
			this.implementationChildren = new ArrayList<Activity>();
		this.implementationChildren.add(implementationChild);
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

	protected void setRuntimeVariables(List<Variable> variables) {
		this.runtimeVariables = variables;
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

	protected int getSymbolCount() {
		return this.symbolCount;
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

	protected ActivityMembers getMemberOf() {
		return this.memberOf;
	}

	protected ActivityMembers getParentOf() {
		return this.parentOf;
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
			// not break, same as VariableDefault
		case VariableDefault:
			parentEnvironment = this.getParent().getPublicEnvironment();
			break;
		case ImplementationChild:
			parentEnvironment = this.getParent().getImplementationEnvironment();
			break;
		default:
			break;
		}

		return parentEnvironment;
	}

	protected void initializeAsRoot(LocationReferenceEnvironment hostEnvironment) {
		this.parent = null;
		this.parentOf = null;

		// TODO generate cacheId for avoid loop
		this.clearCachedMetadata();
		
		this.root = this;
		this.rootProperties = new RootProperties();
		this.rootProperties.HostEnvironment = hostEnvironment;

		this.memberOf = new ActivityMembers();
	}

	protected void initializeRelationship(RuntimeArgument argument) {
		this.initializeRelationship(argument.getOwner(), RelationshipType.ArgumentExpression, true);
	}

	protected void initializeRelationship(Variable variable, boolean isPublic) {
		this.initializeRelationship(variable.getOwner(), RelationshipType.VariableDefault, isPublic);
	}

	protected void initializeRelationship(Activity parent, RelationshipType relationshipType, boolean isPublic) {
		this.parent = parent;
		this.root = parent.getRoot();
		this.relationshipToParent = relationshipType;

		this.clearCachedMetadata();

		// set activity visibility
		if (isPublic)
			this.memberOf = parent.memberOf;
		else {
			// parent private members init in child init()
			if (parent.parentOf == null)
				parent.parentOf = new ActivityMembers(parent.memberOf, parent.getId());
			this.memberOf = parent.parentOf;
		}
	}

	protected void clearCachedMetadata() {
		this.children = null;
		this.implementationChildren = null;
		this.runtimeArguments = null;
		this.runtimeVariables = null;
		this.implementationVariables = null;
		this.cacheState = MetadataCacheState.Uncached;
	}

	protected void setCached() {
		this.cacheState = MetadataCacheState.Full;
	}

	protected void setRuntimeReady() {
		this.cacheState = MetadataCacheState.RuntimeReady;
	}

	protected boolean isMetadataCached() {
		return this.cacheState != MetadataCacheState.Uncached;
	}

	protected boolean isRuntimeReady() {
		return this.cacheState == MetadataCacheState.RuntimeReady;
	}

	protected final void internalCacheMetadata() {
		this.onInternalCacheMetadata();

		if (this.children == null)
			this.children = emptyActivities;

		if (this.implementationChildren == null)
			this.implementationChildren = emptyActivities;

		if (this.runtimeArguments == null)
			this.runtimeArguments = emptyArguments;
		else
			this.symbolCount += this.runtimeArguments.size();

		if (this.runtimeVariables == null)
			this.runtimeVariables = emptyVariables;
		else
			this.symbolCount += this.runtimeVariables.size();

		if (this.implementationVariables == null)
			this.implementationVariables = emptyVariables;
		else
			this.symbolCount += this.implementationVariables.size();

		this.cacheState = MetadataCacheState.Partial;
	}

	protected void internalExecute(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) throws Exception {
	}

	protected void internalAbort(ActivityInstance instance, ActivityExecutor executor, Exception terminationReason) {
	}

	protected void internalCancel(ActivityInstance instance, ActivityExecutor executor, BookmarkManager bookmarkManager) {
		NativeActivityContext context = executor.NativeActivityContextPool.acquire();
		try {
			context.initialize(instance, executor, bookmarkManager);
			context.cancel();
		} catch (Exception e) {
			context.dispose();
			executor.NativeActivityContextPool.release(context);
		}
	}

	protected void onInternalCacheMetadata() {
		this.cacheMetadata(new ActivityMetadata(this, this.getParentEnvironment()));
	}

	protected void cacheMetadata(ActivityMetadata metadata) {
		// TODO default dynamic scan and prepare
	}

	protected boolean isResultArgument(RuntimeArgument runtimeArgument) {
		return false;
	}

	public class RootProperties {
		public LocationReferenceEnvironment HostEnvironment;
	}

	public enum MetadataCacheState {
		Uncached,
		Partial,
		Full,
		RuntimeReady
	}
}
