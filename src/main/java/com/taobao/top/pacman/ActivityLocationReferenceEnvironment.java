package com.taobao.top.pacman;

import java.util.HashMap;
import java.util.Iterator;
import java.util.Map;

public class ActivityLocationReferenceEnvironment extends LocationReferenceEnvironment {
	private Map<String, LocationReference> declarations;

	// private List<LocationReference> unnamedDeclarations;

	public ActivityLocationReferenceEnvironment(LocationReferenceEnvironment parent) {
		this.setParent(parent);
	}

	public Map<String, LocationReference> getDeclarations() {
		if (this.declarations == null)
			this.declarations = new HashMap<String, LocationReference>();
		return this.declarations;
	}

	@Override
	public boolean isVisible(LocationReference locationReference) {
		// TODO check declarations
		return false;
	}

	@Override
	public LocationReference getLocationReference(String name) {
		LocationReference locationReference = this.getLocationReference(this, name);
		if (locationReference != null)
			return locationReference;

		LocationReferenceEnvironment currentEnvironment = this.getParent();
		while (currentEnvironment != null &&
				currentEnvironment instanceof ActivityLocationReferenceEnvironment) {
			locationReference = this.getLocationReference(
					(ActivityLocationReferenceEnvironment) currentEnvironment, name);
			if (locationReference != null)
				return locationReference;
			currentEnvironment = currentEnvironment.getParent();
		}

		// TODO maybe have some other environment

		return null;
	}

	@Override
	public Iterator<LocationReference> getLocationReferences() {
		return this.getDeclarations().values().iterator();
	}

	public void declare(LocationReference locationReference, Activity owner) {
		// TODO check duplicate name
		this.getDeclarations().put(locationReference.getName(), locationReference);
	}

	private LocationReference getLocationReference(ActivityLocationReferenceEnvironment environment, String name) {
		return environment.declarations != null ? environment.declarations.get(name) : null;
	}
}
