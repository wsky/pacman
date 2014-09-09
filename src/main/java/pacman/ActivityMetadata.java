package pacman;

import java.util.List;

// api wrapper for activtiy metadata, can reject some logic here, like log, validate
public class ActivityMetadata {
	private Activity activity;
	private LocationReferenceEnvironment environment;

	public ActivityMetadata(Activity activity, LocationReferenceEnvironment environment) {
		this.activity = activity;
		this.environment = environment;
	}

	public LocationReferenceEnvironment getEnvironment() {
		return this.environment;
	}

	public void bindAndAddArgument(Argument binding, RuntimeArgument argument) {
		bindArgument(binding, argument);
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

	public void setRuntimeVariables(List<Variable> variables) {
		if (variables != null)
			this.activity.setRuntimeVariables(variables);
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

	public void addImplementationChild(Activity implementationChild) {
		if (implementationChild != null)
			this.activity.addImplementationChild(implementationChild);
	}

	public static void bindArgument(Argument binding, RuntimeArgument argument) {
		binding.setRuntimeArgument(argument);
		argument.setBoundArgument(binding);
	}
}
