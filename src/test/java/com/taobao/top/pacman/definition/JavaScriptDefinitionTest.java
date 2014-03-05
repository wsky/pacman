package com.taobao.top.pacman.definition;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.taobao.top.pacman.WorkflowInstance;

public class JavaScriptDefinitionTest {
	@Test
	public void parse_test() throws Exception {
		WorkflowDefinition workflow = WorkflowDefinition.Create();
		Context ctx = Context.enter();
		try {
			Scriptable scope = ctx.initStandardObjects();
			ScriptableObject.putProperty(scope, "wf", workflow);
			ctx.evaluateString(scope,
					"wf." +
							"	In('arg')." +
							"	In('isThen')." +
							"	Out('result')." +
							"	Sequence()." +
							"		WriteLine().Text('hello').End()." +
							"		WriteLine().Text('world').End()." +
							"		Assign().From('arg').To('result').End()." +
							"		If().Condition('isThen')." +
							"			Then()." +
							"				WriteLine().Text('then').End()." +
							"			End()." +
							"			Else()." +
							"				WriteLine().Text('else').End()." +
							"			End()." +
							"		End()." +
							"	End()." +
							"End()",
					"DSL", 1, null);
		} finally {
			Context.exit();
		}

		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("arg", "test");
		inputs.put("isThen", false);
		Map<String, Object> outputs = WorkflowInstance.invoke(workflow.toActivity(new DefinitionValidator()), inputs);
		System.out.println(outputs);
	}
}
