package com.taobao.top.pacman.definition;

public class DefinitionUtilities {
	public static void traversal(ActivityDefinition definition, Callback callback) {
		callback.execute(definition);
		for (ActivityDefinition child : definition.activities)
			traversal(child, callback);
	}

	public static interface Callback {
		public void execute(ActivityDefinition definition);
	}
}
