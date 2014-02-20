package com.taobao.top.pacman.statements;

import java.util.HashMap;
import java.util.Map;

import com.taobao.top.pacman.Activity;

public class IfTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		If if1 = new If();
		if1.Then = new WriteLine("then");
		if1.Else = new WriteLine("else");
		return if1;
	}

	@Override
	protected Map<String, Object> createInputs() {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Condition", false);
		return inputs;
	}

}
