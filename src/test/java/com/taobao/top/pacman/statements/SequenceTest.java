package com.taobao.top.pacman.statements;

import org.junit.Test;

import com.taobao.top.pacman.WorkflowInstance;

public class SequenceTest {
	@Test
	public void invoke_test() throws Exception {
		Sequence sequence = new Sequence();
		sequence.getActivities().add(new WriteLine());
		sequence.getActivities().add(new WriteLine());
		WorkflowInstance.invoke(sequence, null);
	}
}
