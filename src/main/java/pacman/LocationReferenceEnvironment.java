package pacman;

import java.util.Iterator;

public abstract class LocationReferenceEnvironment {
	private LocationReferenceEnvironment parent;

	public LocationReferenceEnvironment getParent() {
		return parent;
	}

	protected void setParent(LocationReferenceEnvironment parent) {
		this.parent = parent;
	}

	public abstract boolean isVisible(LocationReference locationReference);

	public abstract LocationReference getLocationReference(String name);

	public abstract Iterator<LocationReference> getLocationReferences();
}
