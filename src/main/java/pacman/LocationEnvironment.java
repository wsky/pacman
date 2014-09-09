package pacman;

public class LocationEnvironment {
	// TODO improve mem usage if only one symbol
	// private Location singleLocation;
	private Location[] locations;
	private LocationEnvironment parent;
	private Activity activity;

	public LocationEnvironment(Activity activity) {
		this.activity = activity;
	}

	public LocationEnvironment(Activity activity, LocationEnvironment parentEnvironment, int symbolCount) {
		this(activity);
		this.parent = parentEnvironment;
		this.locations = new Location[symbolCount];
	}

	public void declare(LocationReference locationReference, Location location, ActivityInstance activityInstance) {
		this.locations[locationReference.getId()] = location;
	}

	// FIXME declareTemporaryLocation

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

	public Object[] tryGetLocation(int id) {
		Location location = this.getLocation(id);
		return new Object[] { location != null, location };
	}

	public Object[] tryGetLocation(int id, Activity environmentOwner) {
		LocationEnvironment targetEnvironment = this;

		while (targetEnvironment != null && targetEnvironment.activity != environmentOwner)
			targetEnvironment = targetEnvironment.parent;

		if (targetEnvironment == null)
			return new Object[] { false, null };

		Object value = targetEnvironment.getLocation(id);
		return new Object[] { value != null, value };
	}

	public Location getLocation(int id) {
		return this.locations != null && id > -1 && id < this.locations.length ? this.locations[id] : null;
	}

}
