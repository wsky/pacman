package com.taobao.top.pacman;

public class LocationEnvironment {
	private Location singleLocation;
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

	// FIXME impl collapseTemporaryResolutionLocations for gathering outputs
	// called for asynchronous argument resolution to collapse Location<Location<T>> to Location<T> in the environment
	protected void collapseTemporaryResolutionLocations() {
		// if (this.locations == null)
		// {
		// if (this.singleLocation != null)
		// //&&object.ReferenceEquals(this.singleLocation.TemporaryResolutionEnvironment, this))
		// {
		// if (this.singleLocation.getValue() == null)
		// {
		// this.singleLocation = (Location)this.singleLocation.CreateDefaultValue();
		// }
		// else
		// {
		// this.singleLocation = ((Location)this.singleLocation.Value).CreateReference(this.singleLocation.BufferGetsOnCollapse);
		// }
		// }
		// }
		// else
		// {
		// for (int i = 0; i < this.locations.Length; i++)
		// {
		// Location referenceLocation = this.locations[i];
		//
		// if (referenceLocation != null &&
		// object.ReferenceEquals(referenceLocation.TemporaryResolutionEnvironment, this))
		// {
		// if (referenceLocation.Value == null)
		// {
		// this.locations[i] = (Location)referenceLocation.CreateDefaultValue();
		// }
		// else
		// {
		// this.locations[i] = ((Location)referenceLocation.Value).CreateReference(referenceLocation.BufferGetsOnCollapse);
		// }
		// }
		// }
		// }
	}
}
