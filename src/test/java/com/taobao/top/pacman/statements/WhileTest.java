package com.taobao.top.pacman.statements;

import static org.junit.Assert.*;

import java.util.Map;

import com.taobao.top.pacman.Activity;
import com.taobao.top.pacman.ActivityMetadata;
import com.taobao.top.pacman.NativeActivity;
import com.taobao.top.pacman.NativeActivityContext;
import com.taobao.top.pacman.OutArgument;
import com.taobao.top.pacman.RuntimeArgument;
import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.Trace;
import com.taobao.top.pacman.Variable;
import com.taobao.top.pacman.VariableValue;

public class WhileTest extends StatementTestBase {
	@Override
	protected Activity createActivity() {
		final Variable conditionVariable = new Variable("condition", true);
		final Variable countVariable = new Variable("count", 0);

		While while1 = new While();
		while1.getVariables().add(conditionVariable);
		while1.getVariables().add(countVariable);

		while1.Condition = new VariableValue(conditionVariable);
		while1.Body = new NativeActivity() {
			private OutArgument condition = new OutArgument(conditionVariable);
			private OutArgument count = new OutArgument(countVariable);

			@Override
			protected void execute(NativeActivityContext context) {
				int c = (Integer) count.get(context) + 1;
				count.set(context, c);

				assertTrue(c < 3);

				Trace.writeLine("while body execute " + c);

				if (c == 2)
					this.condition.set(context, false);
			}

			@Override
			protected void cacheMetadata(ActivityMetadata metadata) {
				metadata.bindAndAddArgument(this.condition, new RuntimeArgument("condition", Boolean.class, ArgumentDirection.Out));
				metadata.bindAndAddArgument(this.count, new RuntimeArgument("count", Boolean.class, ArgumentDirection.Out));
			}
		};

		return while1;
	}

	@Override
	protected Map<String, Object> createInputs() {
		return null;
	}
}
