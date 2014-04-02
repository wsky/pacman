package com.taobao.top.pacman.definition;

import org.junit.Test;

public class TryCatchTest extends DefinitionTestBase {
	@Test
	public void try_catch_test() {
		this.testMetadata(new TryCatchDefinition().
				Try().
				Activity(new WriteLineDefinition().Text("try")).
				Catch(Exception.class).
				Activity(new WriteLineDefinition().Text("try")).
				Finally().
				Activity(new WriteLineDefinition().Text("try")));
	}

	@Test
	public void try_miss_test() {
		this.testMetadata(new TryCatchDefinition().Catch().End(), true);
	}
}
