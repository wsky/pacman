package com.taobao.top.pacman.runtime;

import com.taobao.top.pacman.Location;

public class LocationEnvironment {
	private Location[] locations;

	public Location getLocation(int id) {
		return id > -1 && id < this.locations.length ? this.locations[id] : null;
	}
}
