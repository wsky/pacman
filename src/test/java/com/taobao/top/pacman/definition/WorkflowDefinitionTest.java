package com.taobao.top.pacman.definition;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.WorkflowInstance;

public class WorkflowDefinitionTest {
	@Test
	public void create_test() throws Exception {
		Activity workflow = WorkflowDefinition.create().
				in("arg").
				out("result").
				sequence().
				var("var").
				activity(new AssignDefinition().fromVar("arg").toVar("result")).
				If().
				condition().
				then(new WriteLineDefinition().fromVar("arg")).
				// in dynamic language
				// then()
				// writeLine().text("then").end().
				// endThen().
				Else(new WriteLineDefinition().text("else")).
				// writeLine().text("else").end().
				// endElse().
				endIf().
				writeLine().
				text("end").
				end().
				end().
				toActivity();
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("arg", "hello");
		Map<String, Object> outputs = WorkflowInstance.invoke(workflow, inputs);
		System.out.println(outputs);
	}
}
