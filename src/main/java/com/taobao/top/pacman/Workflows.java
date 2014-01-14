package com.taobao.top.pacman;

import java.util.Map;

import com.taobao.top.pacman.hosting.WorkflowInstance;

public class Workflows {
	public static Activity create() {
		return null;
	}

	public static Map<String, Object> Invoke(Activity workflow, Map<String, Object> inputs) {
		return WorkflowInstance.invoke(workflow, inputs);
	}
}
