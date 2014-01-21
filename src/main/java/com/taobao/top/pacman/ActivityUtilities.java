package com.taobao.top.pacman;

import java.util.List;
import java.util.Stack;
import java.util.concurrent.atomic.AtomicInteger;

public class ActivityUtilities {
	public static void cacheRootMetadata(Activity activity, LocationReferenceEnvironment hostEnvironment) {
		activity.initializeAsRoot(hostEnvironment);
		processActivityTree(activity);
		// TODO support error validator
	}

	private static void processActivityTree(Activity root) {
		Stack<Activity> stack = new Stack<Activity>();
		stack.push(root);
		Activity currentActivity;
		do {
			currentActivity = stack.pop();
			processActivity(currentActivity, stack);
		} while (!stack.isEmpty());
	}

	private static void processActivity(Activity activity, Stack<Activity> stack) {
		activity.internalCacheMetadata();
		processChildren(activity, activity.getChildren(), stack);

		ActivityLocationReferenceEnvironment implementationEnvironment = null;
		ActivityLocationReferenceEnvironment publicEnvironment = null;
		AtomicInteger environmentId = new AtomicInteger(0);

		implementationEnvironment = processArguments(activity,
				activity.getRuntimeArguments(),
				implementationEnvironment,
				environmentId,
				stack);
		publicEnvironment = processVariables(activity,
				activity.getRuntimeVariables(),
				true,
				publicEnvironment,
				environmentId,
				stack);
		implementationEnvironment = processVariables(activity,
				activity.getImplementationVariables(),
				false,
				implementationEnvironment,
				environmentId,
				stack);

		if (publicEnvironment == null)
			publicEnvironment = new ActivityLocationReferenceEnvironment(activity.getParentEnvironment());

		activity.setPublicEnvironment(publicEnvironment);
		activity.setImplementationEnvironment(implementationEnvironment);
	}

	private static void processChildren(Activity parent, List<Activity> children, Stack<Activity> stack) {
		for (Activity activity : children) {
			activity.initializeRelationship(parent, RelationshipType.Child);
			stack.push(activity);
		}
	}

	private static ActivityLocationReferenceEnvironment processArguments(
			Activity owner,
			List<RuntimeArgument> arguments,
			ActivityLocationReferenceEnvironment environment,
			AtomicInteger environmentId,
			Stack<Activity> stack) {
		if (arguments.size() == 0)
			return environment;
		if (environment == null)
			environment = new ActivityLocationReferenceEnvironment(owner.getParentEnvironment());
		for (RuntimeArgument argument : arguments) {
			argument.initializeRelationship(owner);
			argument.setId(environmentId.getAndIncrement());
			environment.declare(argument, owner);

			if (argument.getBoundArgument() != null && argument.getBoundArgument().getExpression() != null)
				stack.add(argument.getBoundArgument().getExpression());
		}
		return environment;
	}

	private static ActivityLocationReferenceEnvironment processVariables(
			Activity owner,
			List<Variable> variables,
			boolean isPublic,
			ActivityLocationReferenceEnvironment environment,
			AtomicInteger environmentId,
			Stack<Activity> stack) {
		if (variables.size() == 0)
			return environment;
		if (environment == null)
			environment = new ActivityLocationReferenceEnvironment(owner.getParentEnvironment());
		for (Variable variable : variables) {
			variable.initializeRelationship(owner, isPublic);
			variable.setId(environmentId.getAndIncrement());
			environment.declare(variable, owner);

			if (variable.getDefault() != null)
				stack.add(variable.getDefault());
		}
		return environment;
	}
}
