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
	public void location_reference_env_test() {
		Variable var = new Variable("var");
		RuntimeArgument arg = new RuntimeArgument("arg", Object.class, ArgumentDirection.In);
		RuntimeArgument varArg = new RuntimeArgument("var", Object.class, ArgumentDirection.In);
		ActivityLocationReferenceEnvironment parent = new ActivityLocationReferenceEnvironment(null);
		parent.declare(var, null);
		parent.declare(arg, null);
		ActivityLocationReferenceEnvironment environment = new ActivityLocationReferenceEnvironment(parent);
		environment.declare(varArg, null);

		assertNotNull(environment.getLocationReference("arg"));
		assertNotNull(environment.getLocationReference("var"));
		assertEquals(RuntimeArgument.class, environment.getLocationReference("var").getClass());

		assertTrue(parent.isVisible(arg));
		assertTrue(environment.isVisible(arg));

		assertTrue(parent.isVisible(var));
		assertTrue(environment.isVisible(var));

		assertFalse(parent.isVisible(varArg));
		assertTrue(environment.isVisible(varArg));
	}

	private void reference_test(LocationReference locationReference) {
		LocationEnvironment environment = new LocationEnvironment(null, null, 1);
		environment.declare(locationReference, new Location(true), null);
		Location location = environment.getLocation(locationReference.getId());
		assertTrue((Boolean) location.getValue());
		location.setValue(false);
		location = environment.getLocation(locationReference.getId());
		assertFalse((Boolean) location.getValue());
	}
}
