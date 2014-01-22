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
	private Activity child;
	private Activity childChild;
	private InArgument in;
	private Variable var;
	private Variable inner;
	private ProcessActivityCallback callback;

	@Before
	public void before() {
		childChild = new WriteLine();
		childChild.setDisplayName("writeLine");

		If _if = new If();
		_if.setDisplayName("if");
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

		root.setDisplayName("root");

		callback = createCallback();
	}

	@Test
	public void cache_metadata_test() {
		root.cacheMetadata(new ActivityMetadata(root));
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

		assertNotSame(parent.getPublicEnvironment(), current.getPublicEnvironment());
		switch (type) {
		case Child:
			assertEquals(null, current.getParentEnvironment());
			break;
		case ArgumentExpression:
			assertEquals(parent.getPublicEnvironment().getParent(), current.getParentEnvironment());
			break;
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

	private ProcessActivityCallback createCallback() {
		return new ProcessActivityCallback() {

			@Override
			public void execute(Activity activity) {
				this.render(activity);
			}

			private void render(Activity activity) {
				int depth = getDepth(activity);
				String blank = "";
				for (int i = 0; i < depth; i++)
					blank = "    " + blank;

				String id = activity.getMemberOf().getOwner() == null ?
						activity.getId() + "" : getId(activity);
				
				System.out.println(String.format("%s%s %s, displayName=%s",
						depth >= 1 ? blank + "- " : blank,
						id,
						activity.getClass().getSimpleName(),
						activity.getDisplayName()));

				for (RuntimeArgument argument : activity.getRuntimeArguments())
					System.out.println(String.format(
							"%s    - %s#%s argument: %s, direction=%s",
							blank, id, argument.getId(), argument.getName(), argument.getDirection()));

				for (Variable variable : activity.getRuntimeVariables())
					System.out.println(String.format(
							"%s    - %s#%s variable: %s, isPublic=%s",
							blank, id, variable.getId(), variable.getName(), variable.isPublic()));

				for (Variable variable : activity.getImplementationVariables())
					System.out.println(String.format(
							"%s    - %s#%s implVariable: %s, isPublic=%s",
							blank, id, variable.getId(), variable.getName(), variable.isPublic()));
			}

			private int getDepth(Activity activity) {
				int depth = 0;
				do {
					if (activity.getParent() == null)
						return depth;
					depth++;
					activity = activity.getParent();
				} while (true);
			}

			private String getId(Activity activity) {
				String id = Integer.toString(activity.getId());
				while (activity.getParent() != null) {
					id = activity.getParent().getId() + "-" + id;
					activity = activity.getParent();
				}
				return id;
			}
		};
	}
}
