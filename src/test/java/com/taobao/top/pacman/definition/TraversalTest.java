package com.taobao.top.pacman.definition;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.definition.ActivityDefinition.ProcessCallback;
import com.taobao.top.pacman.definition.DefinitionUtilities.Callback;

public class TraversalTest {
	@Test
	public void traversal_test() {
		ActivityDefinition workflow = new WorkflowDefinition("workflow").
				Sequence().
				Activity(new WriteLineDefinition()).
				Activity(new AssignDefinition()).
				End();
		DefinitionUtilities.traversal(workflow, new Callback() {
			public void execute(ActivityDefinition definition) {
				if (definition.getParent() != null)
					System.out.print(definition.getParent().getDisplayName() + "-");
				System.out.println(definition.getDisplayName());
			}
		});
	}

	@Test
	public void process_callback_test() {
		ActivityDefinition workflow = new WorkflowDefinition("workflow").
				Sequence().
				Activity(new WriteLineDefinition()).
				Activity(new AssignDefinition()).
				End();

		DefinitionValidator validator = new DefinitionValidator();
		validator.addExtension(ProcessCallback.class, new ProcessCallback() {
			public void execute(ActivityDefinition definition, Activity activity) {
				if (definition.getParent() != null)
					System.out.print(definition.getParent().getDisplayName() + "-");
				System.out.println(definition.getDisplayName());
			}
		});
		workflow.toActivity(validator);

		System.err.println(validator.getErrors());
	}
}
