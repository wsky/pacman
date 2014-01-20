package com.taobao.top.pacman;

import static org.junit.Assert.*;

import org.junit.Test;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class LocationAndEnvironmentTest {
	@Test
	public void reference_test() {
		this.reference_test(new Variable("var"));
		this.reference_test(new RuntimeArgument("in", Boolean.class, ArgumentDirection.In));
		this.reference_test(new RuntimeArgument("out", Boolean.class, ArgumentDirection.Out));
	}

	@Test
	public void scope_test() {
		// variable in parent activity
		// argument in child activity
	}

	private void reference_test(LocationReference locationReference) {
		LocationEnvironment environment = new LocationEnvironment();
		environment.bindReference(locationReference, true);
		Location location = environment.getLocation(locationReference.getId());
		assertTrue((Boolean) location.getValue());
		location.setValue(false);
		location = environment.getLocation(locationReference.getId());
		assertFalse((Boolean) location.getValue());
	}
}
