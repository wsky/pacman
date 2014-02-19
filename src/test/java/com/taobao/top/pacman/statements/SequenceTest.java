package com.taobao.top.pacman.statements;

import org.junit.Test;

import com.taobao.top.pacman.ActivityLocationReferenceEnvironment;
import com.taobao.top.pacman.ActivityUtilities;
import com.taobao.top.pacman.RenderProcessActivityCallback;
import com.taobao.top.pacman.WorkflowInstance;

public class SequenceTest {
	@Test
	public void metadata_test() {
		ActivityUtilities.cacheRootMetadata(this.createSequence(),
				new ActivityLocationReferenceEnvironment(null),
				new RenderProcessActivityCallback());
	}

	@Test
	public void invoke_test() throws Exception {
		WorkflowInstance.invoke(this.createSequence(), null);
	}

	private Sequence createSequence() {
		Sequence sequence = new Sequence();
		sequence.getActivities().add(new WriteLine());
		sequence.getActivities().add(new WriteLine());
		return sequence;
	}
}
