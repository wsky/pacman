package com.taobao.top.pacman;

import java.util.List;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityUtilities {
	public static void cacheRootMetadata(Activity activity, LocationReferenceEnvironment hostEnvironment) {
		activity.initializeAsRoot(hostEnvironment);
		processActivityTree(activity, 0);
		// TODO support error validator
	}

	private static void processActivityTree(Activity currentActivity, int depth) {
		if (depth > 10)
			throw new SecurityException("depth too large");

		processActivity(currentActivity);

		if (currentActivity.getChildren() == null)
			return;
		for (Activity child : currentActivity.getChildren()) {
			processActivityTree(child, depth + 1);
		}
	}

	private static void processActivity(Activity activity) {
		activity.internalCacheMetadata();

		ActivityLocationReferenceEnvironment implementationEnvironment = null;
		ActivityLocationReferenceEnvironment publicEnvironment = null;
		AtomicInteger environmentId = new AtomicInteger(0);

		processChildren(activity, activity.getChildren());
		publicEnvironment = processArguments(activity, activity.getRuntimeArguments(), implementationEnvironment, environmentId);
		processVariables(activity, activity.getRuntimeVariables(), true, publicEnvironment, environmentId);
		processVariables(activity, activity.getImplementationVariables(), false, implementationEnvironment, environmentId);

		activity.setPublicEnvironment(publicEnvironment);
		activity.setImplementationEnvironment(implementationEnvironment);
	}

	private static void processChildren(Activity parent, List<Activity> children) {
		for (Activity activity : children)
			activity.initializeRelationship(parent, RelationshipType.Child);
	}

	private static ActivityLocationReferenceEnvironment processArguments(
			Activity owner,
			List<RuntimeArgument> arguments,
			ActivityLocationReferenceEnvironment environment,
			AtomicInteger environmentId) {
		if (arguments.size() == 0)
			return environment;
		if (environment == null)
			environment = new ActivityLocationReferenceEnvironment(owner.getParentEnvironment());
		for (RuntimeArgument argument : arguments) {
			argument.initializeRelationship(owner);
			argument.setId(environmentId.getAndIncrement());
			environment.declare(argument, owner);
		}
		return environment;
	}

	private static ActivityLocationReferenceEnvironment processVariables(
			Activity owner,
			List<Variable> variables,
			boolean isPublic,
			ActivityLocationReferenceEnvironment environment,
			AtomicInteger environmentId) {
		if (variables.size() == 0)
			return environment;
		if (environment == null)
			environment = new ActivityLocationReferenceEnvironment(owner.getParentEnvironment());
		for (Variable variable : variables) {
			variable.initializeRelationship(owner, isPublic);
			variable.setId(environmentId.getAndIncrement());
			environment.declare(variable, owner);
		}
		return environment;
	}

	protected static void processActivityInstanceTree(ActivityInstance rootInstance, ActivityExecutor executor) {

	}
}
