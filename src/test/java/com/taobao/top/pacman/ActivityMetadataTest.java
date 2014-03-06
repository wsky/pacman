package com.taobao.top.pacman;

import static org.junit.Assert.*;

import org.junit.Before;
import org.junit.Test;

import com.taobao.top.pacman.ActivityUtilities.ProcessActivityCallback;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.statements.If;
import com.taobao.top.pacman.statements.WriteLine;

public class ActivityMetadataTest {
	private Activity root;
	private Activity nest;
	private Activity child;
	private Activity childChild;
	private InArgument in;
	private OutArgument out;
	private Variable var;
	private Variable inner;
	private ProcessActivityCallback callback;

	@Before
	public void before() {
		in = new InArgument(true);
		var = new Variable("var", true);
		out = new OutArgument();
		inner = new Variable("inner", false);

		childChild = new WriteLine();
		((WriteLine) childChild).Text = new InArgument(var);
		childChild.setDisplayName("writeLine");

		If _if = new If();
		_if.setDisplayName("if");
		_if.Then = childChild;
		child = _if;

		nest = new Activity() {
			private InArgument nestIn = new InArgument(inner);

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				Helper.assertTrue(metadata.getEnvironment().isVisible(inner));
				metadata.bindAndAddArgument(this.nestIn, new RuntimeArgument("nestIn", Integer.class, ArgumentDirection.In));
			}
		};
		nest.setDisplayName("nest");

		root = new Activity() {
			public Activity child = ActivityMetadataTest.this.child;
			public Activity nest = ActivityMetadataTest.this.nest;
			public InArgument in = ActivityMetadataTest.this.in;
			public OutArgument out = ActivityMetadataTest.this.out;
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
				metadata.addImplementationChild(this.nest);
			}
		};

		root.setDisplayName("root");

		callback = new RenderProcessActivityCallback();
	}

	@Test
	public void cache_metadata_test() {
		root.initializeAsRoot(new ActivityLocationReferenceEnvironment(null));
		root.cacheMetadata(new ActivityMetadata(root, root.getParentEnvironment()));
		root.setRuntimeReady();
		assertRoot();
	}

	@Test
	public void activity_tree_process_test() {
		ActivityLocationReferenceEnvironment hostEnvironment = new ActivityLocationReferenceEnvironment(null);
		ActivityUtilities.cacheRootMetadata(root, hostEnvironment, callback);
		assertRoot();

		// root
		assertActivity(root, null, root, null, true);
		assertEquals(hostEnvironment, root.getParentEnvironment().getParent());
		assertNotSame(hostEnvironment, root.getPublicEnvironment());
		assertEquals(1, root.getId());

		// child
		assertActivity(root, root, child, RelationshipType.Child, true);

		// childChild
		assertActivity(root, child, childChild, RelationshipType.Child, true);

		// runtimeArgument
		assertActivity(root, root, in.getExpression(), RelationshipType.ArgumentExpression, true);
		assertEquals(0, in.getRuntimeArgument().getId());
		assertEquals(root, in.getRuntimeArgument().getOwner());
		// env
		assertEquals(in.getRuntimeArgument(), root.getImplementationEnvironment().getLocationReference("in"));

		// runtimeVariable
		assertActivity(root, root, var.getDefault(), RelationshipType.VariableDefault, true);
		assertEquals(root, var.getOwner());
		assertEquals(2, var.getId());
		assertTrue(var.isPublic());
		// env
		assertEquals(var, root.getPublicEnvironment().getLocationReference("var"));

		// implementationVariable
		assertActivity(root, root, inner.getDefault(), RelationshipType.VariableDefault, false);
		assertEquals(root, inner.getOwner());
		assertEquals(3, inner.getId());
		assertFalse(inner.isPublic());
		// env
		assertEquals(inner, root.getImplementationEnvironment().getLocationReference("inner"));
	}

	@Test
	public void reference_to_undifined_variable_test() {
		// TODO test undefined variable validate
	}

	private void assertRoot() {
		assertTrue(root.isMetadataCached());
		assertTrue(root.isRuntimeReady());
		assertEquals(If.class, root.getChildren().get(0).getClass());
		assertEquals("in", root.getRuntimeArguments().get(0).getName());
		assertEquals("out", root.getRuntimeArguments().get(1).getName());
		assertEquals("var", root.getRuntimeVariables().get(0).getName());
		assertEquals("inner", root.getImplementationVariables().get(0).getName());
	}

	private void assertActivity(Activity root, Activity parent, Activity current, RelationshipType type, boolean isPublic) {
		assertEquals(root, current.getRoot());
		assertEquals(parent, current.getParent());
		assertTrue(current.isMetadataCached());
		if (type == null)
			return;

		assertNotSame(parent.getPublicEnvironment(), current.getPublicEnvironment());
		switch (type) {
		case ArgumentExpression:
			assertEquals(parent.getPublicEnvironment().getParent(), current.getParentEnvironment());
			break;
		case Child:
		case VariableDefault:
			assertEquals(parent.getPublicEnvironment(), current.getParentEnvironment());
			break;
		default:
			break;
		}

		if (isPublic) {
			assertEquals(parent.getMemberOf(), current.getMemberOf());
			assertEquals(null, current.getMemberOf().getOwner());
			assertEquals(null, current.getMemberOf().getParent());
		} else {
			assertEquals(parent.getParentOf(), current.getMemberOf());
			assertEquals(parent, current.getMemberOf().getOwner());
			assertEquals(parent.getMemberOf(), current.getMemberOf().getParent());
		}
	}
}
