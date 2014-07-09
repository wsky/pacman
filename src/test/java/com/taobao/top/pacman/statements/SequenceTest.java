package com.taobao.top.pacman.statements;

import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.testsuite.StatementTestBase;

public class SequenceTest extends StatementTestBase {
	protected Activity createActivity() {
		Sequence sequence = new Sequence();
		sequence.getVariables().add(new Variable("var", 1));
		sequence.getActivities().add(new WriteLine());
		sequence.getActivities().add(new WriteLine());
		return sequence;
	}
	
	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
	
	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
	}
}
