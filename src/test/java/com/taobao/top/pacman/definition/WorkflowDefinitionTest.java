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
				toActivity();
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("arg", "hello");
		Map<String, Object> outputs = WorkflowInstance.invoke(workflow, inputs);
		System.out.println(outputs);
	}

	@Test(expected = IndexOutOfBoundsException.class)
	public void exception_test() throws Exception {
		Activity workflow = WorkflowDefinition.Create().
				Sequence().
				Activity(new ActivityDefinition("error") {
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
				End().
				toActivity();
		Map<String, Object> outputs = WorkflowInstance.invoke(workflow, null);
		System.out.println(outputs);
		throw (IndexOutOfBoundsException) outputs.get("exception");
	}
}
