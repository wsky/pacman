package com.taobao.top.pacman;

import java.util.ArrayList;
import java.util.List;

public class LocationEnvironment {
	private List<Location> locations;

	public LocationEnvironment() {
		this.locations = new ArrayList<Location>();
	}

	public Location getLocation(LocationReference locationReference) {
		return this.getLocation(locationReference.getId());
	}

	public Location getLocation(int id) {
		return id > -1 && id < this.locations.size() ? this.locations.get(id) : null;
	}

	public void bindReference(LocationReference locationReference) {
		this.bindReference(locationReference, null);
	}

	public void bindReference(LocationReference locationReference, Object defaultValue) {
		locationReference.setId(this.locations.size());
		this.locations.add(new Location(defaultValue));
	}
}
