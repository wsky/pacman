package com.taobao.top.pacman;

import com.taobao.top.pacman.expressions.FunctionValue;
import com.taobao.top.pacman.expressions.Literal;

public class Variable extends LocationReference {
	private String name;
	private ActivityWithResult _default;
	private boolean isPublic;
	private Activity owner;

	// TODO support unnamed variable
	public Variable() {
	}

	public Variable(String name) {
		this();
		this.name = name;
	}

	public Variable(String name, Object constValue) {
		this(name);
		this._default = new Literal(constValue);
	}

	public Variable(Function<ActivityContext, Object> expression) {
		this(null, expression);
	}

	public Variable(String name, Function<ActivityContext, Object> expression) {
		this(name);
		this._default = new FunctionValue(expression);
	}

	@Override
	protected String getName() {
		return this.name;
	}

	protected ActivityWithResult getDefault() {
		return this._default;
	}

	protected boolean isPublic() {
		return isPublic;
	}

	protected Activity getOwner() {
		return this.owner;
	}

	public void initializeRelationship(Activity parent, boolean isPublic) {
		// TODO check variable already in use
		this.owner = parent;
		this.isPublic = isPublic;
		if (this._default != null)
			this._default.initializeRelationship(this, isPublic);
	}

	public Object get(ActivityContext context) {
		return context.getValue(this);
	}

	public void set(ActivityContext context, Object value) {
		context.setValue(this, value);
	}

	protected boolean tryPopulateLocation(ActivityExecutor executor, ActivityContext context) {
		boolean sync = true;
		Location variableLocation = new Location();
		context.getEnvironment().declare(this, variableLocation, context.getCurrentInstance());

		if (this.getDefault() == null)
			return sync;

		Object[] ret = this.getDefault().tryGetValue(context);
		if ((Boolean) ret[0])
			variableLocation.setValue(ret[1]);
		else
			sync = false;

		return sync;
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
			Object[] ret = context.getEnvironment().tryGetLocation(this.getId(), this.getOwner());
			location = (Location) ret[1];
			Helper.assertTrue((Boolean) ret[0]);
		}

		return location;
	}

	protected boolean isInTree() {
		return this.getOwner() != null;
	}
}
