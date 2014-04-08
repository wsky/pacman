package com.taobao.top.pacman.definition;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

public class DefinitionValidator {
	private Map<ActivityDefinition, List<String>> errors;
	private ActivityDefinition current;
	private Map<Class<?>, Object> extensions;

	public void setCurrent(ActivityDefinition current) {
		this.current = current;
	}

	public Map<ActivityDefinition, List<String>> getErrors() {
		if (errors == null)
			this.errors = new HashMap<ActivityDefinition, List<String>>();
		return this.errors;
	}

	public boolean hasError() {
		return this.getErrors().get(current) != null;
	}

	public boolean hasAnyError() {
		return this.getErrors().size() > 0;
	}

	public void addError(String error) {
		List<String> errors = this.getErrors().get(this.current);
		if (errors == null)
			this.getErrors().put(this.current, errors = new ArrayList<String>());
		errors.add(error);
	}

	@SuppressWarnings("unchecked")
	public <T> T getExtension(Class<T> type) {
		return this.extensions != null ? (T) this.extensions.get(type) : null;
	}

	public <T> void addExtension(Class<T> type, T extension) {
		if (this.extensions == null)
			this.extensions = new HashMap<Class<?>, Object>();
		this.extensions.put(type, extension);
	}

}
