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
			private InArgument in = new InArgument(true);
			private OutArgument out = new OutArgument();
			private Variable var = new Variable("var");
			private Activity child = new If();

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				super.cacheMetadata(metadata);
				metadata.bindAndAddArgument(this.in, new RuntimeArgument("in", Integer.class, ArgumentDirection.In));
				metadata.bindAndAddArgument(this.out, new RuntimeArgument("out", Integer.class, ArgumentDirection.Out));
				metadata.addVariable(this.var);
				metadata.addChild(this.child);
			}
		};
		activity.cacheMetadata(new ActivityMetadata(activity));
		Iterator<RuntimeArgument> iterator = activity.getRuntimeArguments().iterator();
		assertEquals("in", iterator.next().getName());
		assertEquals("out", iterator.next().getName());
		assertEquals("var", activity.getVariables().iterator().next().getName());
		assertEquals(If.class, activity.getChildren().iterator().next().getClass());

	}
}
