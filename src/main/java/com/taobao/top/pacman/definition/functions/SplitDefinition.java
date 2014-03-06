package com.taobao.top.pacman.definition.functions;

import com.taobao.top.pacman.ActivityContext;
import com.taobao.top.pacman.Function;
import com.taobao.top.pacman.InlinedFunctionValue;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.definition.ActivityDefinition;
import com.taobao.top.pacman.definition.DefinitionValidator;
import com.taobao.top.pacman.definition.InlinedFunctionDefinition;
import com.taobao.top.pacman.definition.VariableReferenceDefinition;

public class SplitDefinition extends InlinedFunctionDefinition {
	private VariableReferenceDefinition variable;
	private String separator;

	public SplitDefinition(VariableReferenceDefinition variable, String separator) {
		this.variable = variable;
		this.separator = separator;
	}

	@Override
	public InlinedFunctionValue toFunction(ActivityDefinition parent, DefinitionValidator validator) {
		if (this.separator == null)
			validator.addError("separator not set");

		final Variable variable = this.variable.toVariable(parent, validator);

		return new InlinedFunctionValue(new Function<ActivityContext, Object>() {
			@Override
			public Object execute(ActivityContext context) {
				Object value = variable.get(context);
				return value != null ? value.toString().split(separator) : "";
			}
		});
	}
}
