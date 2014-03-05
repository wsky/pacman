package com.taobao.top.pacman.definition;

import static org.junit.Assert.*;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.WorkflowInstance;

public class WorkflowDefinitionTest {
	@Test
	public void create_test() throws Exception {
		DefinitionValidator validator = new DefinitionValidator();
		Activity workflow = WorkflowDefinition.Create().
				In("arg").
				Out("result").
				Var("var").
				Sequence().
				Activity(new AssignDefinition().From("arg").To("var")).
				Activity(new AssignDefinition().From("var").To("result")).
				If().
				Condition().
				Then(new WriteLineDefinition().From("arg")).
				// in dynamic language
				// then()
				// writeLine().text("then").end().
				// endThen().
				Else(new WriteLineDefinition().Text("else")).
				// writeLine().text("else").end().
				// endElse().
				EndIf().
				WriteLine().
				Text("end").
				End().
				End().
				toActivity(validator);
		assertFalse(validator.hasAnyError());
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("arg", "hello");
		Map<String, Object> outputs = WorkflowInstance.invoke(workflow, inputs);
		System.out.println(outputs);
	}

	@Test
	public void validate_test() throws Exception {
		DefinitionValidator validator = new DefinitionValidator();
		WorkflowDefinition.Create().
				Sequence().
				Activity(new WriteLineDefinition()).
				Activity(new IfDefinition()).
				Activity(new AssignDefinition()).
				Activity(new AssignDefinition().From("var").To("var")).
				End().
				toActivity(validator);
		assertTrue(validator.hasAnyError());
		System.err.println(validator.getErrors());
		for (Entry<ActivityDefinition, List<String>> e : validator.getErrors().entrySet()) {
			System.err.println(e.getKey().getDisplayName());
			System.err.println(Arrays.toString(e.getValue().toArray()));
		}
	}
}
