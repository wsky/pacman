package com.taobao.top.pacman;

import java.util.HashMap;
import java.util.Map;

import org.junit.Test;

import com.taobao.top.pacman.RuntimeArgument.ArgumentDirection;
import com.taobao.top.pacman.statements.WriteLine;

public class ScheduleTest {
	@Test
	public void schedule() throws Exception {
		Map<String, Object> inputs = new HashMap<String, Object>();
		inputs.put("name", "test_name");
		Activity workflow = new Workflow();
		Map<String, Object> outputs = WorkflowInstance.invoke(workflow, inputs);
		System.out.println(inputs);
		System.out.println(outputs);
	}

	class Workflow extends NativeActivity {
		private InArgument name;
		private OutArgument result1;
		private OutArgument result2;
		private Variable var;
		private Activity body;

		public Workflow() {
			this.name = new InArgument();
			this.result1 = new OutArgument();
			this.result2 = new OutArgument();
			this.var = new Variable();
			WriteLine writeLine = new WriteLine();
			writeLine.Text = new InArgument(this.var);
			// writeLine.Text = new InArgument();
			this.body = writeLine;
		}

		@Override
		protected void execute(NativeActivityContext context) {
			this.var.set(context, this.name.get(context));
			this.result1.set(context, "1");
			this.result2.set(context, "2");
			// context.abort(new Exception("error"));
			context.scheduleActivity(this.body);
			// FIXME test callback
			// context.scheduleActivity(this.body, new CompletionCallback() {
			// @Override
			// public void execute(NativeActivityContext context, ActivityInstance completedInstance) {
			// result1.set(context, "1");
			// result2.set(context, "2");
			// }
			// });
		}

		@Override
		protected void cacheMetadata(ActivityMetadata metadata) {
			metadata.bindAndAddArgument(this.name, new RuntimeArgument("name", String.class, ArgumentDirection.In));
			metadata.bindAndAddArgument(this.result1, new RuntimeArgument("result1", String.class, ArgumentDirection.Out));
			metadata.bindAndAddArgument(this.result2, new RuntimeArgument("result2", String.class, ArgumentDirection.Out));
			metadata.addRuntimeVariable(this.var);
			metadata.addChild(this.body);
		}
	}
}
