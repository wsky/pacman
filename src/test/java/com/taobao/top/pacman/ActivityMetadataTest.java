package com.taobao.top.pacman;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.statements.If;
import com.taobao.top.pacman.statements.WriteLine;

public class ActivityMetadataTest {
	private Activity root;
	private Activity child;
	private Activity childChild;
	private InArgument in;
	private Variable var;
	private Variable inner;

	@Before
	public void before() {
		childChild = new WriteLine();
		If _if = new If();
		_if.Then = childChild;
		child = _if;

		in = new InArgument(true);
		var = new Variable("var", true);
		inner = new Variable("inner", false);

		root = new Activity() {
			public Activity child = ActivityMetadataTest.this.child;
			public InArgument in = ActivityMetadataTest.this.in;
			public OutArgument out = new OutArgument();
			public Variable var = ActivityMetadataTest.this.var;
			private Variable inner = ActivityMetadataTest.this.inner;

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
	}

	@Test
	public void cache_metadata_test() {
		root.cacheMetadata(new ActivityMetadata(root));
		assertRoot();
	}

	@Test
	public void activity_tree_test() {
		ActivityLocationReferenceEnvironment hostEnvironment = new ActivityLocationReferenceEnvironment(null);
		ActivityUtilities.cacheRootMetadata(root, hostEnvironment);
		assertRoot();
		// root
		assertActivity(root, null, root, null, true);
		assertEquals(hostEnvironment, root.getParentEnvironment().getParent());
		assertNotSame(hostEnvironment, root.getPublicEnvironment());
		// child
		assertActivity(root, root, child, RelationshipType.Child, true);
		// childChild
		assertActivity(root, child, childChild, RelationshipType.Child, true);
		// runtimeArgument
		assertEquals(0, in.getRuntimeArgument().getId());
		assertEquals(root, in.getRuntimeArgument().getOwner());
		assertActivity(root, root, in.getExpression(), RelationshipType.ArgumentExpression, false);
		// runtimeVariable
		assertEquals(root, var.getOwner());
		assertEquals(2, var.getId());
		assertActivity(root, root, var.getDefault(), RelationshipType.VariableDefault, true);
		// implementationVariable
		assertEquals(root, inner.getOwner());
		assertEquals(3, inner.getId());
		assertActivity(root, root, inner.getDefault(), RelationshipType.VariableDefault, false);
	}

	private void assertRoot() {
		assertEquals(If.class, root.getChildren().get(0).getClass());
		assertEquals("in", root.getRuntimeArguments().get(0).getName());
		assertEquals("out", root.getRuntimeArguments().get(1).getName());
		assertEquals("var", root.getRuntimeVariables().get(0).getName());
		assertEquals("inner", root.getImplementationVariables().get(0).getName());
	}

	private void assertActivity(Activity root, Activity parent, Activity current, RelationshipType type, boolean isPublic) {
		assertEquals(root, current.getRoot());
		assertEquals(parent, current.getParent());
		if (type == null)
			return;
		switch (type) {
		case Child:
			assertNotSame(parent.getPublicEnvironment(), current.getPublicEnvironment());
			assertEquals(null, current.getParentEnvironment());
			break;
		case ArgumentExpression:
			assertEquals(parent.getPublicEnvironment().getParent(), current.getParentEnvironment());
			// FIXME expression should be processed in cacheMetadata()
			// assertEquals(parent.getPublicEnvironment(), current.getPublicEnvironment());
			break;
		case VariableDefault:
			assertEquals(parent.getPublicEnvironment(), current.getParentEnvironment());
			// FIXME expression should be processed in cacheMetadata()
			// assertEquals(isPublic ?
			// parent.getPublicEnvironment() :
			// parent.getImplementationEnvironment(),
			// current.getPublicEnvironment());
			break;
		default:
			break;
		}

	}
}
