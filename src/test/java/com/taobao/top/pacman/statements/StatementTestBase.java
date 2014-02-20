package com.taobao.top.pacman.statements;

import java.util.Map;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityLocationReferenceEnvironment;
import com.taobao.top.pacman.ActivityUtilities;
import com.taobao.top.pacman.RenderProcessActivityCallback;
import com.taobao.top.pacman.WorkflowInstance;

public abstract class StatementTestBase {
	@Test
	public void metadata_test() {
		ActivityUtilities.cacheRootMetadata(this.createActivity(),
				new ActivityLocationReferenceEnvironment(null),
				new RenderProcessActivityCallback());
	}

	@Test
	public void invoke_test() throws Exception {
		WorkflowInstance.invoke(this.createActivity(), this.createInputs());
	}

	protected abstract Activity createActivity();

	protected abstract Map<String, Object> createInputs();
}
