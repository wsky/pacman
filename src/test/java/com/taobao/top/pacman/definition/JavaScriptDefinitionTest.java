package com.taobao.top.pacman.definition;

import static org.junit.Assert.*;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;
import org.mozilla.javascript.Context;
import org.mozilla.javascript.Scriptable;
import org.mozilla.javascript.ScriptableObject;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.WorkflowInstance;
import com.taobao.top.pacman.definition.functions.SplitDefinition;

public class JavaScriptDefinitionTest {
	@Test
	public void parse_test() throws Exception {
		WorkflowDefinition workflow = new WorkflowDefinition();
		Context ctx = Context.enter();
		try {
			Scriptable scope = ctx.initStandardObjects();
			ScriptableObject.putProperty(scope, "def", new DefinitonScripting());
			ScriptableObject.putProperty(scope, "Var",
					ctx.compileFunction(scope, "function variable(n) { return def.variable(n); }", "func", 1, null));
			ScriptableObject.putProperty(scope, "Split",
					ctx.compileFunction(scope, "function split(v, s) { return def.split(v, s); }", "func", 1, null));
			ScriptableObject.putProperty(scope, "wf", workflow);
			ctx.evaluateString(scope,
					"wf." +
							"	In('arg')." +
							"	In('isThen')." +
							"	In('isWhile')." +
							"	Out('result')." +
							"	Sequence()." +
							"		WriteLine().Text('hello').End()." +
							"		WriteLine().Text('world').End()." +
							"		Assign().Value(Split(Var('arg'),',')).To(Var('result')).End()." +
							"		If().Condition(Var('isThen'))." +
							"			Then()." +
							"				WriteLine().Text('then').End()." +
							"			End()." +
							"			Else()." +
							"				WriteLine().Text('else').End()." +
							"			End()." +
							"		End()." +
							"		While().Condition(Var('isWhile'))." +
							"			Body()." +
							"				Assign().Value(false).To(Var('isWhile')).End()." +
							"			End()." +
							"		End()." +
							"		TryCatch()." +
							"			Try().WriteLine().Text('try').End().End()." +
							"			Catch().WriteLine().Text('catch').End().End()." +
							"			Finally().WriteLine().Text('finally').End().End()." +
							"		End()." +
							"	End()." +
							"End()",
					"DSL", 1, null);
		} finally {
			Context.exit();
		}

		DefinitionValidator validator = new DefinitionValidator();
		Activity activity = workflow.toActivity(validator);

		if (validator.hasAnyError()) {
			System.err.println(validator.getErrors());
			fail();
		}

		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("arg", "1,2,3");
		inputs.put("isThen", false);
		inputs.put("isWhile", true);
		Map<String, Object> outputs = WorkflowInstance.invoke(activity, inputs);
		System.out.println(outputs);

		Exception exception = (Exception) outputs.get("exception");
		if (exception != null) {
			exception.printStackTrace();
			fail();
		}

		assertArrayEquals(new String[] { "1", "2", "3" }, (String[]) outputs.get("result"));
	}

	public class DefinitonScripting {
		public VariableReferenceDefinition variable(String name) {
			return new VariableReferenceDefinition(name);
		}

		public InlinedFunctionDefinition split(VariableReferenceDefinition variable, String separator) {
			return new SplitDefinition(variable, separator);
		}
	}
}
