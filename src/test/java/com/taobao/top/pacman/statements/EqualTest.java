package com.taobao.top.pacman.statements;

import static org.junit.Assert.*;

import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.testsuite.StatementTestBase;

public class EqualTest extends StatementTestBase {
	
	@Override
	protected Activity createActivity() {
		Equal equal = new Equal();
		equal.Left = new InArgument("hi");
		equal.Right = new InArgument("hi");
		return equal;
	}
	
	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
	
	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
		assertTrue((Boolean) outputs.get("Result"));
	}
}
