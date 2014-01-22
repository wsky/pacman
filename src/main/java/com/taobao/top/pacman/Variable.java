package com.taobao.top.pacman;

import com.taobao.top.pacman.expressions.FunctionValue;
import com.taobao.top.pacman.expressions.Literal;

public class Variable extends LocationReference {
	private String name;
	private ActivityWithResult _default;
	private boolean isPublic;
	private Activity owner;

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
}
