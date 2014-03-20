package com.taobao.top.pacman.statements;

import java.util.Map;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityLocationReferenceEnvironment;
import com.taobao.top.pacman.ActivityUtilities;
import com.taobao.top.pacman.RenderProcessActivityCallback;
import com.taobao.top.pacman.Trace;
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
		System.out.println(WorkflowInstance.invoke(this.createActivity(), this.createInputs()));
	}

	@Test
	public void base_perf_test() throws Exception {
		Trace.setEnabled(false);
		
		Activity activity = this.createActivity();
		Map<String, Object> inputs = this.createInputs();
		// warmup
		for (int i = 0; i < 10000; i++)
			WorkflowInstance.invoke(activity, inputs);

		long total = 100000;
		long begin = System.currentTimeMillis();

		for (int i = 0; i < total; i++)
			WorkflowInstance.invoke(activity, inputs);

		long cost = System.currentTimeMillis() - begin;
		System.err.println(String.format(
				"total=%s|cost=%s|avg=%s",
				total, cost, ((float) total / (float) cost) * 1000));

		Trace.setEnabled(true);
	}

	protected abstract Activity createActivity();

	protected abstract Map<String, Object> createInputs();
}
