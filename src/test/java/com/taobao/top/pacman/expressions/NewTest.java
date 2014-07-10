package com.taobao.top.pacman.expressions;

import static org.junit.Assert.*;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.InArgument;
import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.statements.Assign;
import com.taobao.top.pacman.testsuite.StatementTestBase;

public class NewTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		Assign assign = new Assign();
		assign.Value = new InArgument(new New(Object.class));
		assign.To = new OutArgument();
		return assign;
	}
	
	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
	
	@Override
	protected void assertOutputs(Map<String, Object> outputs) {
		assertNotNull(outputs.get("To"));
	}
	
	@Test
	public void new_test() throws Exception {
		assertEquals(HashMap.class, this.invoke(new New(HashMap.class), null).get("Result").getClass());
		assertEquals(ArrayList.class, this.invoke(new New(ArrayList.class), null).get("Result").getClass());
		assertEquals(HashMap.class, this.invoke(new New(HashMap.class), null).get("Result").getClass());
	}
}
