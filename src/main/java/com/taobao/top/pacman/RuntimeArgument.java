package com.taobao.top.pacman;

public class RuntimeArgument extends LocationReference {
	private String name;
	private ArgumentDirection direction;
	private Argument boundArgument;
	private Activity owner;

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

	public Activity getOwner() {
		return this.owner;
	}

	public void initializeRelationship(Activity parent) {
		this.owner = parent;
		if (this.boundArgument != null &&
				this.boundArgument.getExpression() != null)
			this.boundArgument.getExpression().initializeRelationship(this);
	}

	public boolean tryPopuateValue(LocationEnvironment environment,
			ActivityInstance activityInstance,
			Object value,
			Location resultLocation) {
		if (value != null) {
			Helper.assertNotNull(resultLocation);
			Location location = new Location();
			environment.declare(this, location, activityInstance);
			location.setValue(value);
			return true;
		}

		if (this.getBoundArgument().getExpression() != null) {
			// FIXME some type of argument just refer to another argument
			Location location = new Location();
			environment.declare(this, location, activityInstance);
			return false;
		}

		if (resultLocation != null && this.getOwner().isResultArgument(this)) {
			environment.declare(this, resultLocation, activityInstance);
			return true;
		}

		Location location = new Location();
		environment.declare(this, location, activityInstance);
		return true;
	}

	public enum ArgumentDirection {
		In, Out
	}
}
