package com.taobao.top.pacman;

import static org.junit.Assert.*;

import java.util.Iterator;

import org.junit.Test;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.statements.If;

public class ActivityMetadataTest {
	@Test
	public void cache_metadata_test() {
		Activity activity = new Activity() {
			public InArgument in = new InArgument(true);
			public OutArgument out = new OutArgument();
			public Variable var = new Variable("var");
			public Activity child = new If();
			private Variable inner = new Variable("inner");

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				super.cacheMetadata(metadata);
				metadata.bindAndAddArgument(this.in, new RuntimeArgument("in", Integer.class, ArgumentDirection.In));
				metadata.bindAndAddArgument(this.out, new RuntimeArgument("out", Integer.class, ArgumentDirection.Out));
				metadata.addRuntimeVariable(this.var);
				metadata.addImplementationVariable(this.inner);
				metadata.addChild(this.child);
			}
		};
		activity.cacheMetadata(new ActivityMetadata(activity));
		Iterator<RuntimeArgument> iterator = activity.getRuntimeArguments().iterator();
		assertEquals("in", iterator.next().getName());
		assertEquals("out", iterator.next().getName());
		assertEquals("var", activity.getRuntimeVariables().iterator().next().getName());
		assertEquals("inner", activity.getImplementationVariables().iterator().next().getName());
		assertEquals(If.class, activity.getChildren().iterator().next().getClass());

		// TODO check releationship/locationReferenceEnvironment
	}
}
