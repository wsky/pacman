package com.taobao.top.pacman;

public class LocationEnvironment {
	private Location[] locations;
	private LocationEnvironment parent;

	public LocationEnvironment() {
	}

	public LocationEnvironment(LocationEnvironment parentEnvironment, int symbolCount) {
		this.parent = parentEnvironment;
		this.locations = new Location[symbolCount];
	}

	public Location getLocation(LocationReference locationReference) {
		return this.getLocation(locationReference.getId());
	}

	public Location getLocation(int id) {
		return this.locations != null && id > -1 && id < this.locations.length ? this.locations[id] : null;
	}

	public void declare(LocationReference locationReference, Location location, ActivityInstance activityInstance) {
		this.locations[locationReference.getId()] = location;
	}
}
