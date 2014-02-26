package com.taobao.top.pacman.statements;

import java.util.HashMap;
import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.OutArgument;

public class AssignTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		Assign assign = new Assign();
		assign.Value = new InArgument();
		assign.To = new OutArgument();
		return assign;
	}

	@Override
	protected Map<String, Object> createInputs() {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("Value", "assign_test");
		return inputs;
	}
}
