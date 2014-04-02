package com.taobao.top.pacman.definition;

import static org.junit.Assert.*;

import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityLocationReferenceEnvironment;
import com.taobao.top.pacman.ActivityUtilities;
import com.taobao.top.pacman.RenderProcessActivityCallback;
import com.taobao.top.pacman.WorkflowInstance;
import com.taobao.top.pacman.definition.ActivityDefinition;
import com.taobao.top.pacman.definition.DefinitionValidator;

public abstract class DefinitionTestBase {	
	protected void testMetadata(ActivityDefinition definition) {
		this.testMetadata(definition, false);
	}

	protected void testMetadata(ActivityDefinition definition, boolean hasAnyError) {
		this.testMetadata(definition, hasAnyError, new DefinitionValidator());
	}

	protected void testMetadata(ActivityDefinition definition, boolean hasAnyError, DefinitionValidator validator) {
		Activity activity = definition.toActivity(validator);
		if (validator.hasAnyError())
			System.err.println(validator.getErrors());
		assertEquals(hasAnyError, validator.hasAnyError());
		if (hasAnyError)
			return;
		ActivityLocationReferenceEnvironment hostEnvironment = new ActivityLocationReferenceEnvironment(null);
		ActivityUtilities.cacheRootMetadata(activity, hostEnvironment, new RenderProcessActivityCallback());
	}

	protected Map<String, Object> invoke(ActivityDefinition definition, Map<String, Object> inputs) throws Exception {
		DefinitionValidator validator = new DefinitionValidator();
		Activity activity = definition.toActivity(validator);
		if (validator.hasAnyError()) {
			System.err.println(validator.getErrors());
			fail();
		}

		Map<String, Object> outputs = WorkflowInstance.invoke(activity, inputs);

		if (outputs.get("exception") != null) {
			((Exception) outputs.get("exception")).printStackTrace();
			fail();
		}

		System.out.println(outputs);
		return outputs;

	}
}
