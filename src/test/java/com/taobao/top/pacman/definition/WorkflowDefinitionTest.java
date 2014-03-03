package com.taobao.top.pacman.definition;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.WorkflowInstance;

public class WorkflowDefinitionTest {
	@Test
	public void create_test() throws Exception {
		Activity workflow = WorkflowDefinition.create().
				in("arg").
				out("result").
				var("var").
				sequence().
				activity(new AssignDefinition().fromVar("arg").toVar("var")).
				activity(new AssignDefinition().fromVar("var").toVar("result")).
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

	@Test(expected = IndexOutOfBoundsException.class)
	public void exception_test() throws Exception {
		Activity workflow = WorkflowDefinition.create().
				sequence().
				activity(new ActivityDefinition("error") {
					@Override
					public Activity toActivity() {
						return new NativeActivity() {
							@Override
							protected void execute(NativeActivityContext context) {
								throw new IndexOutOfBoundsException();
							}
						};
					}
				}).
				end().
				toActivity();
		Map<String, Object> outputs = WorkflowInstance.invoke(workflow, null);
		System.out.println(outputs);
		throw (IndexOutOfBoundsException) outputs.get("exception");
	}
}
