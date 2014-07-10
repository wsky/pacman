package com.taobao.top.pacman.testsuite;

import java.util.Map;

import org.junit.Test;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityLocationReferenceEnvironment;
import com.taobao.top.pacman.ActivityUtilities;
import com.taobao.top.pacman.RenderProcessActivityCallback;
import com.taobao.top.pacman.Trace;
import com.taobao.top.pacman.WorkflowExtensionManager;
import com.taobao.top.pacman.WorkflowInstance;

public abstract class StatementTestBase {
	@Test
	public void metadata_test() throws Exception {
		ActivityUtilities.cacheRootMetadata(this.createActivity(),
				new ActivityLocationReferenceEnvironment(null),
				new RenderProcessActivityCallback());
	}
	
	@Test
	public void invoke_test() throws Exception {
		Map<String, Object> outputs = this.invoke(
				this.createActivity(),
				this.createInputs(),
				this.createExtensionManager());
		this.assertOutputs(outputs);
		System.out.println(outputs);
	}
	
	@Test
	public void base_perf_test() throws Exception {
		Trace.setEnabled(false);
		
		Activity activity = this.createActivity();
		Map<String, Object> inputs = this.createInputs();
		WorkflowExtensionManager extensionManager = this.createExtensionManager();
		
		// warmup
		for (int i = 0; i < 10000; i++)
			WorkflowInstance.invoke(activity, inputs, extensionManager);
		
		long total = 100000;
		long begin = System.currentTimeMillis();
		
		for (int i = 0; i < total; i++)
			WorkflowInstance.invoke(activity, inputs, extensionManager);
		
		long cost = System.currentTimeMillis() - begin;
		System.err.println(String.format(
				"total=%s|cost=%s|avg=%s",
				total, cost, ((float) total / (float) cost) * 1000));
		
		Trace.setEnabled(true);
	}
	
	protected Map<String, Object> invoke(Activity activity, Map<String, Object> inputs) throws Exception {
		return this.invoke(activity, inputs, null);
	}
	
	protected Map<String, Object> invoke(Activity activity,
			Map<String, Object> inputs,
			WorkflowExtensionManager extensionManager) throws Exception {
		return WorkflowInstance.invoke(activity, inputs, extensionManager);
	}
	
	protected abstract Activity createActivity();
	
	protected abstract Map<String, Object> createInputs();
	
	protected abstract void assertOutputs(Map<String, Object> outputs);
	
	protected WorkflowExtensionManager createExtensionManager() {
		return new WorkflowExtensionManager();
	}
}
