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

	protected boolean tryPopuateValue(LocationEnvironment environment,
			ActivityInstance activityInstance,
			ActivityContext resolutionContext,
			Object value,
			Location resultLocation) {
		if (value != null) {
			Helper.assertNull(resultLocation);
			Location location = new Location();
			environment.declare(this, location, activityInstance);
			location.setValue(value);
			return true;
		}

		if (this.getBoundArgument().getExpression() != null) {
			// TODO skip fastpath
			// Location location = new Location();
			// environment.declare(this, location, activityInstance);

			// NOTE fast-path as some type of argument just refer to another argument
			resolutionContext.setActivity(this.getBoundArgument().getExpression());
			return this.boundArgument.tryPopulateValue(environment, activityInstance, resolutionContext);
		}

		if (resultLocation != null && this.getOwner().isResultArgument(this)) {
			// NOTE here only works for resultArgument
			// FIXME resultLocation should be referenceLocation, not direct
			environment.declare(this, resultLocation, activityInstance);
			return true;
		}

		// only declare location but no value
		Location location = new Location();
		environment.declare(this, location, activityInstance);
		return true;
	}

	@Override
	protected Location getLocation(ActivityContext context) {
		Helper.assertNotNull(context);
		Helper.assertTrue(this.isInTree());

		Location location;
		if (!context.allowChainedEnvironmentAccess()) {
			Helper.assertEquals(this.getOwner(), context.getActivity());
			Object[] ret = context.getEnvironment().tryGetLocation(this.getId());
			location = (Location) ret[1];
			Helper.assertTrue((Boolean) ret[0]);
		} else {
			Helper.assertTrue(this.getOwner() == context.getActivity() ||
					// check activity visiable, as argument's immutability
					this.getOwner() == context.getActivity().getMemberOf().getOwner());

			Object[] ret = context.getEnvironment().tryGetLocation(this.getId(), this.getOwner());
			location = (Location) ret[1];
			Helper.assertTrue((Boolean) ret[0]);
		}

		return location;
	}

	private boolean isInTree() {
		return this.getOwner() != null;
	}

	public enum ArgumentDirection {
		In, Out
	}
}
