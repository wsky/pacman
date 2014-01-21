package com.taobao.top.pacman;

import static org.junit.Assert.*;

import org.junit.Test;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;

public class LocationAndEnvironmentTest {
	@Test
	public void location_test() {
		this.reference_test(new Variable("var"));
		this.reference_test(new RuntimeArgument("in", Boolean.class, ArgumentDirection.In));
		this.reference_test(new RuntimeArgument("out", Boolean.class, ArgumentDirection.Out));
	}

	@Test
	public void get_location_reference_test() {
		ActivityLocationReferenceEnvironment parent = new ActivityLocationReferenceEnvironment(null);
		parent.declare(new Variable("var"), null);
		parent.declare(new RuntimeArgument("arg", Object.class, ArgumentDirection.In), null);
		ActivityLocationReferenceEnvironment environment = new ActivityLocationReferenceEnvironment(parent);
		environment.declare(new RuntimeArgument("var", Object.class, ArgumentDirection.In), null);
		
		assertNotNull(environment.getLocationReference("var"));
		assertEquals(RuntimeArgument.class, environment.getLocationReference("var").getClass());
		
		assertNotNull(environment.getLocationReference("arg"));

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
